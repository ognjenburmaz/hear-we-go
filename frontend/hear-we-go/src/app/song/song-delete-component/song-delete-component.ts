import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import {  ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Song, SongService } from '../song-service';


@Component({
  selector: 'app-song-delete-component',
  imports: [CommonModule,RouterModule,FormsModule],
  templateUrl: './song-delete-component.html',
  styleUrl: './song-delete-component.css',
})
export class SongDeleteComponent implements OnInit {
  songId:string|null=null;
  constructor(private router: Router,
      private cdr:ChangeDetectorRef,
      private service :SongService,
      private route:ActivatedRoute)
 
  {

}
ngOnInit(): void {


   this.songId = this.route.snapshot.paramMap.get('id');
   if(this.songId!=null){
  this.service.delete(this.songId).subscribe({
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
