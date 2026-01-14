import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { Router } from 'express';
import { Album, AlbumService } from '../../services/album-service';

@Component({
  selector: 'app-albums-get-by-artist-component',
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './albums-get-by-artist-component.html',
  styleUrl: './albums-get-by-artist-component.css',
})
export class AlbumsGetByArtistComponent implements OnInit{

  albums:Album[]=[];
    artistId:string|null=null;
     constructor(private router: Router,
      private cdr:ChangeDetectorRef,
      private service :AlbumService,
      private route:ActivatedRoute)
  {

  }

    ngOnInit(): void 
    {
      this.artistId = this.route.snapshot.paramMap.get('id');
                this.LoadAllSongs();
              }
              LoadAllSongs():void
              {
                if(this.artistId!=null){
                   this.service.getAllByArtist(this.artistId).subscribe
                ({
                 next:(albums:Album[])=>{
      
                      this.albums=albums;
                      this.cdr.detectChanges();
      
                   },
                  error:(_)=>console.log("greska")
                   })
                  }
    }

}
