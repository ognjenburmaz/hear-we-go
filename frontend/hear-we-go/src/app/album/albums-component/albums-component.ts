import { Component, ChangeDetectorRef, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Album, AlbumService } from '../../services/album-service';

@Component({
  selector: 'app-albums-component',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './albums-component.html',
  styleUrl: './albums-component.css',
})
export class AlbumsComponent implements OnInit
{
albums:Album[]=[]
   constructor(
      private router: Router,
      private cdr:ChangeDetectorRef,
      private service : AlbumService
      )

      {

      }

      ngOnInit(): void {
        this.LoadAllAlbums();
      }
      LoadAllAlbums():void
{

    this.service.getAll().subscribe
            ({
             next:(albums:Album[])=>{

                    this.albums=albums;
                    this.cdr.detectChanges();

                 },
                error:(_)=>console.log("greska")
                 })

}

        navigateToAdd() {
  this.router.navigate(['/albums/add']);
}
}
