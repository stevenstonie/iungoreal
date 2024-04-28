import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { NotificationFPayload, ResponsePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private apiUrl = 'http://localhost:8080/api/notification';
  private FRIEND = 'friend'

  constructor(private httpClient: HttpClient) { }

  getLast50NotificationsF(username: string): Observable<NotificationFPayload[]> {
    const params = new HttpParams().set('username', username);

    return this.httpClient.get<NotificationFPayload[]>(`${this.apiUrl}/${this.FRIEND}/getLast50`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removeNotificationF(id: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('id', id);

    return this.httpClient.delete<ResponsePayload>(`${this.apiUrl}/${this.FRIEND}/remove`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getNbOfLast51NotificationsF(username: string): Observable<number> {
    const params = new HttpParams().set('username', username);

    return this.httpClient.get<number>(`${this.apiUrl}/${this.FRIEND}/countLast51`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // ----------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in notification service.'));
  }
}
