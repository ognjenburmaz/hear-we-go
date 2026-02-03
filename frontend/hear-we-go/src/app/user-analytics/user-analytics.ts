import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { AnalyticsService } from '../services/analytics-service';
import { UserAnalytics } from '../DTOs/analytics-module';
import { DecimalPipe, KeyValuePipe, NgForOf, NgIf, UpperCasePipe } from '@angular/common';
import { delay } from 'rxjs/operators';

@Component({
  selector: 'app-user-analytics',
  templateUrl: 'user-analytics.html',
  styleUrl: 'user-analytics.css',
  imports: [DecimalPipe, NgForOf, NgIf, KeyValuePipe, UpperCasePipe],
  standalone: true
})
export class UserAnalyticsComponent implements OnInit {
  stats: UserAnalytics | null = null;
  loading: boolean = true;

  constructor(
    private analyticsService: AnalyticsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.loadAnalytics();
  }

  loadAnalytics() {
    this.loading = true;
    this.cdr.detectChanges();

    this.analyticsService.getUserStats().pipe(
      delay(800)
    ).subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Greška:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  refreshData() {
    this.loadAnalytics();
  }
}
