import { Routes } from '@angular/router';
import { LoginComponent } from './login-component/login-component';
import { RegisterComponent } from './register-component/register-component';
import { HomePageComponent } from './home-page-component/home-page-component';
import { ArtistAddComponent } from './artist/artist-add-component/artist-add-component';
import { ArtistPutComponent } from './artist/artist-put-component/artist-put-component';
import { AlbumAddComponent } from './album/album-add-component/album-add-component';
import { AlbumPutComponent } from './album/album-put-component/album-put-component';
import { SongAddComponent } from './song/song-add-component/song-add-component';
import { SongPutComponent } from './song/song-put-component/song-put-component';
import { ArtistsComponent } from './artist/artists-component/artists-component';
import { SongsGetByAlbumComponent } from './song/songs-get-by-album-component/songs-get-by-album-component';
import { Profile } from './profile/profile';

import { authGuard, guestGuard } from './auth-guard';
import {OtpLoginComponent} from './otp-login-component/otp-login-component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },

  {
    path: '',
    canActivate: [guestGuard],
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'register', component: RegisterComponent },
      {path: 'login/otp', component: OtpLoginComponent},
    ]
  },

  {
    path: '',
    canActivate: [authGuard],
    children: [
      { path: 'home', component: HomePageComponent },
      { path: 'profile', component: Profile },

      { path: 'artists', component: ArtistsComponent },
      { path: 'artists/add', component: ArtistAddComponent },
      { path: 'artists/put/:id', component: ArtistPutComponent },

      { path: 'albums/add', component: AlbumAddComponent },
      { path: 'albums/put/:id', component: AlbumPutComponent },
      { path: 'albums/:id/songs', component: SongsGetByAlbumComponent },

      { path: 'songs/add', component: SongAddComponent },
      { path: 'songs/put/:id', component: SongPutComponent },
    ]
  },

  { path: '**', redirectTo: 'home' }
];
