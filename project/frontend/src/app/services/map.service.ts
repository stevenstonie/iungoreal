import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Marker } from '../models/marker';
import { Observable, catchError, throwError } from 'rxjs';

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

  // deleteMarker(id: number) {
  //   return this.httpClient.delete(`${this.apiUrl}/delete/${id}`);
  // }

  // -------------------------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in map service.'));
  }
}
