import { Injectable } from '@angular/core';
import { HttpClient} from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { jwtDecode } from 'jwt-decode';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8081/api/auth';
  private token: string;
  private email: string;

  constructor(private http: HttpClient) {
    this.token = this.getToken() ?? '';
    this.email = this.getEmail() ?? '';
  }

  login(credentials: { email: string, password: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/login`, credentials)
      .pipe(
        tap(response => {
          this.token = response.token;
          localStorage.setItem('token', this.token);
          localStorage.setItem('email', credentials.email);
        })
      );
  }

  register(credentials: { email: string, password: string, username: string }): Observable<any> {
    return this.http.post<{ token: string }>(`${this.apiUrl}/register`, credentials)
      .pipe(
        tap(response => {
          this.token = response.token;
          localStorage.setItem('token', this.token);
          localStorage.setItem('email', credentials.email);
          localStorage.setItem('username', credentials.username);
        })
      )
  }

  getToken(): string {
    return localStorage.getItem('token') ?? '';
  }

  getEmail(): string {
    return localStorage.getItem('email') ?? '';
  }

  logout() {
    this.removeCredentialsFromStorage();
    window.location.reload();
  }

  removeCredentialsFromStorage(): void {
    this.token = '';
    localStorage.removeItem('token');
    localStorage.removeItem('email');
  }

  isAuthenticated(): boolean {
    if (!this.token || this.token === '') {
      console.log('logged out (no token)');
      return false;
    }
    else if (!this.isTokenNotExpired()) {
      console.log('logged out due to expired token');
      this.removeCredentialsFromStorage();
      return false;
    }

    return true;
  }

  private isTokenNotExpired(): boolean {
    try {
      const decodedToken: any = jwtDecode(this.token);

      console.log('exp: ' + new Date(decodedToken.exp * 1000).toLocaleString());
      console.log('now: ' + new Date(Date.now()).toLocaleString());

      if (decodedToken.exp === undefined) {
        return false;
      }

      const now = Math.floor(Date.now() / 1000);

      return now < decodedToken.exp;
    } catch (error) {
      console.error(`Error decoding token: ${error}`);
      return false;
    }
  }
}
