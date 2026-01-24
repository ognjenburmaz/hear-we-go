import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {RegistrationRequestsService} from '../services/registration-requests-service';
import {ActivatedRoute, Router} from '@angular/router';
import {User} from '../services/auth.service';

@Component({
  selector: 'app-registration-requests',
  imports: [],
  templateUrl: './registration-requests.html',
  styleUrl: './registration-requests.css',
})
export class RegistrationRequests implements OnInit {

  registrationRequests: User[] = []

  constructor(private service: RegistrationRequestsService, private route: ActivatedRoute, private router: Router, private cdr: ChangeDetectorRef) {
  }


  ngOnInit(): void {
    this.getAllRequests();
  }

  getAllRequests(): void {
    this.service.getAll().subscribe({
      next: (registrationRequests: User[]) => {
        this.registrationRequests = registrationRequests
        console.log(this.registrationRequests)
        this.cdr.detectChanges()
      },
      error: (_) => {
        console.error("GRESKA!")
        this.cdr.detectChanges()

      }
    })
  }

  accept(id: string | undefined): void {
    this.service.accept(id).subscribe({
      next: () => {
        this.router.navigate(['registrationrequests'])
        this.getAllRequests()
        console.log("USPEH!")
        this.cdr.detectChanges()
      }
    });
  }

  reject(id: string | undefined): void {
    this.service.reject(id).subscribe({
      next: () => {
        this.router.navigate(['registrationrequests'])
        this.getAllRequests()
        console.log("USPEH U ODBIJANJU!")
        this.cdr.detectChanges()
      }
    });
  }

}
