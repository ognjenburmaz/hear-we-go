import { CommonModule} from '@angular/common';
import { Component,ChangeDetectorRef, OnInit } from '@angular/core'; 
import { FormsModule } from '@angular/forms';
import { RouterModule,Router, ActivatedRoute } from '@angular/router';
import { Album, AlbumService } from '../album-service';

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
    artistIds!: string;
    errorMessage:string='';
    albumId: string | null = null;
  
    constructor(
       private route:ActivatedRoute,
      private router: Router,
      private cdr: ChangeDetectorRef,
       private service:AlbumService
    ) { }
  ngOnInit(): void 
  {
      this.albumId = this.route.snapshot.paramMap.get('id');
  }
    onSubmit(): void {
      this.errorMessage = '';
  
      if (this.title=='' || this.releaseDate==null || this.genre==''||this.artistIds=='') {
        this.errorMessage = 'Sva polja su obavezna!';
        return;
      }

      const album: Album ={
                 title: this.title.trim(),
                  releaseDate:this.releaseDate.trim(),
                  genre:this.genre.trim(),
                  artistIds:this.artistIds.split(',')
                }
            if(this.albumId!=null){
                     this.service.put(album,this.albumId).subscribe({
                    next:(album: Album) => {
                      this.router.navigate(['home'])
                    },
                    error:(_) => {
                      console.log("Greska!")
                    }
                  })
                }
    }
}
