import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

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
    return this.http.post(`${this.baseUrl}/api/auth/login`, credentials)
      .pipe(
        tap(response => {
          this.token = response as string;
          localStorage.setItem('token', this.token);
        })
      );
  }
  
  register(user: any): Observable<any> {
    return this.http.post(`${this.baseUrl}/api/auth/register`, user);
  }

  getToken(): string {
    return this.token;
  }

  isAuthenticated(): boolean {
    return !!this.token && !this.isTokenExpired();
  }

  private isTokenExpired(): boolean {
    // use something like 'jwt-decode' to decode the token and check its expiration, https://www.npmjs.com/package/jwt-decode6
    return true;
  }
}
