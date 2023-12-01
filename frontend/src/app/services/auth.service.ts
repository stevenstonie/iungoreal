import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080';
  private token: string;

  constructor(private http: HttpClient) {
    this.token = localStorage.getItem('token') ?? '';
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.baseUrl}/api/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.token = response.token;
          localStorage.setItem('token', this.token);
          localStorage.setItem('email', credentials.email);
          console.log(credentials.email);
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
          console.log(credentials.email);
        })
      )
  }

  getToken(): string {
    return this.token;
  }

  isAuthenticated(): boolean {
    return !!this.token && !this.isTokenExpired();
  }

  private isTokenExpired(): boolean {
    try {
      const decodedToken: any = jwtDecode(this.token);

      if (decodedToken.exp === undefined) {
        return false;
      }

      const now = Date.now() / 1000;
      return decodedToken.exp < now;
    } catch (error) {
      console.error(`Error decoding token: ${error}`);
      return false;
    }
  }
}
