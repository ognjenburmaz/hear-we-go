import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Router} from '@angular/router';

interface AuthRequest {
  username: string | null;
  password: string;
}

interface LoginResponse {
  jwt: string;
  expiresIn: number;
}

export interface UserRegistrationRequest {
  firstName: string;
  lastName: string;
  email: string;
  username: string;
  password: string;
}


export interface User {
  id?: string;
  username: string;
  email: string;
  firstName: string;
  lastName: string;
  role?: string;
}


@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = '/api/users';

  constructor(private http: HttpClient, private router: Router) {
  }

  otplogin(credentials: AuthRequest): Observable<LoginResponse> {
    const url = `${this.apiUrl}/login/otp`;
    return this.http.post<LoginResponse>(url, credentials)
      .pipe(
        tap(response => {
          const token = response.jwt;
          localStorage.setItem('authToken', token);
          console.log('Login successful, token stored:', token);
          // this.router.navigate(['/home']);
        }),
        catchError(error => {
          console.error('Login failed:', error);
          throw error;
        })
      );
  }

  pswlogin(credentials: AuthRequest): Observable<any> {
    const url = `${this.apiUrl}/login/psw`;
    return this.http.post<any>(url, credentials)
      .pipe(
        tap(response => {
          // const token = response.jwt;
          // localStorage.setItem('authToken', token);
          console.log("Login succesful, response from endpoint: " + response);
          // this.router.navigate(['/home']);
        }),
        catchError(error => {
          console.error('Login failed:', error);
          throw error;
        })
      );
  }


  register(registrationData: UserRegistrationRequest): Observable<User> {
    const url = `${this.apiUrl}/register`;


    return this.http.post<User>(url, registrationData)
      .pipe(
        tap(user => {
          console.log('Registration successful:', user);

          this.router.navigate(['/login']);
        }),
        catchError(error => {
          console.error('Registration failed:', error);

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
