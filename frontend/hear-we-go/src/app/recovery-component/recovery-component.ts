import {ChangeDetectorRef, Component} from '@angular/core';
import {FormsModule} from "@angular/forms";
import {NgIf} from "@angular/common";
import {AuthService} from '../services/auth.service';
import {Router} from '@angular/router';
import {EmailRequest} from '../DTOs/emailRequest';

@Component({
  selector: 'app-recovery-component',
  imports: [
    FormsModule,
    NgIf
  ],
  templateUrl: './recovery-component.html',
  styleUrl: './recovery-component.css',
})
export class RecoveryComponent {

  username!: string;
  email!: string;
  errorMessage: string = '';
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

    if (!this.email) {
      this.errorMessage = 'Molimo Vas unesite vasu e-adresu!';
      return;
    }

    this.emailRequest.email = this.email

    this.authService.sendRecoveryEmail(this.emailRequest).subscribe({
      next: (response) => {
        console.log('Link sent!', response);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Link sending error:', err);
        this.errorMessage = 'Uneseni mejl nije pronadjen!';

        this.cdr.detectChanges();
      }
    });
  }


}
