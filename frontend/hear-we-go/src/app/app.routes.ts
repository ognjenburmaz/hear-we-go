import {Routes} from '@angular/router';
import {LoginComponent} from './login-component/login-component';
import {RegisterComponent} from './register-component/register-component';
import {HomePageComponent} from './home-page-component/home-page-component';
import {ArtistAddComponent} from './artist/artist-add-component/artist-add-component';
import {ArtistPutComponent} from './artist/artist-put-component/artist-put-component';
import {AlbumAddComponent} from './album/album-add-component/album-add-component';
import {AlbumPutComponent} from './album/album-put-component/album-put-component';
import {SongAddComponent} from './song/song-add-component/song-add-component';
import {SongPutComponent} from './song/song-put-component/song-put-component';
import {ArtistsComponent} from './artist/artists-component/artists-component';
import {SongsGetByAlbumComponent} from './song/songs-get-by-album-component/songs-get-by-album-component';
import {Profile} from './profile/profile';

import {authGuard, guestGuard} from './auth-guard';
import {OtpLoginComponent} from './otp-login-component/otp-login-component';
import {RecoveryComponent} from './recovery-component/recovery-component';
import {PasswordChangeComponent} from './password-change-component/password-change-component';
import {AlbumsComponent} from './album/albums-component/albums-component';
import {SongsComponent} from './song/songs-component/songs-component';
import {SongDeleteComponent} from './song/song-delete-component/song-delete-component';
import {AlbumsGetByArtistComponent} from './album/albums-get-by-artist-component/albums-get-by-artist-component';
import {SongsGetByAlbumUserComponent} from './song/songs-get-by-album-user-component/songs-get-by-album-user-component';
import {RegistrationRequests} from './registration-requests/registration-requests';
import {UserHistoryComponent} from './user-history-component/user-history-component';
import {UserAnalyticsComponent} from './user-analytics/user-analytics';

export const routes: Routes = [
  {path: '', redirectTo: 'login', pathMatch: 'full'},
  {path: 'users/changepassword', component: PasswordChangeComponent},
  {path: 'requests', component: RegistrationRequests},

  {
    path: '',
    canActivate: [guestGuard],
    children: [
      {path: 'login', component: LoginComponent},
      {path: 'login/otp', component: OtpLoginComponent},
      {path: 'recovery', component: RecoveryComponent},
      {path: 'register', component: RegisterComponent},
    ]
  },

  {
    path: '',
    canActivate: [authGuard],
    children: [
      {path: 'home', component: HomePageComponent},
      {path: 'profile', component: Profile},

      {path: 'artists', component: ArtistsComponent},
      {path: 'artists/add', component: ArtistAddComponent},
      {path: 'artists/put/:id', component: ArtistPutComponent},
      {path: 'home/artists/:id/albums', component: AlbumsGetByArtistComponent},

      {path: 'albums', component: AlbumsComponent},
      {path: 'albums/add', component: AlbumAddComponent},
      {path: 'albums/put/:id', component: AlbumPutComponent},
      {path: 'albums/:id/songs', component: SongsGetByAlbumComponent},
      {path: 'home/albums/:id/songs', component: SongsGetByAlbumUserComponent},

      {path: 'songs', component: SongsComponent},
      {path: 'songs/add', component: SongAddComponent},
      {path: 'songs/put/:id', component: SongPutComponent},
      {path: 'songs/delete/:id', component: SongDeleteComponent},

      {path: 'history', component: UserHistoryComponent},
      {path: 'analytics', component: UserAnalyticsComponent},
    ]
  },

  {path: '**', redirectTo: 'home'}
];
