import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Prediction {
  id: string;
  assetName: string;
  assetType: string;
  probability: number;
  confidence: number;
  severity: string;
  timeWindow: string;
  topFactors: string[];
  modelVersion: string;
}

@Component({
  selector: 'app-predictions',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">AI Predictions</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">ML-powered failure predictions across infrastructure</p>
        </div>
        <div class="flex items-center gap-2">
          <span class="text-xs text-gray-500 dark:text-gray-400">Model: v2.1.0</span>
          <span class="badge badge-success text-xs">95.2% accuracy</span>
        </div>
      </div>

      <!-- Model Performance Summary -->
      <div class="grid grid-cols-2 sm:grid-cols-4 gap-4">
        <div class="card text-center">
          <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">94.7%</p>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Accuracy</p>
        </div>
        <div class="card text-center">
          <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">92.3%</p>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Precision</p>
        </div>
        <div class="card text-center">
          <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">89.1%</p>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Recall</p>
        </div>
        <div class="card text-center">
          <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">0.934</p>
          <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">PR-AUC</p>
        </div>
      </div>

      <!-- Predictions List -->
      <div class="space-y-4">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white">Top Risk Predictions</h3>
        <div *ngFor="let pred of predictions" class="card hover:shadow-md transition-shadow"
             [class.border-l-4]="true"
             [class.border-l-danger-500]="pred.severity === 'CRITICAL'"
             [class.border-l-warning-500]="pred.severity === 'HIGH'"
             [class.border-l-primary-500]="pred.severity === 'MEDIUM'">
          <div class="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
            <div class="flex-1">
              <div class="flex items-center gap-3">
                <h4 class="font-semibold text-gray-900 dark:text-white">{{ pred.assetName }}</h4>
                <span class="badge" [class.badge-critical]="pred.severity === 'CRITICAL'"
                      [class.badge-warning]="pred.severity === 'HIGH'"
                      [class.badge-success]="pred.severity === 'MEDIUM'">{{ pred.severity }}</span>
              </div>
              <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ pred.assetType.replace('_', ' ') }} - Window: {{ pred.timeWindow }}</p>
              <div class="flex flex-wrap gap-2 mt-2">
                <span *ngFor="let factor of pred.topFactors" class="text-xs px-2 py-1 bg-gray-100 dark:bg-gray-800 rounded text-gray-600 dark:text-gray-300">{{ factor }}</span>
              </div>
            </div>
            <div class="flex items-center gap-6">
              <div class="text-center">
                <div class="relative w-16 h-16">
                  <svg class="w-16 h-16 transform -rotate-90" viewBox="0 0 36 36">
                    <circle cx="18" cy="18" r="15.5" fill="none" stroke="currentColor" stroke-width="3" class="text-gray-200 dark:text-gray-700"/>
                    <circle cx="18" cy="18" r="15.5" fill="none" stroke="currentColor" stroke-width="3"
                            [class.text-danger-500]="pred.probability > 0.8"
                            [class.text-warning-500]="pred.probability <= 0.8 && pred.probability > 0.5"
                            [class.text-success-500]="pred.probability <= 0.5"
                            [attr.stroke-dasharray]="pred.probability * 97.4 + ' ' + (100 - pred.probability * 97.4)"
                            stroke-linecap="round"/>
                  </svg>
                  <span class="absolute inset-0 flex items-center justify-center text-sm font-bold">{{ pred.probability | percent:'1.0-0' }}</span>
                </div>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">Probability</p>
              </div>
              <div class="text-right">
                <p class="text-sm font-medium text-gray-700 dark:text-gray-300">Confidence</p>
                <p class="text-lg font-bold text-primary-600 dark:text-primary-400">{{ pred.confidence | percent:'1.0-0' }}</p>
                <p class="text-xs text-gray-400 dark:text-gray-500">{{ pred.modelVersion }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class PredictionsComponent {
  predictions: Prediction[] = [
    { id: 'PRD-8923', assetName: 'Water Main - 5th Ave', assetType: 'WATER_PIPE', probability: 0.92, confidence: 0.88, severity: 'CRITICAL', timeWindow: '24-48 hours', topFactors: ['Pressure: 68 PSI', 'Corrosion: 0.32', 'Age: 45 yrs'], modelVersion: 'v2.1.0' },
    { id: 'PRD-8922', assetName: 'Bridge - Oak Street', assetType: 'BRIDGE', probability: 0.84, confidence: 0.82, severity: 'HIGH', timeWindow: '48-72 hours', topFactors: ['Vibration: 0.45g', 'Traffic: 8500/day', 'Age: 62 yrs'], modelVersion: 'v2.1.0' },
    { id: 'PRD-8921', assetName: 'Power Line - Sector 7', assetType: 'POWER_LINE', probability: 0.71, confidence: 0.75, severity: 'HIGH', timeWindow: '72-96 hours', topFactors: ['Load: 485MW', 'Thermal: 0.78', 'Wind: 35mph'], modelVersion: 'v2.1.0' },
    { id: 'PRD-8920', assetName: 'Road - Highway 101', assetType: 'ROAD', probability: 0.58, confidence: 0.72, severity: 'MEDIUM', timeWindow: '5-7 days', topFactors: ['Traffic: 12000/day', 'Temp: -5C change', 'Age: 15 yrs'], modelVersion: 'v2.0.8' },
    { id: 'PRD-8919', assetName: 'Transformer - West Side', assetType: 'TRANSFORMER', probability: 0.52, confidence: 0.68, severity: 'MEDIUM', timeWindow: '7-10 days', topFactors: ['Load: 92%', 'Age: 55 yrs', 'Oil temp: 85C'], modelVersion: 'v2.1.0' },
  ];
}
