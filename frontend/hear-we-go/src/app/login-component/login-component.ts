import {ChangeDetectorRef, Component} from '@angular/core'; // <--- 1. Importuj ChangeDetectorRef
import {Router, RouterModule} from '@angular/router';
import {AuthService} from '../auth/auth.service';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-login-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
  standalone: true
})
export class LoginComponent {
  username!: string;
  password!: string;
  errorMessage: string = '';

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {
  }

  onLogin(): void {
    this.errorMessage = '';

    if (!this.username || !this.password) {
      this.errorMessage = 'Molimo unesite korisničko ime i lozinku.';
      return;
    }

    const credentials = {username: this.username, password: this.password};

    this.authService.pswlogin(credentials).subscribe({
      next: (response) => {
        console.log('Successfully logged in!', response);
        localStorage.setItem("username", this.username)
        this.router.navigate(['/login/otp']);
      },
      error: (err) => {
        console.error('Login error:', err);
        this.errorMessage = 'Pogrešno korisničko ime ili lozinka.';

        this.cdr.detectChanges();
      }
    });
  }
}
