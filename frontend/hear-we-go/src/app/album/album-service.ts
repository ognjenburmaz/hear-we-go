import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { tap, catchError, map } from 'rxjs/operators';


export interface Album {
  id?: string;
  title: string;
  releaseDate:string;
  genre: string;
  artistIds:string[];
}

@Injectable({
  providedIn: 'root',
})
export class AlbumService {
  
  private apiUrl = 'api/content/albums';
    constructor(private http: HttpClient, private router: Router) { }
  
    create(albumData:  Album): Observable<Album> {
        const url = `${this.apiUrl}`;
    
    
        return this.http.post<Album>(this.apiUrl, albumData)
          .pipe(
            tap(album => {
              console.log('Creation successful:', album);
    
              this.router.navigate(['/home']);
            }),
            catchError(error => {
              console.error('Creation failed:', error);
    
              throw error;
            })
          );
      }
  
      put(albumData:  Album,id:string): Observable<Album> {
        const url = `${this.apiUrl}`;
    
    
        return this.http.put<Album>(this.apiUrl+'/'+id, albumData)
          .pipe(
            tap(album => {
              console.log('Edit successful:', album);
    
              this.router.navigate(['/home']);
            }),
            catchError(error => {
              console.error('Edit failed:', error);
    
              throw error;
            })
          );
      }
}
