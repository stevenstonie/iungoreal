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

  sendFriendRequest(sender: string, receiver: string): Observable<MessagePayload> {
    const params = new HttpParams().set('sender', sender).set('receiver', receiver);

    return this.http.post<MessagePayload>(`${this.apiUrl}/sendRequest`, {}, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  checkRequest(sender: string, receiver: string): Observable<MessagePayload> {
    const params = new HttpParams().set('sender', sender).set('receiver', receiver);

    return this.http.get<MessagePayload>(`${this.apiUrl}/checkRequest`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  checkFriendship(user1: string, user2: string): Observable<MessagePayload> {
    const params = new HttpParams().set('user1', user1).set('user2', user2);

    return this.http.get<MessagePayload>(`${this.apiUrl}/checkFriendship`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getAllUsernamesOfFriends(username: string): Observable<string[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<string[]>(`${this.apiUrl}/getAllFriendsUsernames`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  acceptFriendRequest(sender: string, receiver: string): Observable<MessagePayload> {
    const params = new HttpParams().set('sender', sender).set('receiver', receiver);

    return this.http.put<MessagePayload>(`${this.apiUrl}/acceptRequest`, {}, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  cancelFriendRequest(sender: string, receiver: string): Observable<MessagePayload> {
    const params = new HttpParams().set('sender', sender).set('receiver', receiver);

    return this.http.delete<MessagePayload>(`${this.apiUrl}/cancelRequest`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  declineFriendRequest(sender: string, receiver: string): Observable<MessagePayload> {
    const params = new HttpParams().set('sender', sender).set('receiver', receiver);

    return this.http.delete<MessagePayload>(`${this.apiUrl}/declineRequest`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  unfriend(unfriender: string, unfriended: string): Observable<MessagePayload> {
    const params = new HttpParams().set('unfriender', unfriender).set('unfriended', unfriended);

    return this.http.delete<MessagePayload>(`${this.apiUrl}/unfriend`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in friends service.'));
  }
}
