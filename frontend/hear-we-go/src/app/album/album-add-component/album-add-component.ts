import { Component,ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-album-add-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './album-add-component.html',
  styleUrl: './album-add-component.css',
})
export class AlbumAddComponent
 {
  title!: string;
    releaseDate!: string;
    genre!: string;
    artistIds: string[]=[];
    errorMessage:string='';
  
    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef
    ) { }
  
    onSubmit(): void {
      this.errorMessage = '';
  
      if (this.title=='' || this.releaseDate==null || this.genre==''||this.artistIds.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }
    }
}
