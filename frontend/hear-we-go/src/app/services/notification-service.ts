import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject } from 'rxjs';
import { NotificationResponse } from '../DTOs/notification-module';
import SockJS from 'sockjs-client';
import { over } from 'stompjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = '/api/notifications';

  private stompClient: any;

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

  markAsRead(createdAt: string): void {
    this.http.patch(`${this.apiUrl}/mark-as-read?createdAt=${createdAt}`, {})
      .subscribe({
        next: () => {
          const currentNotifications = this.notificationsSubject.value.map(n => {
            if (n.createdAt === createdAt) {
              return { ...n, isRead: true };
            }
            return n;
          });

          this.updateState(currentNotifications);
        },
        error: (err) => console.error('Greška pri označavanju kao pročitano:', err)
      });
  }

  deleteNotification(createdAt: string): void {
    this.http.delete(`${this.apiUrl}?createdAt=${createdAt}`)
      .subscribe({
        next: () => {
          const currentNotifications = this.notificationsSubject.value.filter(
            n => n.createdAt !== createdAt
          );

          this.updateState(currentNotifications);
          console.log('Notifikacija obrisana:', createdAt);
        },
        error: (err) => console.error('Greška pri brisanju notifikacije:', err)
      });
  }

  initializeWebSocketConnection(userId: string): void {

    const socket = new SockJS('https://localhost:8080/ws');
    this.stompClient = over(socket);
    this.stompClient.debug = (str: string) => {
      console.log('STOMP DEBUG: ' + str);
    };

    this.stompClient.connect({}, () => {
      console.log('WebSocket veza uspešno uspostavljena!');

      this.stompClient.subscribe(`/topic/notifications/${userId}`, (message: any) => {
        if (message.body) {
          const newNotification: NotificationResponse = JSON.parse(message.body);

          this.addNewNotification(newNotification);
        }
      });
    }, (error: any) => {
      console.error('WebSocket greška, pokušavam ponovo...', error);
      setTimeout(() => this.initializeWebSocketConnection(userId), 5000);
    });
  }

  public addNewNotification(notification: NotificationResponse): void {
    const currentList = this.notificationsSubject.value;
    const newList = [notification, ...currentList];
    this.updateState(newList);
  }


}
