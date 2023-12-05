import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, map, of, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080';
  private token: string;
  private email: string;

  constructor(private http: HttpClient) {
    this.token = this.getToken() ?? '';
    this.email = this.getEmail() ?? '';
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.baseUrl}/api/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.token = response.token;
          localStorage.setItem('token', this.token);
          localStorage.setItem('email', credentials.email);
        })
      );
  }

  register(credentials: { email: string, password: string, firstName: string, lastName: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.baseUrl}/api/auth/register`, credentials)
      .pipe(
        tap(response => {
          this.token = response.token;
          localStorage.setItem('token', this.token);
          localStorage.setItem('email', credentials.email);
        })
      )
  }

  getToken(): string {
    return localStorage.getItem('token') ?? '';
  }

  getEmail(): string {
    return localStorage.getItem('email') ?? '';
  }

  logout(): void {
    this.token = '';
    localStorage.removeItem('token');
    localStorage.removeItem('email');
  }

  isAuthenticated(): boolean {
    if(!!this.token && this.isTokenNotExpired()) {
      return true;
    }
    else {
      this.logout();
      console.log('user forcefully logged out due to expired token');
      return false;
    }
  }

  private isTokenNotExpired(): boolean {
    try {
      const decodedToken: any = jwtDecode(this.token);

      if (decodedToken.exp === undefined) {
        return false;
      }

      const now = Math.floor(Date.now() / 1000);

      console.log('exp: ' + decodedToken.exp);
      console.log('now: ' + now);

      return now < decodedToken.exp;
    } catch (error) {
      console.error(`Error decoding token: ${error}`);
      return false;
    }
  }
}
