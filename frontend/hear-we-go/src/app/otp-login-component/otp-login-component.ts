import {ChangeDetectorRef, Component} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {Router, RouterLink} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';


@Component({
  selector: 'app-otp-login-component',
  imports: [
    FormsModule,
    ReactiveFormsModule,
    RouterLink
],
  templateUrl: './otp-login-component.html',
  styleUrl: './otp-login-component.css',
})
export class OtpLoginComponent {
  username!: string;
  otp!: string;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
  }

  onLogin(): void {
    this.errorMessage = '';

    if (!this.otp) {
      this.errorMessage = 'Molimo Vas unesite kod!';
      return;
    }

    const credentials = {username: localStorage.getItem("username"), password: this.otp};

    this.authService.otplogin(credentials).subscribe({
      next: (response) => {
        console.log('Successfully logged in!', response);
        this.router.navigate(['/home']);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = 'Pogrešan kod!';

        this.cdr.detectChanges();
      }
    });
  }

}
