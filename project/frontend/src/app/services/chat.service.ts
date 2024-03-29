import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { throwError, Observable, catchError } from 'rxjs';
import { ChatroomPayload, ResponsePayload } from '../models/Payloads';
import { ChatMessage } from '../models/app';

@Injectable({
  providedIn: 'root'
})
export class ChatService {
  private apiUrl = 'http://localhost:8083/api/chat';

  constructor(private http: HttpClient) { }

  getAllFriendsWithNoDmChats(username: string): Observable<string[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<string[]>(`${this.apiUrl}/getFriendsWithNoDmChats`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  createChatroom(friendUsername: string, username: string): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('friendUsername', friendUsername);

    return this.http.post<ResponsePayload>(`${this.apiUrl}/createChatroom`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getAllDmChatroomsOfUser(username: string): Observable<ChatroomPayload[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<ChatroomPayload[]>(`${this.apiUrl}/getAllDmChatroomsOfUser`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getNextMessagesByChatroomId(chatroomId: number, lastMessageId: number | null): Observable<ChatMessage[]> {
    let params = new HttpParams().set('chatroomId', chatroomId.toString());
    if(lastMessageId !== null) {
      params = params.set('lastMessageId', lastMessageId.toString());
    }

    return this.http.get<ChatMessage[]>(`${this.apiUrl}/getNextMessagesByChatroomId`, { params })
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
