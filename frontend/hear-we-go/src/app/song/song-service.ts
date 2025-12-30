import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { tap, catchError, map } from 'rxjs/operators';
export interface Song {
  id?: string;
  title: string;
  genre: string;
  durationSeconds: number;
  albumId:string;
}
@Injectable({
  providedIn: 'root',
})
export class SongService 
{
  private apiUrl = 'api/content/songs';
  private apiUrlAlbums = 'api/content/albums';
    constructor(private http: HttpClient, private router: Router) { }
  
    create(formData:  FormData): Observable<Song> {
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
  
      put(songData:  Song,id:string): Observable<Song> {
        const url = `${this.apiUrl}`;
    
    
        return this.http.put<Song>(this.apiUrl+'/'+id, songData)
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
  
      getAll(albumId:string) :Observable<Song[]> 
  {
      return this.http.get<Song[]>(this.apiUrlAlbums+'/'+albumId+'/songs');
   } 
}
