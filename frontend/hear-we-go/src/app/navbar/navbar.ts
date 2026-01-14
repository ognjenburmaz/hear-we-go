import { Component, HostListener, ElementRef } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../services/notification-service'

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css'
})
export class NavbarComponent {
  dropdownOpen = false;

  constructor(
    private elementRef: ElementRef,
    public authService: AuthService,
    public notificationService: NotificationService
  ) {}

  onLogout(): void {
    this.authService.logout();
  }

  get isAdmin(): boolean {
    // @ts-ignore
    return this.authService.isAuthenticated() && this.authService.getUserInfo().role === 'ADMIN';
  }

  toggleDropdown(): void {
    this.dropdownOpen = !this.dropdownOpen;
  }

  closeDropdown(): void {
    this.dropdownOpen = false;
  }

  @HostListener('document:click', ['$event.target'])
  onClickOutside(targetElement: EventTarget | null) {
    if (!targetElement) {
      return;
    }

    if (targetElement instanceof HTMLElement) {
      const clickedInside = this.elementRef.nativeElement.contains(targetElement);
      if (!clickedInside) {
        this.closeDropdown();
      }
    }
  }

}
