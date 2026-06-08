import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/components/login/login.component').then(m => m.LoginComponent),
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./features/dashboard/components/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'map',
    loadComponent: () => import('./features/map/components/map.component').then(m => m.MapComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'assets',
    loadComponent: () => import('./features/assets/components/assets.component').then(m => m.AssetsComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'predictions',
    loadComponent: () => import('./features/predictions/components/predictions.component').then(m => m.PredictionsComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'alerts',
    loadComponent: () => import('./features/alerts/components/alerts.component').then(m => m.AlertsComponent),
    canActivate: [AuthGuard],
  },
  {
    path: 'analytics',
    loadComponent: () => import('./features/analytics/components/analytics.component').then(m => m.AnalyticsComponent),
    canActivate: [AuthGuard],
  },
  { path: '**', redirectTo: '/dashboard' },
];
