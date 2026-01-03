import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { NotificationResponse } from '../DTOs/notification-module';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = '/api/notifications';

  private notificationsSubject = new BehaviorSubject<NotificationResponse[]>([]);
  public notifications$ = this.notificationsSubject.asObservable();

  private unreadCountSubject = new BehaviorSubject<number>(0);
  public unreadCount$ = this.unreadCountSubject.asObservable();

  constructor(private http: HttpClient) {
    this.loadInitialNotifications();
  }

  loadInitialNotifications(): void {
    console.log('Pokrećem GET zahtev na:', this.apiUrl);
    this.http.get<NotificationResponse[]>(this.apiUrl)
      .subscribe({
        next: (data) => {
          console.log('STIGLI PODACI IZ BAZE:', data);
          this.updateState(data);
        },
        error: (err) => {
          console.error('BACKEND JE VRATIO GREŠKU:', err);
        }
      });
  }

  private updateState(notifications: NotificationResponse[]): void {
    this.notificationsSubject.next(notifications);

    const unread = notifications.filter(n => !n.isRead).length;
    this.unreadCountSubject.next(unread);
  }

  public addNewNotification(notification: NotificationResponse): void {
    const currentList = this.notificationsSubject.value;
    const newList = [notification, ...currentList];
    this.updateState(newList);
  }
}
