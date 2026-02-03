import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { UserActivity } from '../DTOs/analytics-module';
import { AnalyticsService } from '../services/analytics-service';
import { DatePipe, KeyValuePipe, NgForOf, NgIf, UpperCasePipe, DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-user-history-component',
  standalone: true,
  imports: [DatePipe, NgIf, NgForOf, KeyValuePipe, UpperCasePipe, DecimalPipe],
  templateUrl: './user-history-component.html',
  styleUrl: './user-history-component.css',
})
export class UserHistoryComponent implements OnInit {
  listeningHistory: UserActivity[] = [];
  ratingHistory: UserActivity[] = [];
  followHistory: UserActivity[] = [];

  pageL = 0; pageR = 0; pageF = 0;
  loadingL = false; loadingR = false; loadingF = false;
  hasMoreL = true; hasMoreR = true; hasMoreF = true;

  constructor(
    private analyticsService: AnalyticsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.refreshData();
  }

  refreshData(): void {
    this.pageL = 0; this.pageR = 0; this.pageF = 0;
    this.listeningHistory = []; this.ratingHistory = []; this.followHistory = [];
    this.hasMoreL = true; this.hasMoreR = true; this.hasMoreF = true;

    this.loadListening();
    this.loadRatings();
    this.loadFollows();
  }

  loadListening(): void {
    this.loadingL = true;
    this.cdr.detectChanges();
    this.analyticsService.getUserHistory(['SONG_LISTENED'], this.pageL, 10).subscribe({
      next: (data) => {
        setTimeout(() => {
          this.listeningHistory = [...this.listeningHistory, ...data];
          this.hasMoreL = data.length === 10;
          this.loadingL = false;
          this.cdr.detectChanges();
        }, 500);
      },
      error: () => { this.loadingL = false; this.cdr.detectChanges(); }
    });
  }

  loadRatings(): void {
    this.loadingR = true;
    this.cdr.detectChanges();
    this.analyticsService.getUserHistory(['RATING_SAVED', 'RATING_REMOVED'], this.pageR, 10).subscribe({
      next: (data) => {
        setTimeout(() => {
          this.ratingHistory = [...this.ratingHistory, ...data];
          this.hasMoreR = data.length === 10;
          this.loadingR = false;
          this.cdr.detectChanges();
        }, 500);
      },
      error: () => { this.loadingR = false; this.cdr.detectChanges(); }
    });
  }

  loadFollows(): void {
    this.loadingF = true;
    this.cdr.detectChanges();
    this.analyticsService.getUserHistory(['SUB_CREATED', 'SUB_DELETED'], this.pageF, 10).subscribe({
      next: (data) => {
        setTimeout(() => {
          this.followHistory = [...this.followHistory, ...data];
          this.hasMoreF = data.length === 10;
          this.loadingF = false;
          this.cdr.detectChanges();
        }, 800);
      },
      error: () => { this.loadingF = false; this.cdr.detectChanges(); }
    });
  }

  moreL() { this.pageL++; this.loadListening(); }
  moreR() { this.pageR++; this.loadRatings(); }
  moreF() { this.pageF++; this.loadFollows(); }

  lessL() {
    if (this.listeningHistory.length > 10) {
      this.listeningHistory = this.listeningHistory.slice(0, -10);
      this.pageL--;
      this.hasMoreL = true;
      this.cdr.detectChanges();
    }
  }

  lessR() {
    if (this.ratingHistory.length > 10) {
      this.ratingHistory = this.ratingHistory.slice(0, -10);
      this.pageR--;
      this.hasMoreR = true;
      this.cdr.detectChanges();
    }
  }

  lessF() {
    if (this.followHistory.length > 10) {
      this.followHistory = this.followHistory.slice(0, -10);
      this.pageF--;
      this.hasMoreF = true;
      this.cdr.detectChanges();
    }
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
