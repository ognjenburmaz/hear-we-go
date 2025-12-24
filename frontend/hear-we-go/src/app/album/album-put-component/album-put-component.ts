import { CommonModule} from '@angular/common';
import { Component,ChangeDetectorRef } from '@angular/core'; 
import { FormsModule } from '@angular/forms';
import { RouterModule,Router } from '@angular/router';

@Component({
  selector: 'app-album-put-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './album-put-component.html',
  styleUrl: './album-put-component.css',
})
export class AlbumPutComponent {
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
