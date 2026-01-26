import { Component ,ChangeDetectorRef, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../../services/song-service';
import { Album, AlbumService } from '../../services/album-service';

@Component({
  selector: 'app-song-put-component',
  imports: [RouterModule, FormsModule],
  templateUrl: './song-put-component.html',
  styleUrl: './song-put-component.css',
})
export class SongPutComponent implements OnInit
{
  title!:string;
  albumId!:string;
  genre!:string;
  durationSeconds!:number;
  songId:string|null=null;
   titleMessage:string='';
    genreMessage:string='';
    validation:boolean=true;

  song:Song|undefined=undefined
  albums:Album[]=[]
errorMessage:string='';
 constructor(
  private route:ActivatedRoute,
      private router: Router,
      private cdr: ChangeDetectorRef,
        private service:SongService,
        private serviceAlbums:AlbumService
    ) { }
    ngOnInit(): void {
      this.songId = this.route.snapshot.paramMap.get('id');
      this.LoadAllAlbums();
      this.LoadSelectedSong();

    }
 LoadSelectedSong()
 {
 if(this.songId!=null){
   this.service.getOne(this.songId).subscribe
        ({
         next:(song:Song)=>{

              this.song=song;

              if(this.song!=undefined)
           {
           this.title=this.song.title
           this.durationSeconds=this.song.durationSeconds
           this.genre=this.song.genre;
           this.albumId=this.song.albumId;
            this.cdr.detectChanges();
           }
           this.cdr.detectChanges();

           },
          error:(_)=>console.log("greska")
           })
          }
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



     onSubmit(): void {

      this.validate()
      if(this.validation)
      {
            if(this.songId!=null){
             const song: Song ={
                 title: this.title.trim(),
                  durationSeconds:this.durationSeconds,
                  genre:this.genre.trim(),
                  albumId:this.albumId.trim(),
                }
                 this.service.put(song,this.songId).subscribe({
              next:(song: Song) => {
                this.router.navigate(['songs'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
          }
        }
    }

    validate()
  { 


    this.validation = true;
    this.errorMessage = '';
    this.titleMessage = '';
    this.genreMessage = '';

     if ( this.title == '' || this.albumId == '' || this.genre == '' || this.durationSeconds == 0) {
      this.errorMessage = 'Sva polja su obavezna!';
      this.validation=false;
    }

    const nameRegex = /^[\p{L}\d\s]+$/u;
    if (!nameRegex.test(this.title)) {
        this.titleMessage = 'Specijalni karakteri nisu dozvoljeni.';
        this.validation = false;
    }

    const genreRegex = /^[\p{L}\d\s]+$/u;
    
    if (!genreRegex.test(this.genre)) {
        this.genreMessage = 'Specijalni karakteri nisu dozvoljeni.';
        this.validation = false;
    }

    }
}
