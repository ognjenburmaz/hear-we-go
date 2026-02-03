
import { Component,ChangeDetectorRef, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule,Router, ActivatedRoute } from '@angular/router';
import { Album, AlbumService } from '../../services/album-service';
import { Artist, ArtistService } from '../../services/artist-service';

@Component({
  selector: 'app-album-put-component',
  imports: [FormsModule, RouterModule],
  templateUrl: './album-put-component.html',
  styleUrl: './album-put-component.css',
  standalone: true
})
export class AlbumPutComponent implements OnInit{
title!: string;
    releaseDate!: string;
    genre!: string;
    artistIds: string[]=[];
    errorMessage:string='';
    titleMessage:string='';
    genreMessage:string='';
    dateMessage:string='';
    validation:boolean=true;
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
      this.validate()
      if(this.validation){
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

    validate()
  { 


    this.validation = true;
    this.errorMessage = '';
    this.titleMessage = '';
    this.genreMessage = '';

     if (this.title=='' || this.releaseDate==null || this.genre==''||this.artistIds.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        this.validation=false;
        
      }

    const nameRegex = /^[\p{L}\d\s]+$/u;
    if (!nameRegex.test(this.title)) {
        this.titleMessage = 'Naziv mora sadržati samo slova i brojeve.';
        this.validation = false;
    }

    const genreRegex = /^[\p{L}\d\s]+$/u;
    
    if (!genreRegex.test(this.genre)) {
        this.genreMessage = 'Specijalni karakteri nisu dozvoljeni.';
        this.validation = false;
    }

    const inputDate = new Date(this.releaseDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    if (inputDate > today) {
        this.dateMessage = 'Datum ne može biti u budućnosti!';
        this.validation = false;
    }
  }
}
