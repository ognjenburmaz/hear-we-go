import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SongAddComponent } from '../song/song-add-component/song-add-component';
import { SongPutComponent } from '../song/song-put-component/song-put-component';
import { SongsGetByAlbumComponent } from '../song/songs-get-by-album-component/songs-get-by-album-component';
import { RouterModule } from '@angular/router';
import { SongsGetByAlbumUserComponent } from '../song/songs-get-by-album-user-component/songs-get-by-album-user-component';



@NgModule({
  declarations: [],
  imports: [
    RouterModule,
    CommonModule,
    SongAddComponent,
    SongPutComponent,
    SongsGetByAlbumComponent,
    SongsGetByAlbumUserComponent
  ],
  exports:[
    SongAddComponent,
    SongPutComponent,
    SongsGetByAlbumComponent,
    SongsGetByAlbumUserComponent
  ]
})
export class SongModule { }
