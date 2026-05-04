import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App {
  protected readonly title = signal('frontend');

  constructor(private http: HttpClient) {
    
  }

  createDossier() {
    const body = {
      clientName: 'Test Client',
      email: 'aa@gmail.com',
      phone: '123456789'
    };

    this.http.post('http://localhost:8080/api/dossiers', body).subscribe({
      next: (res) => console.log('Dossier créé:', res),
      error: (err) => console.error('Erreur création dossier:', err)
    });
  }
}
