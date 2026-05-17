import { Component, OnDestroy, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { interval, Subscription } from 'rxjs';

import { Dossier, DossierService } from '../../services/dossier.service';

@Component({
  selector: 'app-dossier-detail',
  standalone: true,
  imports: [DatePipe],
  templateUrl: './dossier-detail.html',
  styleUrl: './dossier-detail.scss',
})
export class DossierDetail implements OnDestroy {
  id: string | null = null;

  dossier = signal<Dossier | null>(null);
  selectedFile: File | null = null;

  message = signal('');
  uploadResult = signal<any>(null);
  selectedDocument = signal<any>(null);


  private pollingSubscription?: Subscription;

  constructor(
    private route: ActivatedRoute,
    private dossierService: DossierService
  ) {
    this.id = this.route.snapshot.paramMap.get('id');

    if (this.id) {
      this.loadDossier(this.id);
    }
  }

  loadDossier(id: string): void {
    this.dossierService.getById(id).subscribe({
      next: (data) => {
        console.log('DETAIL:', data);
        this.dossier.set(data);
        this.startPollingIfNeeded();
      },
      error: (err) => {
        console.error('Erreur détail dossier:', err);
        this.message.set('Erreur chargement dossier');
        this.stopPolling();
      },
    });
  }

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    const maxSizeMb = 10;
    const maxSizeBytes = maxSizeMb * 1024 * 1024;

    const allowedTypes = ['application/pdf', 'image/jpeg', 'image/png'];

    if (!allowedTypes.includes(file.type)) {
      this.selectedFile = null;
      this.message.set('Seuls les fichiers PDF, JPG et PNG sont acceptés');
      input.value = '';
      return;
    }

    if (file.size > maxSizeBytes) {
      this.selectedFile = null;
      this.message.set(`Le fichier ne doit pas dépasser ${maxSizeMb} MB`);
      input.value = '';
      return;
    }

    this.selectedFile = file;
    this.message.set('');
    this.uploadResult.set(null);
    this.selectedDocument.set(null);
  }

  upload(): void {
    if (!this.id) {
      this.message.set('Erreur: dossier introuvable');
      return;
    }

    if (!this.selectedFile) {
      this.message.set('Veuillez sélectionner un fichier');
      return;
    }

    this.message.set('Envoi du document en cours...');
    this.uploadResult.set(null);

    this.dossierService.uploadDocument(this.id, this.selectedFile).subscribe({
      next: (response) => {
        console.log('UPLOAD RESPONSE:', response);

        this.uploadResult.set(response);
        this.selectedDocument.set(response);
        this.selectedFile = null;
        this.message.set('Document envoyé. Extraction en cours...');

        this.loadDossier(this.id!);
      },
      error: (err) => {
        console.error('Erreur upload:', err);
        this.message.set('Erreur lors de l’envoi du document');
      },
    });
  }

  hasPendingExtraction(): boolean {
    const currentDossier = this.dossier();
    const documents = currentDossier?.documents;

    if (!documents || documents.length === 0) {
      return false;
    }

    return documents.some((document) => {
      const status = document.extractionResult?.status;
      return status === 'PENDING' || status === 'PROCESSING';
    });
  }

  startPollingIfNeeded(): void {
    if (!this.hasPendingExtraction()) {
      this.stopPolling();
      return;
    }

    if (this.pollingSubscription) {
      return;
    }

    this.pollingSubscription = interval(2000).subscribe(() => {
      if (!this.id) {
        this.stopPolling();
        return;
      }

      this.dossierService.getById(this.id).subscribe({
        next: (data) => {
          console.log('POLLING DETAIL:', data);
          this.dossier.set(data);

          if (!this.hasPendingExtraction()) {
            const last = this.lastDocument();
            this.uploadResult.set(last);
            this.selectedDocument.set(last);

            this.message.set('Extraction terminée');
            this.stopPolling();
          }
        },
        error: (err) => {
          console.error('Erreur polling dossier:', err);
          this.stopPolling();
        },
      });
    });
  }

  stopPolling(): void {
    this.pollingSubscription?.unsubscribe();
    this.pollingSubscription = undefined;
  }

  ngOnDestroy(): void {
    this.stopPolling();
  }

  lastDocument() {
    const documents = this.dossier()?.documents;

    if (!documents || documents.length === 0) {
      return null;
    }

    return documents[documents.length - 1];
  }

  displayedDocument() {
    return this.selectedDocument() ?? this.lastDocument();
  }

  selectDocument(document: any): void {
    this.selectedDocument.set(document);
  }

  deleteDocument(documentId: number): void {
    const confirmed = confirm('Voulez-vous vraiment supprimer ce document ?');

    if (!confirmed) {
      return;
    }

    this.dossierService.deleteDocument(documentId).subscribe({
      next: () => {
        this.message.set('Document supprimé');

        if (this.selectedDocument()?.id === documentId) {
          this.selectedDocument.set(null);
        }

        this.stopPolling();

        if (this.id) {
          this.loadDossier(this.id);
        }
      },
      error: (err) => {
        console.error('Erreur suppression document:', err);
        this.message.set('Erreur lors de la suppression du document');
      },
    });
  }

  getStatusClass(status?: string): string {
    switch (status) {
      case 'PENDING':
        return 'badge badge-pending';
      case 'PROCESSING':
        return 'badge badge-processing';
      case 'COMPLETED':
        return 'badge badge-completed';
      case 'FAILED':
        return 'badge badge-failed';
      default:
        return 'badge badge-old';
    }
  }

  isProcessing(status?: string): boolean {
    return status === 'PENDING' || status === 'PROCESSING';
  }

  getExtractionLabel(status?: string): string {
    switch (status) {
      case 'PENDING':
        return 'En attente';

      case 'PROCESSING':
        return 'Traitement en cours';

      case 'COMPLETED':
        return 'Terminé';

      case 'FAILED':
        return 'Erreur';

      default:
        return 'Ancien document';
    }
  }
}
