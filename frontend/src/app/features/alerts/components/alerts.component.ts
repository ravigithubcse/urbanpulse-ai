import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Alert {
  id: string;
  title: string;
  description: string;
  severity: string;
  status: string;
  assetName: string;
  assetType: string;
  createdAt: string;
  acknowledgedBy?: string;
  predictedFailure?: string;
}

@Component({
  selector: 'app-alerts',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Alerts</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ activeAlerts.length }} active alerts requiring attention</p>
        </div>
        <div class="flex items-center gap-3">
          <select [(ngModel)]="filterSeverity" (change)="filterAlerts()" class="input-field text-sm w-36">
            <option value="">All Severities</option>
            <option value="CRITICAL">Critical</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
          <button class="btn-primary text-sm">Create Alert Rule</button>
        </div>
      </div>

      <!-- Alerts List -->
      <div class="space-y-3">
        <div *ngFor="let alert of filteredAlerts" 
             class="card hover:shadow-md transition-shadow"
             [class.border-l-4]="true"
             [class.border-l-danger-500]="alert.severity === 'CRITICAL'"
             [class.border-l-warning-500]="alert.severity === 'HIGH'"
             [class.border-l-primary-500]="alert.severity === 'MEDIUM'">
          <div class="flex flex-col md:flex-row md:items-start md:justify-between gap-4">
            <div class="flex-1">
              <div class="flex items-center gap-3">
                <span class="badge" [class.badge-critical]="alert.severity === 'CRITICAL'"
                      [class.badge-warning]="alert.severity === 'HIGH'"
                      [class.badge-success]="alert.severity === 'MEDIUM' || alert.severity === 'LOW'">{{ alert.severity }}</span>
                <span class="badge bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-300">{{ alert.status }}</span>
              </div>
              <h4 class="font-semibold text-gray-900 dark:text-white mt-2">{{ alert.title }}</h4>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ alert.description }}</p>
              <div class="flex items-center gap-4 mt-2 text-xs text-gray-500 dark:text-gray-400">
                <span>{{ alert.assetName }}</span>
                <span>{{ alert.assetType.replace('_', ' ') }}</span>
                <span>{{ alert.createdAt }}</span>
                <span *ngIf="alert.predictedFailure" class="text-danger-500">Predicted: {{ alert.predictedFailure }}</span>
              </div>
            </div>
            <div class="flex items-center gap-2">
              <button *ngIf="alert.status === 'ACTIVE'" class="btn-primary text-sm">Acknowledge</button>
              <button *ngIf="alert.status === 'ACKNOWLEDGED'" class="btn-secondary text-sm">Resolve</button>
              <button class="text-gray-400 hover:text-gray-600 dark:hover:text-gray-200">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 5v.01M12 12v.01M12 19v.01M12 6a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2zm0 7a1 1 0 110-2 1 1 0 010 2z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AlertsComponent {
  filterSeverity = '';

  alerts: Alert[] = [
    { id: 'ALT-4521', title: 'Critical pressure drop in water main', description: 'Pressure dropped 23% below normal. Immediate inspection recommended.', severity: 'CRITICAL', status: 'ACTIVE', assetName: 'Water Main - 5th Ave', assetType: 'WATER_PIPE', createdAt: '3 min ago', predictedFailure: '24-48 hours' },
    { id: 'ALT-4520', title: 'Vibration anomaly detected on bridge', description: 'Vibration levels exceed threshold by 45%. Structural assessment needed.', severity: 'HIGH', status: 'ACTIVE', assetName: 'Bridge - Oak Street', assetType: 'BRIDGE', createdAt: '8 min ago', predictedFailure: '48-72 hours' },
    { id: 'ALT-4519', title: 'Thermal stress warning on power line', description: 'Thermal stress index at 0.78. Load reduction recommended.', severity: 'HIGH', status: 'ACKNOWLEDGED', assetName: 'Power Line - Sector 7', assetType: 'POWER_LINE', createdAt: '15 min ago', acknowledgedBy: 'John Doe', predictedFailure: '72-96 hours' },
    { id: 'ALT-4518', title: 'Corrosion rate increase detected', description: 'Corrosion rate increased 15% over past week.', severity: 'MEDIUM', status: 'ACTIVE', assetName: 'Sewer Line - Main St', assetType: 'SEWER', createdAt: '32 min ago' },
    { id: 'ALT-4517', title: 'Sensor battery low', description: 'Battery level at 18%. Replacement needed within 2 weeks.', severity: 'LOW', status: 'ACTIVE', assetName: 'Street Light - Broadway', assetType: 'STREET_LIGHT', createdAt: '1 hour ago' },
  ];

  filteredAlerts = [...this.alerts];

  get activeAlerts() {
    return this.alerts.filter(a => a.status === 'ACTIVE');
  }

  filterAlerts() {
    this.filteredAlerts = this.alerts.filter(alert => {
      return !this.filterSeverity || alert.severity === this.filterSeverity;
    });
  }
}
