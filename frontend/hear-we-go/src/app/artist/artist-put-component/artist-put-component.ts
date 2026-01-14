import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule ,ActivatedRoute} from '@angular/router';

import { FormsModule } from '@angular/forms';
import { Artist, ArtistService } from '../../services/artist-service';


@Component({
  selector: 'app-artist-put-component',
  imports: [FormsModule, RouterModule],
  templateUrl: './artist-put-component.html',
  styleUrl: './artist-put-component.css',
})
export class ArtistPutComponent implements OnInit {
    name!: string;
    biography!: string;
    genres: string = '';
    errorMessage:string='';
    artist: Artist|undefined = undefined;
    artistId: string | null = null;
    artists: Artist[]=[];

    constructor(
      private route:ActivatedRoute,
      private router: Router,
      private cdr: ChangeDetectorRef,
        private service : ArtistService
    ) { }
  ngOnInit(): void
  {

    this.artistId = this.route.snapshot.paramMap.get('id');
    if(this.artistId!=null){
   this.service.getOne(this.artistId).subscribe
        ({
         next:(artist:Artist)=>{

              this.artist=artist;

              if(this.artist!=undefined)
           {
           this.name=this.artist.name
           this.biography=this.artist.biography
           this.genres=this.artist.genres.join(', ');
            this.cdr.detectChanges();
           }
           this.cdr.detectChanges();
           },
          error:(_)=>console.log("greska")
           })
          }

  }
    onSubmit(): void {
      this.errorMessage = '';

      if (this.name=='' || this.biography=='' || this.genres.length==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }

      const artist: Artist ={
           name: this.name.trim(),
            biography:this.biography.trim(),
            genres:this.genres.split(',')
          }
              if(this.artistId!=null){
               this.service.put(artist,this.artistId).subscribe({
              next:(artist: Artist) => {
                this.router.navigate(['artists'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
          }
    }

}
