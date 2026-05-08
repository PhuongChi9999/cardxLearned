import { Component, signal } from '@angular/core';
import { JsonPipe, DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

import { Dossier, DossierService } from '../../services/dossier.service';

@Component({
  selector: 'app-dossier-detail',
  standalone: true,
  imports: [JsonPipe, DatePipe],
  templateUrl: './dossier-detail.html',
  styleUrl: './dossier-detail.scss',
})
export class DossierDetail {
  id: string | null = null;

  dossier = signal<Dossier | null>(null);
  selectedFile: File | null = null;

  message = signal('');
  uploadResult = signal<any>(null);

  constructor(
    private route: ActivatedRoute,
    private dossierService: DossierService,
    private http: HttpClient
  ) {
    this.id = this.route.snapshot.paramMap.get('id');

    if (this.id) {
      this.loadDossier(this.id);
    }
  }

  loadDossier(id: string) {
    this.dossierService.getById(id).subscribe({
      next: (data) => {
        console.log('DETAIL:', data);
        this.dossier.set(data);
      },
      error: (err) => {
        console.error('Erreur détail dossier:', err);
        this.message.set('Erreur chargement dossier');
      }
    });
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
      this.message.set('');
      this.uploadResult.set(null);
    }
  }

  upload() {
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
        this.message.set('Document envoyé avec succès');
        this.loadDossier(this.id!);
      },
      error: (err) => {
        console.error('Erreur upload:', err);
        this.message.set('Erreur lors de l’envoi du document');
      }
    });
  }

  extract(documentId: number) {
    this.message.set('Analyse en cours...');

    this.http.post(`/api/documents/${documentId}/extract`, {})
      .subscribe({
        next: () => {
          this.getResult(documentId);
        },
        error: (err) => {
          console.error('Erreur analyse:', err);
          alert('Erreur analyse');
        }
      });
  }

  getResult(documentId: number) {
    this.http.get(`/api/documents/${documentId}/result`)
      .subscribe({
        next: (res: any) => {
          console.log('RESULT:', res);
           this.message.set(JSON.stringify(res, null, 2));
        },
        error: (err) => {
          console.error('Erreur résultat:', err);
          alert('Erreur récupération résultat');
        }
      });
  }

}
