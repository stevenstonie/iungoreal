import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { User } from '../models/user';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) { }

  getUserByUsername(username: string, isTheUserTheLoggedOne: boolean): Observable<User> {
    let urlPart = 'getPublicByUsername';
    if (isTheUserTheLoggedOne) {
      urlPart = 'getPrivateByUsername';
    }
    const params = new HttpParams().set('username', username);

    return this.http.get<User>(`${this.apiUrl}/${urlPart}`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getUserByEmail(email: string): Observable<User> {
    const params = new HttpParams().set('email', email);

    return this.http.get<User>(`${this.apiUrl}/getByEmail`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    return throwError(() => new Error('An error occurred in user service.'));
  }
}
