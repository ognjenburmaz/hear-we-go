import { CommonModule} from '@angular/common';
import { Component,ChangeDetectorRef, OnInit } from '@angular/core'; 
import { FormsModule } from '@angular/forms';
import { RouterModule,Router, ActivatedRoute } from '@angular/router';
import { Album, AlbumService } from '../album-service';
import { Artist, ArtistService } from '../../artist/artist-service';

@Component({
  selector: 'app-album-put-component',
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './album-put-component.html',
  styleUrl: './album-put-component.css',
})
export class AlbumPutComponent implements OnInit{
title!: string;
    releaseDate!: string;
    genre!: string;
    artistIds: string[]=[];
    errorMessage:string='';
    albumId: string | null = null;
    album:Album|undefined=undefined;

    artists:Artist[]=[]
  
    constructor(
       private route:ActivatedRoute,
      private router: Router,
      private cdr: ChangeDetectorRef,
       private service:AlbumService,
       private serviceArtists:ArtistService
    ) { }
  ngOnInit(): void 
  {
    this.GetAllArtists();
      this.albumId = this.route.snapshot.paramMap.get('id');
       if(this.albumId!=null){
   this.service.getOne(this.albumId).subscribe
        ({
         next:(album:Album)=>{
             
              this.album=album;
        
              if(this.album!=undefined)
           {
           this.title=this.album.title
           this.releaseDate=this.album.releaseDate
           this.genre=this.album.genre;
           this.artistIds=this.album.artistIds;
            this.cdr.detectChanges();
           }
           this.cdr.detectChanges();

           },  
          error:(_)=>console.log("greska")
           })
          }

          
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
            if(this.albumId!=null){
                     this.service.put(album,this.albumId).subscribe({
                    next:(album: Album) => {
                      this.router.navigate(['albums'])
                    },
                    error:(_) => {
                      console.log("Greska!")
                    }
                  })
                }
    }
}
