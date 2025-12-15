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

  constructor(private authService: AuthService, private router: Router) { }

  onLogin(): void {
    const credentials = { username: this.username, password: this.password };

    this.authService.login(credentials).subscribe({
      next: (token) => {
        console.log('Successfully logged in, token:', token);
      },
      error: (err) => {
        console.error('Login error in component:', err);
      }
    });
  }
}
