
import { Component, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Artist, ArtistService } from '../../services/artist-service';

@Component({
  selector: 'app-artist-add-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './artist-add-component.html',
  styleUrl: './artist-add-component.css',
})
export class ArtistAddComponent

{
  name!: string;
    biography!: string;
    genres: string='';
    errorMessage:string='';

    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service : ArtistService
    ) { }

    onSubmit(): void {
      this.errorMessage = '';

      if (this.name=='' || this.biography=='' || this.genres.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }


    const artist: Artist ={
     name: this.name.trim(),
      biography:this.biography.trim(),
      genres:this.genres.split(',')
    }

         this.service.create(artist).subscribe({
        next:(artist: Artist) => {
          this.router.navigate(['artists'])
        },
        error:(_) => {
          console.log("Greska!")
        }
      })
  }

}
