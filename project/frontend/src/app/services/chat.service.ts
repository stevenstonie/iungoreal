import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { throwError, Observable, catchError } from 'rxjs';
import { ResponsePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8083/api/chat';

  constructor(private http: HttpClient) { }

  getAllFriendsWithNoChats(username: string): Observable<string[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<string[]>(`${this.apiUrl}/getFriendsWithNoChats`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  createChatroom(friendUsername: string, username: string): Observable<ResponsePayload> {
    const params = new HttpParams().set('friendUsername', friendUsername).set('username', username);

    return this.http.post<ResponsePayload>(`${this.apiUrl}/createChatroom`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // -----------------------------------------------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in friends service.'));
  }
}
