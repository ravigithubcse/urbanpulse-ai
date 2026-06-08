import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../environments/environment';

export interface DashboardStats {
  totalAssets: number;
  activeSensors: number;
  activeAlerts: number;
  avgHealthScore: number;
  predictionsToday: number;
  costAvoidance: number;
  alertBreakdown: { critical: number; high: number; medium: number; low: number };
  assetStatusBreakdown: { active: number; maintenance: number; failed: number };
  recentPredictions: Prediction[];
  recentAlerts: Alert[];
  healthTrend: HealthTrendPoint[];
  sensorActivity: SensorActivityPoint[];
}

export interface Prediction {
  id: string;
  assetName: string;
  assetType: string;
  probability: number;
  severity: string;
  time: string;
}

export interface Alert {
  id: string;
  title: string;
  severity: string;
  status: string;
  time: string;
  assetName: string;
}

export interface HealthTrendPoint {
  date: string;
  score: number;
  critical: number;
  warning: number;
}

export interface SensorActivityPoint {
  time: string;
  readings: number;
}

@Injectable({ providedIn: 'root' })
export class DashboardService {
  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<DashboardStats> {
    // Return mock data for demo
    return of({
      totalAssets: 2847,
      activeSensors: 3847,
      activeAlerts: 12,
      avgHealthScore: 87,
      predictionsToday: 47,
      costAvoidance: 487000,
      alertBreakdown: { critical: 2, high: 4, medium: 3, low: 3 },
      assetStatusBreakdown: { active: 2450, maintenance: 127, failed: 18 },
      recentPredictions: [
        { id: '1', assetName: 'Water Main - 5th Ave', assetType: 'WATER_PIPE', probability: 0.92, severity: 'critical', time: '2 min ago' },
        { id: '2', assetName: 'Bridge - Oak Street', assetType: 'BRIDGE', probability: 0.78, severity: 'high', time: '5 min ago' },
        { id: '3', assetName: 'Power Line - Sector 7', assetType: 'POWER_LINE', probability: 0.65, severity: 'medium', time: '12 min ago' },
        { id: '4', assetName: 'Road - Highway 101', assetType: 'ROAD', probability: 0.58, severity: 'medium', time: '18 min ago' },
        { id: '5', assetName: 'Sewer Line - Main St', assetType: 'SEWER', probability: 0.45, severity: 'low', time: '25 min ago' },
      ],
      recentAlerts: [
        { id: '1', title: 'Critical pressure drop detected', severity: 'critical', status: 'active', time: '3 min ago', assetName: 'Water Main - 5th Ave' },
        { id: '2', title: 'Vibration anomaly on bridge', severity: 'high', status: 'acknowledged', time: '8 min ago', assetName: 'Bridge - Oak Street' },
        { id: '3', title: 'Thermal stress warning', severity: 'medium', status: 'active', time: '15 min ago', assetName: 'Power Line - Sector 7' },
      ],
      healthTrend: [
        { date: 'Jan', score: 82, critical: 8, warning: 22 },
        { date: 'Feb', score: 84, critical: 6, warning: 20 },
        { date: 'Mar', score: 83, critical: 7, warning: 21 },
        { date: 'Apr', score: 85, critical: 5, warning: 18 },
        { date: 'May', score: 86, critical: 4, warning: 16 },
        { date: 'Jun', score: 87, critical: 3, warning: 14 },
        { date: 'Jul', score: 87, critical: 3, warning: 12 },
      ],
      sensorActivity: [
        { time: '00:00', readings: 1200 },
        { time: '04:00', readings: 980 },
        { time: '08:00', readings: 2100 },
        { time: '12:00', readings: 1850 },
        { time: '16:00', readings: 1950 },
        { time: '20:00', readings: 1600 },
        { time: '23:59', readings: 1100 },
      ],
    });
  }
}
