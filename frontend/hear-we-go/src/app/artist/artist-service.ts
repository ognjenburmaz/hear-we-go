
import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Router } from '@angular/router';
import { tap, catchError, map } from 'rxjs/operators';

export interface Artist {
  id?: string;
  name: string;
  biography: string;
  genres: string[];
}


@Injectable({
  providedIn: 'root',
})
export class ArtistService 
{
  private apiUrl = '/api/content/artists';
  constructor(private http: HttpClient, private router: Router) { }

  create(artistData:  Artist): Observable<Artist> {
      const url = `${this.apiUrl}`;
  
  
      return this.http.post<Artist>(this.apiUrl, artistData)
        .pipe(
          tap(artist => {
            console.log('Creation successful:', artist);
  
            this.router.navigate(['/artists']);
          }),
          catchError(error => {
            console.error('Creation failed:', error);
  
            throw error;
          })
        );
    }

    getAll() :Observable<Artist[]> 
{
    return this.http.get<Artist[]>(this.apiUrl);
 } 
}
