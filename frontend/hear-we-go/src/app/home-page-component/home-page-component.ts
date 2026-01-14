import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Router } from 'express';
import { Artist, ArtistService } from '../services/artist-service';

@Component({
  selector: 'app-home-page-component',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './home-page-component.html',
  styleUrl: './home-page-component.css',
})
export class HomePageComponent implements OnInit{
  artists:Artist[]=[]
  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr:ChangeDetectorRef,
    private service : ArtistService
  ) { }
  ngOnInit(): void
   {
    this.LoadAllArtists();
  }

  LoadAllArtists():void
        {
             this.service.getAll().subscribe
          ({
           next:(artists:Artist[])=>{
  
                this.artists=artists;
                this.cdr.detectChanges();
  
             },
            error:(_)=>console.log("greska")
             })
        }

  onLogout(): void {
    this.authService.logout();
  }

  cardClick():void
  {

  }
}
