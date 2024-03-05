import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { NotificationFPayload } from '../models/payloads';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {
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

  // ----------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in app service.'));
  }
}
