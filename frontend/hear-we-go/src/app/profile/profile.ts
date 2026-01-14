import { Component, OnInit } from '@angular/core';

import { AuthService } from '../services/auth.service';
import {NotificationListComponent} from '../notification/notification-list/notification-list';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [NotificationListComponent],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class Profile implements OnInit {
  userData: any = null;

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.userData = this.authService.getUserInfo();
    console.log('Podaci sa profila:', this.userData);
  }
}
