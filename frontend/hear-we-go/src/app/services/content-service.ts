import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

import {SearchResponse,SongResponse,AlbumResponse,ArtistResponse } from '../DTOs/search-mogule'

@Injectable({
  providedIn: 'root',
})
export class ContentService {

  private apiUrl = 'api/content';
  constructor(private http: HttpClient) {}

  globalSearch(query: string): Observable<SearchResponse> {
    const params = new HttpParams().set('query', query);
    return this.http.get<SearchResponse>(`${this.apiUrl}/search`, { params });
  }
}
