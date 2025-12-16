import { Component, ChangeDetectorRef } from '@angular/core'; // <--- 1. Import
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../auth/auth.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-register-component',
  imports: [ CommonModule, FormsModule, RouterModule ],
  templateUrl: './register-component.html',
  styleUrl: './register-component.css',
})
export class RegisterComponent {
  firstName!: string;
  lastName!: string;
  email!: string;
  username!: string;
  password!: string;

  errorMessage: string = '';

  // 2. Ubaci cdr u konstruktor
  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) { }

  onRegister(): void {
    this.errorMessage = '';

    const cleanFirstName = this.firstName?.trim();
    const cleanLastName = this.lastName?.trim();
    const cleanEmail = this.email?.trim();
    const cleanUsername = this.username?.trim();
    const cleanPassword = this.password?.trim();

    if (!cleanFirstName || !cleanLastName || !cleanEmail || !cleanUsername || !cleanPassword) {
      this.errorMessage = 'Sva polja su obavezna';
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(cleanEmail)) {
      this.errorMessage = 'Unesite validnu email adresu.';
      return;
    }

    if (cleanPassword.length <= 8) {
      this.errorMessage = 'Lozinka mora biti duža od 8 karaktera.';
      return;
    }

    const registrationData = {
      firstName: cleanFirstName,
      lastName: cleanLastName,
      email: cleanEmail,
      username: cleanUsername,
      password: cleanPassword
    };

    this.authService.register(registrationData).subscribe({
      next: (response) => {
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Registration error:', err);
        this.errorMessage = 'Registracija nije uspela. Proverite podatke ili pokušajte kasnije.';

        this.cdr.detectChanges();
      }
    });
  }
}
