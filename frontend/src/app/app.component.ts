import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { SidebarComponent } from './shared/components/sidebar/sidebar.component';
import { ThemeService } from './core/services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, SidebarComponent],
  template: `
    <div class="min-h-screen bg-gray-50 dark:bg-dark-bg transition-colors duration-200">
      <app-navbar *ngIf="isLoggedIn"></app-navbar>
      <div class="flex" *ngIf="isLoggedIn">
        <app-sidebar class="hidden lg:block"></app-sidebar>
        <main class="flex-1 ml-0 lg:ml-64 pt-16 min-h-screen overflow-y-auto">
          <div class="p-6">
            <router-outlet></router-outlet>
          </div>
        </main>
      </div>
      <div *ngIf="!isLoggedIn">
        <router-outlet></router-outlet>
      </div>
    </div>
  `,
})
export class AppComponent implements OnInit {
  isLoggedIn = true; // Will be controlled by auth service

  constructor(private themeService: ThemeService) {}

  ngOnInit() {
    this.themeService.initTheme();
  }
}
