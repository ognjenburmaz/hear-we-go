import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AlbumAddComponent } from './album-add-component/album-add-component';
import { AlbumPutComponent } from './album-put-component/album-put-component';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    RouterModule,
    AlbumAddComponent,
    AlbumPutComponent
  ],
  exports:
  [
    AlbumAddComponent,
    AlbumPutComponent
  ]
})
export class AlbumModule {

  
 }
