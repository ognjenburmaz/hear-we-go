import { Component ,ChangeDetectorRef, OnInit } from '@angular/core'; 
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../song-service';

@Component({
  selector: 'app-song-put-component',
  imports: [RouterModule,CommonModule,FormsModule],
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
errorMessage:string='';
 constructor(
  private route:ActivatedRoute,
      private router: Router,
      private cdr: ChangeDetectorRef,
        private service:SongService
    ) { }
    ngOnInit(): void {
      this.songId = this.route.snapshot.paramMap.get('id');
    }
     onSubmit(): void {
      this.errorMessage = '';
  
      if ( this.title==''|| this.albumId==''||this.genre==''|| this.durationSeconds==0) {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }
            if(this.songId!=null){
             const song: Song ={
                 title: this.title.trim(),
                  durationSeconds:this.durationSeconds,
                  genre:this.genre.trim(),
                  albumId:this.albumId.trim(),
                }
                 this.service.put(song,this.songId).subscribe({
              next:(song: Song) => {
                this.router.navigate(['home'])
              },
              error:(_) => {
                console.log("Greska!")
              }
            })
          }
    }
}
