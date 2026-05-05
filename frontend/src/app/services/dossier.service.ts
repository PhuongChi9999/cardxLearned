import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Dossier {
  id?: number;
  clientName: string;
  email?: string;
  phone?: string;
  status?: string;
}

@Injectable({
  providedIn: 'root'
})
export class DossierService {

  //private apiUrl = '/api/dossiers';
  private apiUrl = 'http://localhost:8080/api/dossiers';

  constructor(private http: HttpClient) {}

  create(dossier: Dossier): Observable<Dossier> {
    return this.http.post<Dossier>(this.apiUrl, dossier);
  }

  getAll(): Observable<Dossier[]> {
    return this.http.get<Dossier[]>(this.apiUrl);
  }
}
