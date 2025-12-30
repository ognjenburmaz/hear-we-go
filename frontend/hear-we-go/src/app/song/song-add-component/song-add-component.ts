import { Component, ChangeDetectorRef } from '@angular/core'; 
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../song-service';


@Component({
  selector: 'app-song-add-component',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './song-add-component.html',
  styleUrl: './song-add-component.css',
})
export class SongAddComponent {
  file: File | null = null;
  title!:string;
  albumId!:string;
  genre!:string;
  durationSeconds!:number;
errorMessage:string='';
 constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service:SongService
    ) { }
onFileSelected(event: any) {
  const file: File = event.target.files[0];
  

  if (file) {
    this.file = file;
    console.log("File selected:", file.name);
  }
}
     onSubmit(): void {
      this.errorMessage = '';
  
      if (this.file==null || this.title==''|| this.albumId==''||this.genre==''|| this.durationSeconds==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }


       const song: Song ={
           title: this.title.trim(),
            durationSeconds:this.durationSeconds,
            genre:this.genre.trim(),
            albumId:this.albumId.trim(),
          }
          const songBlob = new Blob([JSON.stringify(song)], {
      type: 'application/json'
        });
          const formData = new FormData();
          formData.append('song',songBlob);
          formData.append('file',this.file);
        
               this.service.create(formData).subscribe({
              next:(song: Song) => {
                this.router.navigate(['home'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
    }
}
