import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { UserActivity } from '../DTOs/analytics-module';
import { AnalyticsService } from '../services/analytics-service';
import { DatePipe, JsonPipe, KeyValuePipe, NgForOf, NgIf } from '@angular/common';

@Component({
  selector: 'app-user-history-component',
  standalone: true,
  imports: [DatePipe, JsonPipe, NgIf, NgForOf, KeyValuePipe],
  templateUrl: './user-history-component.html',
  styleUrl: './user-history-component.css',
})
export class UserHistoryComponent implements OnInit {
  listeningHistory: UserActivity[] = [];
  ratingHistory: UserActivity[] = [];
  followHistory: UserActivity[] = [];
  history: UserActivity[] = [];
  isLoading: boolean = false;

  constructor(
    private analyticsService: AnalyticsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.refreshData();
  }

  refreshData(): void {
    this.isLoading = true;
    this.analyticsService.getUserHistory().subscribe({
      next: (data) => {
        this.history = data.sort((a, b) =>
          new Date(b.timestamp).getTime() - new Date(a.timestamp).getTime()
        );

        this.groupHistory(this.history);

        setTimeout(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        }, 700);
      },
      error: (err) => {
        console.error('Greška pri učitavanju:', err);
        this.isLoading = false;
      }
    });
  }

  private groupHistory(data: UserActivity[]): void {
    this.listeningHistory = data.filter(a => a.eventType === 'SONG_LISTENED');

    this.ratingHistory = data.filter(a =>
      ['RATING_SAVED', 'RATING_REMOVED'].includes(a.eventType)
    );

    this.followHistory = data.filter(a =>
      ['SUB_CREATED', 'SUB_DELETED'].includes(a.eventType)
    );
  }

  formatEventType(type: string): string {
    const types: Record<string, string> = {
      'SONG_LISTENED': 'Preslušano',
      'RATING_SAVED': 'Ocenjeno',
      'RATING_REMOVED': 'Ocena uklonjena',
      'SUB_CREATED': 'Zapratili ste',
      'SUB_DELETED': 'Otpratili ste'
    };
    return types[type] || type.replace(/_/g, ' ');
  }

  getDisplayName(payload: any): string {
    if (!payload) return '';
    return payload.targetName || payload.title || payload.songName || payload.name || '';
  }
}
