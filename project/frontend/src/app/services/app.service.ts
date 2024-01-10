import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Post } from '../models/app';
import { Observable, catchError, throwError } from 'rxjs';
import { ResponsePayload } from '../models/payloads';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private postApiUrl = 'http://localhost:8083/api/post';

  constructor(private httpClient: HttpClient) { }

  createPost(formData: FormData): Observable<ResponsePayload> {
    return this.httpClient.post<ResponsePayload>(`${this.postApiUrl}/create`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in app service.'));
  }
}
