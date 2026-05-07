import { Component, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { Dossier, DossierService } from '../../services/dossier.service';

@Component({
  selector: 'app-dossier-detail',
  standalone: true,
  templateUrl: './dossier-detail.html',
  styleUrl: './dossier-detail.scss',
})
export class DossierDetail {
  id: string | null = null;

  dossier = signal<Dossier | null>(null);
  selectedFile: File | null = null;
  message = signal('');

  constructor(
    private route: ActivatedRoute,
    private dossierService: DossierService
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
    }
  }

  upload() {
    this.message.set('Upload document sera traité dans l’étape suivante');
  }
}
