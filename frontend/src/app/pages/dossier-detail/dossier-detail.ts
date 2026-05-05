import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dossier-detail',
  standalone: true,
  templateUrl: './dossier-detail.html',
  styleUrl: './dossier-detail.scss',
})
export class DossierDetail {

  id: string | null = null;
  dossier: any = null;

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient
  ) {
    this.id = this.route.snapshot.paramMap.get('id');

    this.loadDossier();
  }

  loadDossier() {
    this.http.get(`http://localhost:8080/api/dossiers/${this.id}`)
      .subscribe(data => {
        console.log('DETAIL:', data);
        this.dossier = data;
      });
  }

  selectedFile: File | null = null;
  message: string = '';

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
  }

  upload() {
    if (!this.selectedFile) {
      alert('Choisir un fichier');
      return;
    }

    const formData = new FormData();
    formData.append('file', this.selectedFile);
    formData.append('dossierId', this.id!);

    this.http.post('http://localhost:8080/api/documents/upload', formData)
      .subscribe({
        next: (doc: any) => {
          console.log('Uploaded:', doc);

          // gọi extract
          this.extract(doc.id);
        },
        error: () => {
          alert('Erreur upload');
        }
      });
  }

  extract(documentId: number) {
    this.message = 'Analyse en cours...';

    this.http.post(`http://localhost:8080/api/documents/${documentId}/extract`, {})
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
    this.http.get(`http://localhost:8080/api/documents/${documentId}/result`)
      .subscribe({
        next: (res: any) => {
          console.log('RESULT:', res);
          this.message = JSON.stringify(res, null, 2);
        },
        error: (err) => {
          console.error('Erreur résultat:', err);
          alert('Erreur récupération résultat');
        }
      });
  }

}
