import { Component ,ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-song-put-component',
  imports: [RouterModule,CommonModule,FormsModule],
  templateUrl: './song-put-component.html',
  styleUrl: './song-put-component.css',
})
export class SongPutComponent 
{
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
  
      if ( this.title==''|| this.albumId==''||this.genre==''|| this.durationSeconds==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }
    }
}
