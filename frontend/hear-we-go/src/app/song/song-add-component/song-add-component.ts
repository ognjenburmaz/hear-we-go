import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../../services/song-service';
import { Album, AlbumService } from '../../services/album-service';


@Component({
  selector: 'app-song-add-component',
  imports: [CommonModule,FormsModule,RouterModule],
  templateUrl: './song-add-component.html',
  styleUrl: './song-add-component.css',
})
export class SongAddComponent implements OnInit {
  file: File | null = null;
  title!:string;
  albumId!:string;
  genre!:string;
  durationSeconds!:number;
errorMessage:string='';

albums:Album[]=[];
 constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service:SongService,
      private serviceAlbums:AlbumService
    ) { }

ngOnInit(): void
{
 this.LoadAllAlbums();
}


LoadAllAlbums():void
{

    this.serviceAlbums.getAll().subscribe
            ({
             next:(albums:Album[])=>{

                    this.albums=albums;
                    this.cdr.detectChanges();

                 },
                error:(_)=>console.log("greska")
                 })

}




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
                this.router.navigate(['songs'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
    }
}
