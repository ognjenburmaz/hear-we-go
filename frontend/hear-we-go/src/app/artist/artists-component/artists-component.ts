import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Artist, ArtistService } from '../artist-service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-artists-component',
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './artists-component.html',
  styleUrl: './artists-component.css',
})



export class ArtistsComponent implements OnInit {

    artists:Artist[]=[]
   constructor(
      private router: Router,
      private cdr:ChangeDetectorRef,
      private service : ArtistService
      )

      {

      }

      ngOnInit(): void {
        this.service.getAll().subscribe
   ({
      next:(artists:Artist[])=>{
          this.artists=artists;
          
        },  
      error:(_)=>console.log("greska")
    })
      }
}

