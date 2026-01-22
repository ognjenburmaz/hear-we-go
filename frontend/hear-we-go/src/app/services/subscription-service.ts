import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';

export interface SubRequest {
  targetId: string;
  targetName: string;
  type: 'ARTIST' | 'GENRE';
}

export interface UserSubscription {
  userId: string;
  targetId: string;
  targetName: string;
  type: string;
}

@Injectable({
  providedIn: 'root'
})
export class SubscriptionService {
  private apiUrl = `/api/subscriptions`;

  constructor(private http: HttpClient, private router: Router) {
  }

  subscribe(sub: SubRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, sub);
  }

  unsubscribe(targetId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${targetId}`);
  }

  getMySubscriptions(): Observable<UserSubscription[]> {
    return this.http.get<UserSubscription[]>(this.apiUrl);
  }
}
