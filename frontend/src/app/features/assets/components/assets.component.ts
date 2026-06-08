import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

interface Asset {
  id: string;
  name: string;
  type: string;
  category: string;
  location: string;
  healthScore: number;
  status: string;
  riskLevel: string;
  age: number;
  lastMaintenance: string;
  sensors: number;
}

@Component({
  selector: 'app-assets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="space-y-6">
      <div class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Infrastructure Assets</h1>
          <p class="text-sm text-gray-500 dark:text-gray-400 mt-1">{{ filteredAssets.length }} assets across all categories</p>
        </div>
        <div class="flex items-center gap-3">
          <input type="text" [(ngModel)]="searchQuery" (input)="filterAssets()" 
                 placeholder="Search assets..." class="input-field text-sm w-64" />
          <select [(ngModel)]="filterCategory" (change)="filterAssets()" class="input-field text-sm w-40">
            <option value="">All Categories</option>
            <option value="WATER">Water</option>
            <option value="TRANSPORTATION">Transportation</option>
            <option value="ENERGY">Energy</option>
            <option value="COMMUNICATION">Communication</option>
          </select>
        </div>
      </div>

      <div class="table-container overflow-x-auto">
        <table class="data-table">
          <thead>
            <tr>
              <th>Asset</th>
              <th>Category</th>
              <th>Location</th>
              <th>Health</th>
              <th>Status</th>
              <th>Risk</th>
              <th>Age</th>
              <th>Sensors</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let asset of filteredAssets" class="group">
              <td>
                <div>
                  <p class="font-medium text-gray-900 dark:text-white">{{ asset.name }}</p>
                  <p class="text-xs text-gray-500 dark:text-gray-400">ID: {{ asset.id }}</p>
                </div>
              </td>
              <td>
                <span class="badge bg-gray-100 dark:bg-gray-800 text-gray-700 dark:text-gray-300">{{ asset.category }}</span>
              </td>
              <td class="text-gray-600 dark:text-gray-300">{{ asset.location }}</td>
              <td>
                <div class="flex items-center gap-2">
                  <div class="w-12 h-2 bg-gray-200 dark:bg-gray-700 rounded-full overflow-hidden">
                    <div class="h-full rounded-full" 
                         [class.bg-success-500]="asset.healthScore >= 70"
                         [class.bg-warning-500]="asset.healthScore >= 40 && asset.healthScore < 70"
                         [class.bg-danger-500]="asset.healthScore < 40"
                         [style.width.%]="asset.healthScore"></div>
                  </div>
                  <span class="text-sm font-medium">{{ asset.healthScore }}</span>
                </div>
              </td>
              <td>
                <span class="badge" [class.badge-success]="asset.status === 'ACTIVE'"
                      [class.badge-warning]="asset.status === 'MAINTENANCE'"
                      [class.badge-critical]="asset.status === 'FAILED'">{{ asset.status }}</span>
              </td>
              <td>
                <span class="text-sm" [class.text-danger-600]="asset.riskLevel === 'High'"
                      [class.text-warning-600]="asset.riskLevel === 'Medium'"
                      [class.text-success-600]="asset.riskLevel === 'Low'">{{ asset.riskLevel }}</span>
              </td>
              <td class="text-gray-600 dark:text-gray-300">{{ asset.age }} yrs</td>
              <td class="text-gray-600 dark:text-gray-300">{{ asset.sensors }}</td>
              <td>
                <button class="text-primary-600 hover:text-primary-700 text-sm font-medium">View</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  `,
})
export class AssetsComponent {
  searchQuery = '';
  filterCategory = '';

  assets: Asset[] = [
    { id: 'AST-2847', name: 'Water Main - 5th Ave', type: 'WATER_PIPE', category: 'WATER', location: 'Manhattan, NY', healthScore: 32, status: 'ACTIVE', riskLevel: 'High', age: 45, lastMaintenance: '2024-01-15', sensors: 8 },
    { id: 'AST-2846', name: 'Bridge - Oak Street', type: 'BRIDGE', category: 'TRANSPORTATION', location: 'Brooklyn, NY', healthScore: 58, status: 'ACTIVE', riskLevel: 'Medium', age: 62, lastMaintenance: '2024-02-20', sensors: 12 },
    { id: 'AST-2845', name: 'Power Line - Sector 7', type: 'POWER_LINE', category: 'ENERGY', location: 'Queens, NY', healthScore: 65, status: 'ACTIVE', riskLevel: 'Medium', age: 38, lastMaintenance: '2024-03-01', sensors: 6 },
    { id: 'AST-2844', name: 'Road - Highway 101', type: 'ROAD', category: 'TRANSPORTATION', location: 'Bronx, NY', healthScore: 78, status: 'ACTIVE', riskLevel: 'Low', age: 15, lastMaintenance: '2024-01-30', sensors: 4 },
    { id: 'AST-2843', name: 'Sewer Line - Main St', type: 'SEWER', category: 'WATER', location: 'Staten Island, NY', healthScore: 88, status: 'ACTIVE', riskLevel: 'Low', age: 25, lastMaintenance: '2024-02-15', sensors: 5 },
    { id: 'AST-2842', name: 'Telecom Tower - Midtown', type: 'TELECOM_TOWER', category: 'COMMUNICATION', location: 'Manhattan, NY', healthScore: 92, status: 'ACTIVE', riskLevel: 'Low', age: 12, lastMaintenance: '2024-03-10', sensors: 10 },
    { id: 'AST-2841', name: 'Transformer - West Side', type: 'TRANSFORMER', category: 'ENERGY', location: 'Manhattan, NY', healthScore: 45, status: 'MAINTENANCE', riskLevel: 'High', age: 55, lastMaintenance: '2023-12-20', sensors: 7 },
    { id: 'AST-2840', name: 'Street Light - Broadway', type: 'STREET_LIGHT', category: 'LIGHTING', location: 'Manhattan, NY', healthScore: 95, status: 'ACTIVE', riskLevel: 'Low', age: 5, lastMaintenance: '2024-03-15', sensors: 2 },
  ];

  filteredAssets = [...this.assets];

  filterAssets() {
    this.filteredAssets = this.assets.filter(asset => {
      const matchesSearch = !this.searchQuery || 
        asset.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        asset.id.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        asset.location.toLowerCase().includes(this.searchQuery.toLowerCase());
      const matchesCategory = !this.filterCategory || asset.category === this.filterCategory;
      return matchesSearch && matchesCategory;
    });
  }
}
