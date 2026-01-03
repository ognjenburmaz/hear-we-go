import { Component,ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Album, AlbumService } from '../../services/album-service';
import { Artist, ArtistService } from '../../services/artist-service';

@Component({
  selector: 'app-album-add-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './album-add-component.html',
  styleUrl: './album-add-component.css',
  standalone: true
})
export class AlbumAddComponent implements OnInit
 {
  title!: string;
    releaseDate!: string;
    genre!: string;
    artistIds: string[]=[];
    errorMessage:string='';
    artists:Artist[]=[];
    constructor(
      private router: Router,
      private cdr: ChangeDetectorRef,
      private service:AlbumService,
      private serviceArtists: ArtistService
    ) { }
    ngOnInit(): void
    {
        this.GetAllArtists() ;
    }



    GetAllArtists():void
    {
       this.serviceArtists.getAll().subscribe
              ({
               next:(artists:Artist[])=>{

                    this.artists=artists;
                    this.cdr.detectChanges();

                 },
                error:(_)=>console.log("greska")
                 })
    }
    onSubmit(): void {
      this.errorMessage = '';

      if (this.title=='' || this.releaseDate==null || this.genre==''||this.artistIds.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }

       const album: Album ={
           title: this.title.trim(),
            releaseDate:this.releaseDate.trim(),
            genre:this.genre.trim(),
            artistIds:this.artistIds
          }

               this.service.create(album).subscribe({
              next:(album: Album) => {
                this.router.navigate(['albums'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
    }

}
