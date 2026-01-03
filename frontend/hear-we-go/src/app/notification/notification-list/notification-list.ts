import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../services/notification-service';
import { map, Observable, of } from 'rxjs';

@Component({
  selector: 'app-notification-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification-list.html',
  styleUrl: './notification-list.css'
})
export class NotificationListComponent implements OnInit {
  hasUnread$!: Observable<boolean>;

  constructor(public notificationService: NotificationService) {}

  ngOnInit(): void {
    this.hasUnread$ = this.notificationService.notifications$.pipe(
      map(notes => notes.some(n => !n.isRead))
    );
  }

  markAsRead(id: string): void {
    console.log('Obeležavam kao pročitano ID:', id);
  }

  deleteNote(id: string): void {
    console.log('Brišem notifikaciju ID:', id);
  }
}
