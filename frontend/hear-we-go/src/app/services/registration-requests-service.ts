import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpClient} from '@angular/common/http';
import {User} from './auth.service';

@Injectable({
  providedIn: 'root',
})
export class RegistrationRequestsService {
  private apiHost = '/api/users';


  private registrationRequests: User[] = []

  constructor(private httpClient: HttpClient) {

  }

  getAll(): Observable<User[]> {
    return this.httpClient.get<User[]>(this.apiHost + '/requests')
  }

  add(registrationRequest: User): Observable<User> {
    return this.httpClient.post<User>(this.apiHost + '/requests/add', registrationRequest)
  }

  accept(id: string | undefined): Observable<User> {
    return this.httpClient.patch<User>(this.apiHost + '/requests/accept/' + id, null)
  }

  reject(id: string | undefined): Observable<User> {
    return this.httpClient.patch<User>(this.apiHost + '/requests/reject/' + id, null)
  }

}
