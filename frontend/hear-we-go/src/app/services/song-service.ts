import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Router} from '@angular/router';
import {catchError, tap} from 'rxjs/operators';

export interface Song {
  id?: string | undefined | any;
  title: string;
  durationSeconds: number;
  genre: string;
  albumId: string;
  averageRating?: number;
  totalRatings?: number;
  userRating?: number;
  isLoading?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class SongService {
  private apiUrl = 'api/content/songs';
  private apiUrlAlbums = 'api/content/albums';

  constructor(private http: HttpClient, private router: Router) {
  }

  create(formData: FormData): Observable<Song> {
    const url = `${this.apiUrl}`;


    return this.http.post<Song>(this.apiUrl, formData)
      .pipe(
        tap(song => {
          console.log('Creation successful:', song);

          this.router.navigate(['/home']);
        }),
        catchError(error => {
          console.error('Creation failed:', error);

          throw error;
        })
      );
  }

  put(songData: Song, id: string): Observable<Song> {
    const url = `${this.apiUrl}`;


    return this.http.put<Song>(this.apiUrl + '/' + id, songData)
      .pipe(
        tap(song => {
          console.log('Edit successful:', song);

          this.router.navigate(['/home']);
        }),
        catchError(error => {
          console.error('Edit failed:', error);

          throw error;
        })
      );
  }

  getAllByAlbum(albumId: string | null): Observable<Song[]> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('authToken')}`);
    return this.http.get<Song[]>(`${this.apiUrlAlbums}/${albumId}/songs`, {headers});
  }

  getAll(): Observable<Song[]> {
    return this.http.get<Song[]>(this.apiUrl);
  }

  getRecommended(username: string | null): Observable<Song[]> {
    return this.http.get<Song[]>("api/recommendations/home/" + username)
  }

  getOne(id: string): Observable<Song> {
    const headers = new HttpHeaders().set('Authorization', `Bearer ${localStorage.getItem('authToken')}`);
    return this.http.get<Song>(`${this.apiUrl}/${id}`, {headers});
  }

  delete(id: string): Observable<Song> {
    return this.http.delete<Song>(this.apiUrl + '/' + id);
  }

  getAudioBlob(songId: string) {
    const token = localStorage.getItem('authToken');

    return this.http.get(
      `/api/content/songs/${songId}/audio`,
      {
        responseType: 'blob',
        headers: new HttpHeaders({
          Authorization: `Bearer ${token}`
        })
      }
    );
  }

  rateSong(songId: string, value: number): Observable<void> {
    return this.http.post<void>(`api/ratings`, {songId, value});
  }

  deleteRating(songId: string): Observable<void> {
    return this.http.delete<void>(`api/ratings/${songId}`);
  }
}

