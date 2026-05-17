import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ExtractionResult {
  id: number;
  status?: string;
  extractedData: any;
}

export interface DocumentItem {
  id: number;
  filename: string;
  fileType: string;
  uploadedAt: string;
  extractionResult?: ExtractionResult;
}

export interface Dossier {
  id?: number;
  clientName: string;
  email?: string;
  phone?: string;
  status?: string;
  createdAt?: string;
  documents?: DocumentItem[];
}

@Injectable({
  providedIn: 'root'
})
export class DossierService {

  private apiUrl = '/api/dossiers';
  //private apiUrl = 'http://localhost:8080/api/dossiers';

  constructor(private http: HttpClient) {}

  create(dossier: Dossier): Observable<Dossier> {
    return this.http.post<Dossier>(this.apiUrl, dossier);
  }

  getAll(): Observable<Dossier[]> {
    return this.http.get<Dossier[]>(this.apiUrl);
  }

  getById(id: string | number): Observable<Dossier> {
    return this.http.get<Dossier>(`${this.apiUrl}/${id}`);
  }

  uploadDocument(dossierId: string | number, file: File): Observable<any> {
    const formData = new FormData();

    formData.append('file', file);
    formData.append('dossierId', dossierId.toString());

    return this.http.post('/api/documents/upload', formData);
  }
  deleteDocument(id: number): Observable<void> {
    return this.http.delete<void>(`/api/documents/${id}`);
  }
}
