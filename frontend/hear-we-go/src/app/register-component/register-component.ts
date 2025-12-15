import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth/auth.service'; // Pretpostavljam putanju
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register-component',
  imports: [ CommonModule, FormsModule ],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css',
})
export class RegisterComponent {
  firstName!: string;
  lastName!: string;
  email!: string;
  username!: string;
  password!: string;

  constructor(private authService: AuthService, private router: Router) { }

  onRegister(): void {
    const registrationData = {
      firstName: this.firstName,
      lastName: this.lastName,
      email: this.email,
      username: this.username,
      password: this.password
    };

    // Poziv servisa za registraciju (treba implementirati u AuthService)
    this.authService.register(registrationData).subscribe({
      next: (response) => {
        console.log('Successfully registered!', response);
        this.router.navigate(['/login']); // Preusmeri na login nakon uspešne registracije
      },
      error: (err) => {
        console.error('Registration error:', err);
        alert('Registration failed. Please check your data.');
      }
    });
  }
}
