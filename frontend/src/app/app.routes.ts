import { Routes } from '@angular/router';

import { MainLayout } from './features/layout/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    component: MainLayout,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'dashboard',
        pathMatch: 'full',
      },
      {
        path: 'dashboard',
        loadComponent: () =>
          import('./features/dashboard/dashboard/dashboard').then(
            (m) => m.Dashboard
          ),
      },
      {
        path: 'gastos',
        loadComponent: () =>
          import('./features/finanzas/finanzas').then((m) => m.Finanzas),
      },
      {
        path: 'perfil',
        loadComponent: () =>
          import('./features/perfil/perfil').then((m) => m.Perfil),
      },
    ],
  },
  {
    path: 'crear-viaje',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/viajes/crear-viaje/crear-viaje').then(
        (m) => m.CrearViaje
      ),
  },
  {
    path: 'viajes/:id',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/viajes/detalle-viaje/detalle-viaje').then(
        (m) => m.DetalleViaje
      ),
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login').then((m) => m.Login),
  },
  {
    path: 'registro',
    loadComponent: () =>
      import('./features/auth/registro/registro').then((m) => m.Registro),
  },
];