import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongAddComponent } from './song-add-component/song-add-component';
import { SongPutComponent } from './song-put-component/song-put-component';
import { SongsGetByAlbumComponent } from './songs-get-by-album-component/songs-get-by-album-component';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [],
  imports: [
    RouterModule,
    CommonModule,
    SongAddComponent,
    SongPutComponent,
    SongsGetByAlbumComponent
  ],
  exports:[
    SongAddComponent,
    SongPutComponent,
    SongsGetByAlbumComponent
  ]
})
export class SongModule { }
