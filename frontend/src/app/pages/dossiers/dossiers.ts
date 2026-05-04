import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-dossiers',
  standalone: true,
  imports: [],
  templateUrl: './dossiers.html',
  styleUrl: './dossiers.scss'
})
export class Dossiers implements OnInit {

  dossiers: any[] = [];

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get<any[]>('http://localhost:8080/api/dossiers')
      .subscribe(data => {
        console.log("DATA:", data);
        this.dossiers = data;
      });
  }
}
