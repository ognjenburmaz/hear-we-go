import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {  Router, RouterModule } from '@angular/router';

import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../../services/song-service';


@Component({
  selector: 'app-songs-component',
  imports: [RouterModule, FormsModule],
  templateUrl: './songs-component.html',
  styleUrl: './songs-component.css',
})
export class SongsComponent  implements OnInit
 {

  songs:Song[]=[];
  constructor(private router: Router,
      private cdr:ChangeDetectorRef,
      private service :SongService)

  {

  }
  ngOnInit(): void
        {

          this.LoadAllSongs();
        }
        LoadAllSongs():void
        {

             this.service.getAll().subscribe
          ({
           next:(songs:Song[])=>{

                this.songs=songs;
                this.cdr.detectChanges();

             },
            error:(_)=>console.log("greska")
             })
            }

     navigateToAdd() {
    this.router.navigate(['/songs/add']);
  }


}
