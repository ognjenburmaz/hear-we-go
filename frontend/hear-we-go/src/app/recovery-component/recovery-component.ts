import {ChangeDetectorRef, Component} from '@angular/core';
import {FormsModule} from "@angular/forms";

import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';
import {EmailRequest} from '../DTOs/emailRequest';

@Component({
  selector: 'app-recovery-component',
  imports: [
    FormsModule
  ],
  templateUrl: './recovery-component.html',
  styleUrl: './recovery-component.css',
})
export class RecoveryComponent {

  username!: string;
  email!: string;
  errorMessage: string = '';
  successMessage: string = '';
  emailRequest: EmailRequest = {
    email: this.email
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
  }

  onSubmit(): void {
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
        this.successMessage = 'Poslali smo vam mejl sa linkom za resetovanje lozinke'
        this.cdr.detectChanges();

        // this.router.navigate(['/login']);
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

        // this.router.navigate(['/login']);
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


        // console.error('Link sending error:', err);
        // this.errorMessage = 'Uneseni mejl nije pronadjen!';

        this.cdr.detectChanges();
      }
    });
  }


}
