import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { PublicUserPayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class MiscService {
  private searchApiUrl = 'http://localhost:8083/api/search'

  constructor(private http: HttpClient) { }

  searchForUsersByInput(input: string): Observable<PublicUserPayload[]>{
    const params = new HttpParams().set('input', input);

    return this.http.get<PublicUserPayload[]>(`${this.searchApiUrl}/getUsersMatching`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }
  
  // ----------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in misc service.'));
  }
}
