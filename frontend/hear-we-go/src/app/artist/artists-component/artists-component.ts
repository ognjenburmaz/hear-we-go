import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { Artist, ArtistService } from '../../services/artist-service';
import { FormsModule } from '@angular/forms';
@Component({
  selector: 'app-artists-component',
  imports: [RouterModule, FormsModule],
  templateUrl: './artists-component.html',
  styleUrl: './artists-component.css',
  standalone: true
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
   navigateToAdd() {
  this.router.navigate(['/artists/add']);
}
}

