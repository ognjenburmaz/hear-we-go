import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-login-component',
  imports: [ CommonModule, FormsModule ],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {
  username!: string;
  password!: string;
  errorMessage: string = '';

  constructor(private authService: AuthService, private router: Router) { }

  onLogin(): void {
    // 1. Resetuj grešku pre slanja
    this.errorMessage = '';

    if (!this.username || !this.password) {
      this.errorMessage = 'Molimo unesite korisničko ime i lozinku.';
      return;
    }

    const credentials = { username: this.username, password: this.password };

    this.authService.login(credentials).subscribe({
      next: (response) => {
        // Ako je uspesno
        console.log('Successfully logged in!', response);
        this.router.navigate(['/home']);
      },
      error: (err) => {

        console.error('Login error:', err);
        this.errorMessage = 'Pogrešno korisničko ime ili lozinka.';
      }
    });
  }
}
