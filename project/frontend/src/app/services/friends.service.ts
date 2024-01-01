import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { MessagePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class FriendsService {
  private apiUrl = 'http://localhost:8080/api/friends';

  constructor(private http: HttpClient) { }

  sendFriendRequest(from: string, to: string): Observable<MessagePayload> {
    const params = new HttpParams().set('from', from).set('to', to);

    return this.http.post<MessagePayload>(`${this.apiUrl}/sendRequest`, {}, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    return throwError(() => new Error('An error occurred in friends service. ' + error.message));
  }
}
