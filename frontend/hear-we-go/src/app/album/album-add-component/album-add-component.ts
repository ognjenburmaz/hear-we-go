import { Component,ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Album, AlbumService } from '../album-service';

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
    artistIds!: string;
    errorMessage:string='';
  
    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service:AlbumService
    ) { }
  
    onSubmit(): void {
      this.errorMessage = '';
  
      if (this.title=='' || this.releaseDate==null || this.genre==''||this.artistIds=='') {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }

       const album: Album ={
           title: this.title.trim(),
            releaseDate:this.releaseDate.trim(),
            genre:this.genre.trim(),
            artistIds:this.artistIds.split(',')
          }
      
               this.service.create(album).subscribe({
              next:(album: Album) => {
                this.router.navigate(['artists'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
    }

}
