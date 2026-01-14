import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AlbumAddComponent } from '../album/album-add-component/album-add-component';
import { AlbumPutComponent } from '../album/album-put-component/album-put-component';
import { AlbumsGetByArtistComponent } from '../album/albums-get-by-artist-component/albums-get-by-artist-component';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule,
    AlbumAddComponent,
    AlbumPutComponent,
    AlbumsGetByArtistComponent
  ],
  exports:
  [
    AlbumAddComponent,
    AlbumPutComponent,
    AlbumsGetByArtistComponent
  ]
})
export class AlbumModule {


 }
