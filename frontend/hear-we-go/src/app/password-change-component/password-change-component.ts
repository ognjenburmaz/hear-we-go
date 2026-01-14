import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';

import {AuthService} from '../services/auth.service';
import {ActivatedRoute, Router} from '@angular/router';
import {PswChangeRequest} from '../DTOs/pswRequest';

@Component({
  selector: 'app-password-change-component',
  imports: [
    FormsModule
],
  templateUrl: './password-change-component.html',
  styleUrl: './password-change-component.css',
  standalone: true
})
export class PasswordChangeComponent implements OnInit {

  password!: string;
  recoveryHash!: string;
  errorMessage: string = '';
  pswRequest: PswChangeRequest = {
    recoveryHash: this.recoveryHash,
    newPassword: this.password
  }

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {
  }
  // emailRequest: EmailRequest = {
  //   email: this.password
  // }

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      const recoveryHash = params['recoveryHash'];
      this.recoveryHash = recoveryHash;
      console.log(recoveryHash);
    });
  }

  onSubmit(): void {
    this.errorMessage = '';

    if (!this.password) {
      this.errorMessage = 'Molimo Vas unesite vasu e-adresu!';
      return;
    }

    this.pswRequest.recoveryHash = this.recoveryHash
    this.pswRequest.newPassword = this.password

    this.authService.changePassword(this.pswRequest).subscribe({
      next: (response) => {
        console.log('Password Changed! ', response);
        this.router.navigate(['/login']);
      },
      error: (err) => {
        console.error('Password change error: ', err);
        this.errorMessage = 'Greska!';

        this.cdr.detectChanges();
      }
    });
  }

}
