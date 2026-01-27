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

  getUserHistory(): Observable<UserActivity[]> {
    return this.http.get<UserActivity[]>(this.apiUrl + '/history');
  }

  getUserStats(): Observable<UserAnalytics> {
    return this.http.get<UserAnalytics>(`${this.apiUrl}/stats`);
  }

}
