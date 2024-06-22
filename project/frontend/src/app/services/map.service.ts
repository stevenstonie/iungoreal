import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Marker } from '../models/marker';
import { Observable, catchError, throwError } from 'rxjs';
import { ResponsePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = 'http://localhost:8082/api/marker';

  constructor(private httpClient: HttpClient) { }

  getMarkers(): Observable<Marker[]> {
    return this.httpClient.get<Marker[]>(`${this.apiUrl}/getAll`);
  }

  addMarker(marker: Marker): Observable<Marker> {
    return this.httpClient.post<Marker>(`${this.apiUrl}/add`, marker)
      .pipe(
        catchError(this.handleError)
      );
  }

  // updateMarker(marker: Marker) {
  //   return this.httpClient.put(`${this.apiUrl}/update`, marker);
  // }

  removeMarker(id: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('id', id.toString());

    return this.httpClient.delete<ResponsePayload>(`${this.apiUrl}/remove`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // -------------------------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in map service.'));
  }
}
