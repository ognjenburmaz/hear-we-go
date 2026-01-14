import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../../services/song-service';
@Component({
  selector: 'app-songs-get-by-album-component',
  imports: [RouterModule, FormsModule],
  templateUrl: './songs-get-by-album-component.html',
  styleUrl: './songs-get-by-album-component.css',
})
export class SongsGetByAlbumComponent implements OnInit
 {

  songs:Song[]=[];
  albumId:string|null=null;
  constructor(private router: Router,
      private cdr:ChangeDetectorRef,
      private service :SongService,
      private route:ActivatedRoute)
  {

  }
  ngOnInit(): void
        {
          this.albumId = this.route.snapshot.paramMap.get('id');
          this.LoadAllSongs();
        }
        LoadAllSongs():void
        {
          if(this.albumId!=null){
             this.service.getAllByAlbum(this.albumId).subscribe
          ({
           next:(songs:Song[])=>{

                this.songs=songs;
                this.cdr.detectChanges();

             },
            error:(_)=>console.log("greska")
             })
            }
        }
     navigateToAdd() {
    this.router.navigate(['/songs/add']);
  }
}
