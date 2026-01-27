import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {UserActivity, UserAnalytics} from '../DTOs/analytics-module';

@Injectable({
  providedIn: 'root',
})
export class AnalyticsService {

  private apiUrl = `/api/analytics`;

  constructor(private http: HttpClient) {}

  // analytics.service.ts
  getUserHistory(types: string[], page: number, size: number = 10): Observable<UserActivity[]> {
    const typesParam = types.map(t => `types=${t}`).join('&');
    return this.http.get<UserActivity[]>(`${this.apiUrl}/history?${typesParam}&page=${page}&size=${size}`);
  }

  getUserStats(): Observable<UserAnalytics> {
    return this.http.get<UserAnalytics>(`${this.apiUrl}/stats`);
  }

}
