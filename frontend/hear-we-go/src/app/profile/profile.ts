import {ChangeDetectorRef, Component, OnInit} from '@angular/core';

import {AuthService} from '../services/auth.service';
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
  emailRequest: EmailRequest = {
    email: this.email
  }
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private authService: AuthService, private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.userData = this.authService.getUserInfo();
    this.email = localStorage.getItem("email")
    this.emailRequest.email = this.email;
    console.log('Podaci sa profila:', this.userData);
  }

  changePsw(): void {
    this.authService.sendRecoveryEmail(this.emailRequest).subscribe({
      next: (response) => {
        console.log('Link sent!', response);
        this.successMessage = 'Poslali smo vam mejl sa linkom za resetovanje lozinke'
        this.cdr.detectChanges();

        // this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Link sending error:', err);
        this.errorMessage = 'Uneseni mejl nije pronadjen!';

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

        // this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Link sending error:', err);
        this.errorMessage = 'Uneseni mejl nije pronadjen!';

        this.cdr.detectChanges();
      }
    });
  }
}
