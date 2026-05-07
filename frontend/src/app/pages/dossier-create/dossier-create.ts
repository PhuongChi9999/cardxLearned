import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { DossierService } from '../../services/dossier.service';

@Component({
  selector: 'app-dossier-create',
  imports: [FormsModule],
  templateUrl: './dossier-create.html',
  styleUrl: './dossier-create.scss',
})
export class DossierCreateComponent {
  nom: string = '';
  email: string = '';
  phone: string = '';

  constructor(
    private dossierService: DossierService,
    private router: Router
  ) {}

  create() {
    const dossier = {
      clientName: this.nom,
      email: this.email,
      phone: this.phone
    };

    this.dossierService.create(dossier).subscribe({
      next: () => {
        alert('Dossier créé !');
        this.router.navigate(['/dossiers']);
      },
      error: (err) => {
        console.error(err);
        alert('Erreur création');
      }
    });
  }
}
