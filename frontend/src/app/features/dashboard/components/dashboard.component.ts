import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { DashboardService, DashboardStats, Prediction, Alert } from '../../../core/services/dashboard.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <!-- Header -->
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Dashboard</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Real-time infrastructure health overview</p>
        </div>
        <div class="flex items-center gap-3">
          <span class="text-xs text-gray-400 dark:text-gray-500 flex items-center gap-1">
            <span class="w-1.5 h-1.5 rounded-full bg-success-500 animate-pulse"></span>
            Live
          </span>
          <span class="text-xs text-gray-400 dark:text-gray-500">{{ currentTime }}</span>
        </div>
      </div>

      <!-- KPI Cards -->
      <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4" *ngIf="stats">
        <div class="stat-card">
          <div class="stat-icon bg-primary-50 dark:bg-primary-900/30 text-primary-600 dark:text-primary-400">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M19 21V5a2 2 0 00-2-2H7a2 2 0 00-2 2v16m14 0h2m-2 0h-5m-9 0H3m2 0h5M9 7h1m-1 4h1m4-4h1m-1 4h1m-5 10v-5a1 1 0 011-1h2a1 1 0 011 1v5"/>
            </svg>
          </div>
          <div>
            <p class="stat-value">{{ stats.totalAssets | number }}</p>
            <p class="stat-label">Total Assets</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon bg-success-50 dark:bg-green-900/30 text-success-600 dark:text-green-400">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div>
            <p class="stat-value">{{ stats.avgHealthScore }}<span class="text-sm font-normal text-gray-400">/100</span></p>
            <p class="stat-label">Avg Health Score</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon bg-warning-50 dark:bg-amber-900/30 text-warning-600 dark:text-amber-400">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z"/>
            </svg>
          </div>
          <div>
            <p class="stat-value">{{ stats.activeSensors | number }}</p>
            <p class="stat-label">Active Sensors</p>
          </div>
        </div>

        <div class="stat-card">
          <div class="stat-icon bg-danger-50 dark:bg-red-900/30 text-danger-600 dark:text-red-400">
            <svg class="w-6 h-6" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 8c-1.657 0-3 .895-3 2s1.343 2 3 2 3 .895 3 2-1.343 2-3 2m0-8c1.11 0 2.08.402 2.599 1M12 8V7m0 1v8m0 0v1m0-1c-1.11 0-2.08-.402-2.599-1M21 12a9 9 0 11-18 0 9 9 0 0118 0z"/>
            </svg>
          </div>
          <div>
            <p class="stat-value">${{ stats.costAvoidance | number }}</p>
            <p class="stat-label">Cost Avoidance</p>
          </div>
        </div>
      </div>

      <!-- Charts Row -->
      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6" *ngIf="stats">
        <!-- Health Trend Chart -->
        <div class="chart-container lg:col-span-2">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Infrastructure Health Trend</h3>
          <div class="h-64 flex items-end gap-2">
            <div *ngFor="let point of stats.healthTrend; let i = index" 
                 class="flex-1 flex flex-col items-center gap-1 group"
                 [title]="point.date + ': Score ' + point.score">
              <div class="w-full flex flex-col gap-1">
                <div class="w-full bg-danger-500/80 rounded-t" [style.height.px]="point.critical * 6"></div>
                <div class="w-full bg-warning-500/80 rounded-t" [style.height.px]="point.warning * 2"></div>
                <div class="w-full bg-success-500/80 rounded-t" [style.height.px]="point.score * 1.5"></div>
              </div>
              <span class="text-[10px] text-gray-400 dark:text-gray-500">{{ point.date }}</span>
            </div>
          </div>
          <div class="flex items-center gap-4 mt-4 text-xs">
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded bg-success-500"></span>Healthy</span>
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded bg-warning-500"></span>Warning</span>
            <span class="flex items-center gap-1"><span class="w-2 h-2 rounded bg-danger-500"></span>Critical</span>
          </div>
        </div>

        <!-- Alert Breakdown -->
        <div class="chart-container">
          <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Active Alerts</h3>
          <div class="space-y-3" *ngIf="stats">
            <div class="flex items-center justify-between p-3 bg-danger-50 dark:bg-red-900/20 rounded-lg">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-danger-500"></span>
                <span class="text-sm text-gray-700 dark:text-gray-300">Critical</span>
              </div>
              <span class="text-lg font-bold text-danger-600 dark:text-red-400">{{ stats.alertBreakdown.critical }}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-warning-50 dark:bg-amber-900/20 rounded-lg">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-warning-500"></span>
                <span class="text-sm text-gray-700 dark:text-gray-300">High</span>
              </div>
              <span class="text-lg font-bold text-warning-600 dark:text-amber-400">{{ stats.alertBreakdown.high }}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-primary-50 dark:bg-primary-900/20 rounded-lg">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-primary-500"></span>
                <span class="text-sm text-gray-700 dark:text-gray-300">Medium</span>
              </div>
              <span class="text-lg font-bold text-primary-600 dark:text-primary-400">{{ stats.alertBreakdown.medium }}</span>
            </div>
            <div class="flex items-center justify-between p-3 bg-gray-100 dark:bg-gray-800 rounded-lg">
              <div class="flex items-center gap-2">
                <span class="w-2 h-2 rounded-full bg-gray-400"></span>
                <span class="text-sm text-gray-700 dark:text-gray-300">Low</span>
              </div>
              <span class="text-lg font-bold text-gray-600 dark:text-gray-400">{{ stats.alertBreakdown.low }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- Tables Row -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6" *ngIf="stats">
        <!-- Recent Predictions -->
        <div class="table-container">
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-dark-border">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Recent Predictions</h3>
            <span class="badge badge-warning">{{ stats.predictionsToday }} today</span>
          </div>
          <table class="data-table">
            <thead>
              <tr>
                <th>Asset</th>
                <th>Type</th>
                <th>Probability</th>
                <th>Time</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngFor="let pred of stats.recentPredictions">
                <td class="font-medium">{{ pred.assetName }}</td>
                <td>
                  <span class="badge" [class.badge-critical]="pred.severity === 'critical'"
                        [class.badge-warning]="pred.severity === 'high' || pred.severity === 'medium'"
                        [class.badge-success]="pred.severity === 'low'">
                    {{ pred.assetType.replace('_', ' ') }}
                  </span>
                </td>
                <td>
                  <div class="flex items-center gap-2">
                    <div class="w-16 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                      <div class="h-full rounded-full" 
                           [class.bg-danger-500]="pred.probability > 0.8"
                           [class.bg-warning-500]="pred.probability <= 0.8 && pred.probability > 0.5"
                           [class.bg-success-500]="pred.probability <= 0.5"
                           [style.width.%]="pred.probability * 100"></div>
                    </div>
                    <span class="text-sm font-medium">{{ pred.probability | percent:'1.0-0' }}</span>
                  </div>
                </td>
                <td class="text-gray-500 dark:text-gray-400">{{ pred.time }}</td>
              </tr>
            </tbody>
          </table>
        </div>

        <!-- Recent Alerts -->
        <div class="table-container">
          <div class="flex items-center justify-between p-4 border-b border-gray-200 dark:border-dark-border">
            <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Recent Alerts</h3>
            <span class="badge badge-critical" *ngIf="stats.activeAlerts > 0">{{ stats.activeAlerts }} active</span>
          </div>
          <div class="divide-y divide-gray-100 dark:divide-dark-border">
            <div *ngFor="let alert of stats.recentAlerts" class="p-4 flex items-start gap-3 hover:bg-gray-50 dark:hover:bg-gray-800/30 transition-colors">
              <div class="mt-0.5" [class.text-danger-500]="alert.severity === 'critical'"
                   [class.text-warning-500]="alert.severity === 'high' || alert.severity === 'medium'">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"/>
                </svg>
              </div>
              <div class="flex-1 min-w-0">
                <p class="text-sm font-medium text-gray-900 dark:text-white truncate">{{ alert.title }}</p>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-0.5">{{ alert.assetName }}</p>
              </div>
              <div class="text-right flex-shrink-0">
                <span class="badge text-xs" [class.badge-critical]="alert.severity === 'critical'"
                      [class.badge-warning]="alert.severity === 'high' || alert.severity === 'medium'">{{ alert.status }}</span>
                <p class="text-xs text-gray-400 dark:text-gray-500 mt-1">{{ alert.time }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class DashboardComponent implements OnInit {
  stats: DashboardStats | null = null;
  currentTime = new Date().toLocaleTimeString();

  constructor(private dashboardService: DashboardService) {}

  ngOnInit() {
    this.dashboardService.getDashboardStats().subscribe(stats => {
      this.stats = stats;
    });
    setInterval(() => {
      this.currentTime = new Date().toLocaleTimeString();
    }, 1000);
  }
}
