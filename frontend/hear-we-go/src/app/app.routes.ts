import { Routes } from '@angular/router';
import {LoginComponent} from './login-component/login-component';
import {RegisterComponent} from './register-component/register-component';
import {HomePageComponent} from './home-page-component/home-page-component';
import { ArtistAddComponent } from './artist/artist-add-component/artist-add-component';
import { ArtistPutComponent } from './artist/artist-put-component/artist-put-component';
import { AlbumAddComponent } from './album/album-add-component/album-add-component';
import { AlbumPutComponent } from './album/album-put-component/album-put-component';
import { SongAddComponent } from './song/song-add-component/song-add-component';
import { SongPutComponent } from './song/song-put-component/song-put-component';
import { ArtistsComponent } from './artist/artists-component/artists-component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent},
  { path: 'home', component: HomePageComponent},
  { path: 'artists', component: ArtistsComponent},
  { path: 'artists/add', component: ArtistAddComponent},
  { path: 'artists/put', component: ArtistPutComponent},
  { path: 'albums/add', component: AlbumAddComponent},
  { path: 'albums/put', component: AlbumPutComponent},
   { path: 'songs/add', component: SongAddComponent},
      { path: 'songs/put', component: SongPutComponent},
];
