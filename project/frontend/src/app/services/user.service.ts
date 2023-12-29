import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of, throwError } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';
  private apiUrl2 = 'http://localhost:8082/api/user';

  constructor(private http: HttpClient) { }

  getLoggedUser(): Observable<User> {
    const token = localStorage.getItem('token');
    if (token) {
      const headers = new HttpHeaders().set('Authorization', `Bearer ${token}`);
      return this.http.get<User>(`${this.apiUrl}/getUserByToken`, { headers });
    } else {
      return of(null as unknown as User);
    }
  }

  getUserByUsername(username: string): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/getUserByUsername/${username}`)
      .pipe(
        catchError(this.handleError)
      );
  }
  
  private handleError(error: HttpErrorResponse) {
    return throwError(() => new Error('An error occurred.'));
  }
}
