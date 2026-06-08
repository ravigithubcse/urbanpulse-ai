import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-primary-50 via-white to-primary-100 dark:from-dark-bg dark:via-dark-bg dark:to-primary-900/20 px-4">
      <div class="w-full max-w-md">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl flex items-center justify-center mx-auto mb-4 shadow-lg">
            <svg class="w-9 h-9 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">UrbanPulse AI</h1>
          <p class="text-gray-500 dark:text-gray-400 mt-1">Predictive Urban Infrastructure Intelligence</p>
        </div>

        <div class="card bg-white/80 dark:bg-dark-card/80 backdrop-blur-lg">
          <h2 class="text-xl font-semibold text-gray-900 dark:text-white mb-6">Sign In</h2>

          <form (ngSubmit)="onSubmit()" class="space-y-4">
            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Email</label>
              <input
                type="email"
                [(ngModel)]="email"
                name="email"
                class="input-field"
                placeholder="admin@urbanpulse.ai"
                required
              />
            </div>

            <div>
              <label class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">Password</label>
              <input
                type="password"
                [(ngModel)]="password"
                name="password"
                class="input-field"
                placeholder="••••••••"
                required
              />
            </div>

            <div class="flex items-center justify-between text-sm">
              <label class="flex items-center gap-2 text-gray-600 dark:text-gray-400">
                <input type="checkbox" class="rounded border-gray-300" />
                Remember me
              </label>
              <a href="#" class="text-primary-600 hover:text-primary-700">Forgot password?</a>
            </div>

            <button type="submit" class="btn-primary w-full flex items-center justify-center gap-2">
              <span>Sign In</span>
              <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"/>
              </svg>
            </button>
          </form>

          <div class="mt-6 pt-4 border-t border-gray-200 dark:border-dark-border text-center">
            <p class="text-sm text-gray-500 dark:text-gray-400">
              Default: admin@urbanpulse.ai / UrbanPulse@2024
            </p>
          </div>
        </div>

        <p class="text-center text-xs text-gray-400 dark:text-gray-600 mt-6">
          UrbanPulse AI v1.0.0 - Protected by enterprise-grade security
        </p>
      </div>
    </div>
  `,
})
export class LoginComponent {
  email = 'admin@urbanpulse.ai';
  password = '';

  constructor(private router: Router) {}

  onSubmit() {
    localStorage.setItem('access_token', 'demo_token');
    this.router.navigate(['/dashboard']);
  }
}
