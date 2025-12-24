
import { Component, ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


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
    genres: string[] = [];
    errorMessage:string='';
  
    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef
    ) { }
  
    onSubmit(): void {
      this.errorMessage = '';
  
      if (this.name=='' || this.biography=='' || this.genres.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }
    }
}
