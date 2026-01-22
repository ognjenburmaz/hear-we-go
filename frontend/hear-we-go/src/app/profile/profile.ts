import {ChangeDetectorRef, Component, OnInit} from '@angular/core';

import {AuthService} from '../services/auth.service';
import {SubscriptionService, UserSubscription } from '../services/subscription-service';
import {NotificationListComponent} from '../notification/notification-list/notification-list';
import {EmailRequest} from '../DTOs/emailRequest';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NotificationListComponent],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  userData: any = null;
  email!: string | null;
  subscriptions: UserSubscription[] = [];
  emailRequest: EmailRequest = {
    email: this.email
  }
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private authService: AuthService,
              private cdr: ChangeDetectorRef,
              private subService: SubscriptionService) {}

  ngOnInit(): void {
    this.userData = this.authService.getUserInfo();
    this.email = localStorage.getItem("email")
    this.emailRequest.email = this.email;
    this.loadSubscriptions();
    console.log('Podaci sa profila:', this.userData);
  }

  loadSubscriptions() {
    this.subService.getMySubscriptions().subscribe({
      next: (data) => {
        this.subscriptions = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Failed to load subscriptions', err)
    });
  }

  unfollow(targetId: string) {
    if(!confirm('Are you sure you want to unfollow?')) return;

    this.subService.unsubscribe(targetId).subscribe({
      next: () => {
        this.subscriptions = this.subscriptions.filter(s => s.targetId !== targetId);
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error unsubscribing', err);
        this.errorMessage = "Failed to unfollow. Try again.";
      }
    });
  }

  changePsw(): void {
    this.authService.sendRecoveryEmail(this.emailRequest).subscribe({
      next: (response) => {
        console.log('Link sent!', response);
        this.successMessage = 'Poslali smo vam mejl sa linkom za resetovanje lozinke'
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Link sending error:', err);
        const code = err.error?.code;

        switch (code) {
          case 'EMAIL_NOT_FOUND':
            this.errorMessage = 'Uneseni mejl nije pronadjen!';
            break;

          case 'RESET_TOO_SOON':
            this.errorMessage = 'Ne mozete resetovati lozinku vise od jedanput dnevno!';
            break;

          default:
            this.errorMessage = 'Nesto je poslo po zlu :(';
        }

        this.cdr.detectChanges();
      }
    });
  }

  resend(): void {
    this.errorMessage = '';
    this.successMessage = '';

    if (!this.email) {
      this.errorMessage = 'Molimo Vas unesite vasu e-adresu!';
      return;
    }

    this.emailRequest.email = this.email

    this.authService.sendRecoveryEmail(this.emailRequest).subscribe({
      next: (response) => {
        console.log('Link sent!', response);
        this.successMessage = 'Mejl je ponovo poslat! Ako ne vidite mejl, proverite spam folder'
        this.cdr.detectChanges();
      },
      error: (err) => {

        const code = err.error?.code;

        switch (code) {
          case 'EMAIL_NOT_FOUND':
            this.errorMessage = 'Uneseni mejl nije pronadjen!';
            break;

          case 'RESET_TOO_SOON':
            this.errorMessage = 'Ne mozete resetovati lozinku vise od jedanput dnevno!';
            break;

          default:
            this.errorMessage = 'Nesto je poslo po zlu :(';
        }

        this.cdr.detectChanges();
      }
    });
  }
}
