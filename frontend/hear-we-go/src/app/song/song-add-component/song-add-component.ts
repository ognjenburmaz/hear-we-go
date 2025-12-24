import { Component, ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';


@Component({
  selector: 'app-song-add-component',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './song-add-component.html',
  styleUrl: './song-add-component.css',
})
export class SongAddComponent {
  file!: any
  title!:string;
  albumId!:string;
  genre!:string;
  durationSeconds!:number;
errorMessage:string='';
 constructor(
      private router: Router,
      private cdr: ChangeDetectorRef
    ) { }

     onSubmit(): void {
      this.errorMessage = '';
  
      if (this.file==null || this.title==''|| this.albumId==''||this.genre==''|| this.durationSeconds==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }
    }
}
