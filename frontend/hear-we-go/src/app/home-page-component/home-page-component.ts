import { Component } from '@angular/core';
import { AuthService } from '../auth/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home-page-component',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './home-page-component.html',
  styleUrl: './home-page-component.css',
})
export class HomePageComponent {

  constructor(private authService: AuthService) { }

  onLogout(): void {
    this.authService.logout();
  }
}
