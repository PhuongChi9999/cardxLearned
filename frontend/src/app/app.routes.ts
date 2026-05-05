import { Routes } from '@angular/router';
import { DossierCreateComponent } from './pages/dossier-create/dossier-create';
import { DossierDetail } from './pages/dossier-detail/dossier-detail';

export const routes: Routes = [
  {
    path: 'dossiers',
    loadComponent: () =>
      import('./pages/dossiers/dossiers').then(m => m.Dossiers)
  },
  {
    path: 'dossiers/new',
    component: DossierCreateComponent
  },
  {
    path: 'dossiers/:id',
    component: DossierDetail
  },
  {
    path: '',
    redirectTo: 'dossiers',
    pathMatch: 'full'
  }
];
