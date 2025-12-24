import { Component, ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-artist-put-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './artist-put-component.html',
  styleUrl: './artist-put-component.css',
})
export class ArtistPutComponent {
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
