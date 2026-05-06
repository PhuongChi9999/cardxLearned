import { Component, OnInit, signal } from '@angular/core';
import { DossierService } from '../../services/dossier.service';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-dossiers',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './dossiers.html',
  styleUrl: './dossiers.scss'
})
export class Dossiers implements OnInit {

  dossiers = signal<any[]>([]);

  constructor(private dossierService: DossierService) {}

  ngOnInit(): void {
    this.loadDossiers();
  }

  loadDossiers(): void {
    this.dossierService.getAll()
      .subscribe(data => {
        console.log('Dossiers reçus:', data);
        this.dossiers.set(data);
      });
  }
}
