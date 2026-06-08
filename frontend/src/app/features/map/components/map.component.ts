import { Component, OnInit, OnDestroy, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';

declare const L: any;

@Component({
  selector: 'app-map',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-4">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Interactive Map</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Real-time infrastructure visualization</p>
        </div>
        <div class="flex items-center gap-2">
          <button class="btn-secondary text-sm" (click)="toggleHeatmap()">
            {{ showHeatmap ? 'Hide' : 'Show' }} Heatmap
          </button>
          <button class="btn-primary text-sm" (click)="centerOnCity()">
            <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M17.657 16.657L13.414 20.9a1.998 1.998 0 01-2.827 0l-4.244-4.243a8 8 0 1111.314 0z"/>
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 11a3 3 0 11-6 0 3 3 0 016 0z"/>
            </svg>
          </button>
        </div>
      </div>

      <!-- Map Container -->
      <div class="card p-0 overflow-hidden">
        <div class="flex flex-wrap gap-4 p-4 border-b border-gray-200 dark:border-dark-border bg-gray-50 dark:bg-gray-800/50">
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-success-500"></span>
            <span class="text-xs text-gray-600 dark:text-gray-300">Healthy ({{ healthyCount }})</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-warning-500"></span>
            <span class="text-xs text-gray-600 dark:text-gray-300">At Risk ({{ atRiskCount }})</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-danger-500"></span>
            <span class="text-xs text-gray-600 dark:text-gray-300">Critical ({{ criticalCount }})</span>
          </div>
          <div class="flex items-center gap-2">
            <span class="w-3 h-3 rounded-full bg-primary-500"></span>
            <span class="text-xs text-gray-600 dark:text-gray-300">Sensors ({{ sensorCount }})</span>
          </div>
        </div>
        <div id="map" class="w-full" style="height: 600px;"></div>
      </div>

      <!-- Asset List -->
      <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
        <div class="card border-l-4 border-l-success-500" *ngFor="let asset of nearbyAssets">
          <div class="flex items-start justify-between">
            <div>
              <h4 class="font-semibold text-gray-900 dark:text-white">{{ asset.name }}</h4>
              <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ asset.type }} - {{ asset.distance }}m away</p>
            </div>
            <span class="badge" [class.badge-success]="asset.status === 'HEALTHY'"
                  [class.badge-warning]="asset.status === 'AT_RISK'"
                  [class.badge-critical]="asset.status === 'CRITICAL'">{{ asset.status }}</span>
          </div>
          <div class="mt-3 flex items-center gap-4 text-xs text-gray-500 dark:text-gray-400">
            <span>Health: {{ asset.health }}%</span>
            <span>Risk: {{ asset.riskLevel }}</span>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class MapComponent implements OnInit, AfterViewInit, OnDestroy {
  private map: any;
  showHeatmap = true;
  healthyCount = 2450;
  atRiskCount = 127;
  criticalCount = 12;
  sensorCount = 3847;

  nearbyAssets = [
    { name: 'Water Main - 5th Ave', type: 'Water Pipe', distance: 150, status: 'CRITICAL', health: 32, riskLevel: 'High' },
    { name: 'Bridge - Oak Street', type: 'Bridge', distance: 320, status: 'AT_RISK', health: 58, riskLevel: 'Medium' },
    { name: 'Power Line - Sector 7', type: 'Power Line', distance: 450, status: 'AT_RISK', health: 65, riskLevel: 'Medium' },
  ];

  private assetMarkers = [
    { lat: 40.7128, lng: -74.0060, name: 'Water Main - Broadway', type: 'WATER_PIPE', status: 'HEALTHY', health: 92 },
    { lat: 40.7200, lng: -74.0100, name: 'Bridge - Canal St', type: 'BRIDGE', status: 'AT_RISK', health: 58 },
    { lat: 40.7150, lng: -73.9950, name: 'Power Line - Financial', type: 'POWER_LINE', status: 'HEALTHY', health: 88 },
    { lat: 40.7100, lng: -74.0000, name: 'Road - FDR Drive', type: 'ROAD', status: 'HEALTHY', health: 85 },
    { lat: 40.7180, lng: -73.9980, name: 'Water Main - 5th Ave', type: 'WATER_PIPE', status: 'CRITICAL', health: 32 },
    { lat: 40.7140, lng: -74.0120, name: 'Sewer Line - West St', type: 'SEWER', status: 'AT_RISK', health: 62 },
    { lat: 40.7220, lng: -73.9920, name: 'Traffic Signal - 42nd', type: 'TRAFFIC', status: 'HEALTHY', health: 95 },
    { lat: 40.7160, lng: -74.0050, name: 'Bridge - Oak Street', type: 'BRIDGE', status: 'AT_RISK', health: 55 },
    { lat: 40.7110, lng: -73.9970, name: 'Transformer - Midtown', type: 'TRANSFORMER', status: 'HEALTHY', health: 90 },
    { lat: 40.7190, lng: -74.0080, name: 'Road - Highway 101', type: 'ROAD', status: 'AT_RISK', health: 48 },
  ];

  ngOnInit() {}

  ngAfterViewInit() {
    this.initMap();
  }

  ngOnDestroy() {
    if (this.map) {
      this.map.remove();
    }
  }

  private initMap() {
    this.map = L.map('map').setView([40.7150, -74.0000], 14);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '&copy; OpenStreetMap contributors',
      maxZoom: 19,
    }).addTo(this.map);

    this.addMarkers();
  }

  private addMarkers() {
    const statusColors: Record<string, string> = {
      HEALTHY: '#22c55e',
      AT_RISK: '#f59e0b',
      CRITICAL: '#ef4444',
    };

    this.assetMarkers.forEach(asset => {
      const color = statusColors[asset.status] || '#6b7280';

      const circle = L.circleMarker([asset.lat, asset.lng], {
        radius: 12,
        fillColor: color,
        color: '#fff',
        weight: 2,
        opacity: 1,
        fillOpacity: 0.8,
      }).addTo(this.map);

      circle.bindPopup(`
        <div style="font-family: Inter, sans-serif; min-width: 180px;">
          <h4 style="margin: 0 0 4px; font-size: 14px; font-weight: 600;">${asset.name}</h4>
          <p style="margin: 0; font-size: 12px; color: #666;">${asset.type.replace('_', ' ')}</p>
          <div style="margin-top: 8px; display: flex; align-items: center; gap: 6px;">
            <span style="width: 8px; height: 8px; border-radius: 50%; background: ${color};"></span>
            <span style="font-size: 12px; font-weight: 500;">${asset.status}</span>
          </div>
          <p style="margin: 4px 0 0; font-size: 12px;">Health Score: ${asset.health}/100</p>
        </div>
      `);
    });
  }

  toggleHeatmap() {
    this.showHeatmap = !this.showHeatmap;
  }

  centerOnCity() {
    if (this.map) {
      this.map.setView([40.7150, -74.0000], 14);
    }
  }
}
