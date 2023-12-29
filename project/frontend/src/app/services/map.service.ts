import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Marker } from '../models/marker';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = 'http://localhost:8081/api/markers';

  constructor(private httpClient: HttpClient) { }

  getMarkers(): Observable<Marker[]> {
    return this.httpClient.get<Marker[]>(`${this.apiUrl}/`);
  }

  addMarker(marker: Marker) {
    return this.httpClient.post(`${this.apiUrl}/addMarker`, marker);
  }

  updateMarker(marker: Marker) {
    return this.httpClient.put(`${this.apiUrl}/updateMarker`, marker);
  }

  deleteMarker(id: number) {
    return this.httpClient.delete(`${this.apiUrl}/deleteMarker/${id}`);
  }
}
