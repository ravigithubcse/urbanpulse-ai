import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-analytics',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Analytics</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">Performance insights and cost avoidance metrics</p>
        </div>
        <div class="flex items-center gap-2">
          <select class="input-field text-sm w-36">
            <option>Last 30 Days</option>
            <option>Last 90 Days</option>
            <option>Last Year</option>
          </select>
        </div>
      </div>

      <!-- Cost Avoidance -->
      <div class="card">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Cost Avoidance Analysis</h3>
        <div class="grid grid-cols-1 sm:grid-cols-3 gap-6">
          <div class="text-center p-4 bg-success-50 dark:bg-green-900/20 rounded-xl">
            <p class="text-3xl font-bold text-success-600 dark:text-green-400">$487,000</p>
            <p class="text-sm text-gray-600 dark:text-gray-300 mt-1">Total Cost Avoidance</p>
            <p class="text-xs text-success-600 dark:text-green-400 mt-1">+23% vs last period</p>
          </div>
          <div class="text-center p-4 bg-primary-50 dark:bg-primary-900/20 rounded-xl">
            <p class="text-3xl font-bold text-primary-600 dark:text-primary-400">$334,000</p>
            <p class="text-sm text-gray-600 dark:text-gray-300 mt-1">Emergency Costs Avoided</p>
            <p class="text-xs text-primary-600 dark:text-primary-400 mt-1">18 emergency repairs</p>
          </div>
          <div class="text-center p-4 bg-warning-50 dark:bg-amber-900/20 rounded-xl">
            <p class="text-3xl font-bold text-warning-600 dark:text-amber-400">3.2x</p>
            <p class="text-sm text-gray-600 dark:text-gray-300 mt-1">ROI on Predictions</p>
            <p class="text-xs text-warning-600 dark:text-amber-400 mt-1">$3.2 saved per $1 spent</p>
          </div>
        </div>
      </div>

      <!-- Model Performance -->
      <div class="card">
        <h3 class="text-lg font-semibold text-gray-900 dark:text-white mb-4">Model Performance by Infrastructure Type</h3>
        <div class="space-y-4">
          <div *ngFor="let model of modelPerformance" class="p-4 bg-gray-50 dark:bg-gray-800/50 rounded-lg">
            <div class="flex items-center justify-between mb-2">
              <div class="flex items-center gap-3">
                <div class="w-8 h-8 rounded-lg flex items-center justify-center" [class]="model.colorClass">
                  <svg class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" [attr.d]="model.icon"/>
                  </svg>
                </div>
                <div>
                  <p class="font-medium text-gray-900 dark:text-white">{{ model.type }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">{{ model.algorithm }}</p>
                </div>
              </div>
              <span class="text-lg font-bold text-primary-600 dark:text-primary-400">{{ model.accuracy }}%</span>
            </div>
            <div class="grid grid-cols-4 gap-4 mt-3">
              <div>
                <p class="text-xs text-gray-500 dark:text-gray-400">Precision</p>
                <p class="text-sm font-semibold text-gray-700 dark:text-gray-200">{{ model.precision }}%</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 dark:text-gray-400">Recall</p>
                <p class="text-sm font-semibold text-gray-700 dark:text-gray-200">{{ model.recall }}%</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 dark:text-gray-400">F1 Score</p>
                <p class="text-sm font-semibold text-gray-700 dark:text-gray-200">{{ model.f1 }}%</p>
              </div>
              <div>
                <p class="text-xs text-gray-500 dark:text-gray-400">Predictions</p>
                <p class="text-sm font-semibold text-gray-700 dark:text-gray-200">{{ model.predictions }}</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
})
export class AnalyticsComponent {
  modelPerformance = [
    { type: 'Water Pipes', algorithm: 'XGBoost + LSTM Ensemble', accuracy: 95.1, precision: 93.8, recall: 91.2, f1: 92.5, predictions: 1247, icon: 'M20 14.66V20a2 2 0 01-2 2H4a2 2 0 01-2-2V6a2 2 0 012-2h2.34M20 14.66a3 3 0 01-3.15-1.35 3 3 0 010-3.3 3 3 0 013.15-1.35M20 14.66l-2.91-1.45M20 14.66l2.91-1.45M4 6h16M4 6l8 4 8-4', colorClass: 'bg-blue-100 dark:bg-blue-900/30 text-blue-600 dark:text-blue-400' },
    { type: 'Bridges', algorithm: 'Transformer Attention', accuracy: 96.3, precision: 94.5, recall: 92.8, f1: 93.6, predictions: 892, icon: 'M4 6h16M4 6v12M20 6v12M4 12h16M8 6V4m8 2V4m-4 14v-4m0 0l-2-2m2 2l2-2', colorClass: 'bg-amber-100 dark:bg-amber-900/30 text-amber-600 dark:text-amber-400' },
    { type: 'Roads', algorithm: 'LSTM Sequence Model', accuracy: 93.8, precision: 91.2, recall: 88.9, f1: 90.0, predictions: 1567, icon: 'M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z', colorClass: 'bg-gray-100 dark:bg-gray-800 text-gray-600 dark:text-gray-400' },
    { type: 'Power Lines', algorithm: 'Temporal Fusion Transformer', accuracy: 94.1, precision: 90.1, recall: 84.5, f1: 87.2, predictions: 741, icon: 'M13 10V3L4 14h7v7l9-11h-7z', colorClass: 'bg-yellow-100 dark:bg-yellow-900/30 text-yellow-600 dark:text-yellow-400' },
  ];
}
