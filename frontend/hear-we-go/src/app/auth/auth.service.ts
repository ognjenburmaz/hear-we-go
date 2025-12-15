import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';

interface AuthRequest {
  username: string;
  password: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient, private router: Router) { }

  login(credentials: AuthRequest): Observable<string> {
    const url = `${this.apiUrl}/login`;
    return this.http.post<string>(url, credentials)
      .pipe(
        tap(token => {
          localStorage.setItem('authToken', token);
          console.log('Login successful, token received:', token);
          this.router.navigate(['/home']);
        }),
        catchError(error => {
          console.error('Login failed:', error);
          alert('Login failed. Please check your credentials.');
          throw error;
        })
      );
  }

  logout(): void {
    localStorage.removeItem('authToken');
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return localStorage.getItem('authToken') !== null;
  }

  getToken(): string | null {
    return localStorage.getItem('authToken');
  }
}
