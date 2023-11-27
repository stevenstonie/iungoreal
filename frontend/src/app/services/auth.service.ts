import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(private http: HttpClient) { }

  authenticate(credentials: any): Observable<any> {
    return this.http.post('/api/authenticate', credentials);
  }

  getToken(): string | any {
    return localStorage.getItem('token');
  }

  login(credentials: any): Observable<any> {
    const headers = new HttpHeaders({ 'Content-Type': 'application/json' });
    return this.http.post('http://localhost:8080/api/auth/authenticate', credentials, { headers }).pipe(
      tap((response: any) => {
        localStorage.setItem('token', response.token);
      })
    );
  }

  getDemoControllerData(): Observable<any> {
    return this.http.get('http://localhost:8080/api/demo-controller');
  }

}
