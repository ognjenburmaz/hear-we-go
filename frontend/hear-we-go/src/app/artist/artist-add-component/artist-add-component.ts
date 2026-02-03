
import { Component, ChangeDetectorRef } from '@angular/core';
import { Router, RouterModule } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { Artist, ArtistService } from '../../services/artist-service';

@Component({
  selector: 'app-artist-add-component',
  imports: [FormsModule, RouterModule],
  templateUrl: './artist-add-component.html',
  styleUrl: './artist-add-component.css',
})
export class ArtistAddComponent

{
  name!: string;
    biography!: string;
    genres: string='';
    errorMessage:string='';
    zanrMessage:string='';
    imeMessage:string='';

    validation:boolean=true;

    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service : ArtistService
    ) { }

    onSubmit(): void {
      this.errorMessage = '';
      this.validate()

      if(this.validation){
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

  validate()
  { 


    this.validation = true;
    this.errorMessage = '';
    this.imeMessage = '';
    this.zanrMessage = '';

    if (!this.name || !this.biography || !this.genres || this.genres.length === 0) {
        this.errorMessage = 'Sva polja su obavezna!';
        this.validation = false;
    }

    const nameRegex = /^[\p{L}\d\s]+$/u;
    if (!nameRegex.test(this.name)) {
        this.imeMessage = 'Ime mora sadržati samo slova i brojeve.';
        this.validation = false;
    }

    const genreRegex = /^[\p{L}\d,\s]+$/u;
    
    if (!genreRegex.test(this.genres)) {
        this.zanrMessage = 'Specijalni karakteri nisu dozvoljeni.';
        this.validation = false;
    }

  }

}
