import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification-service';
import { map, Observable, of } from 'rxjs';
import {AuthService} from '../../services/auth.service';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-list.html',
  styleUrl: './notification-list.css'
})
export class NotificationListComponent implements OnInit {
  hasUnread$!: Observable<boolean>;

  constructor(public notificationService: NotificationService, public authService: AuthService) {}

  ngOnInit(): void {
    const userId = this.authService.getUserId();
    if (userId) {
      this.notificationService.initializeWebSocketConnection(userId);
    }

    this.hasUnread$ = this.notificationService.notifications$.pipe(
      map(notes => notes.some(n => !n.isRead))
    );

  }

  markAsRead(createdAt: string): void {
    this.notificationService.markAsRead(createdAt);
  }

  deleteNote(createdAt: string, event: Event): void {
    event.stopPropagation();

    if (confirm('Da li ste sigurni da želite da obrišete ovu notifikaciju?')) {
      this.notificationService.deleteNotification(createdAt);
    }
  }
}
