import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ArtistsComponent } from './artists-component/artists-component';
import { ArtistAddComponent } from './artist-add-component/artist-add-component';
import { ArtistPutComponent } from './artist-put-component/artist-put-component';
import { RouterModule } from '@angular/router';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule,
    ArtistsComponent,
    ArtistAddComponent,
    ArtistPutComponent,
  
    
    
  ],
  exports:
  [
    ArtistsComponent,
    ArtistAddComponent,
    ArtistPutComponent,

  ]
})
export class ArtistModule { }
