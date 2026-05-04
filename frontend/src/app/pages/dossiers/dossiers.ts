import { Component, OnInit, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dossiers',
  standalone: true,
  templateUrl: './dossiers.html',
  styleUrl: './dossiers.scss'
})
export class Dossiers implements OnInit {

  dossiers = signal<any[]>([]);

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.loadDossiers();
  }

  loadDossiers(): void {
    this.http.get<any[]>('http://localhost:8080/api/dossiers')
      .subscribe(data => {
          console.log('Dossiers reçus:', data);
          this.dossiers.set(data);
        });
  }
}
