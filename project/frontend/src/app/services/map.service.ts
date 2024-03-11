import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Marker } from '../models/marker';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  private apiUrl = 'http://localhost:8082/api/marker';

  constructor(private httpClient: HttpClient) { }

  getMarkers(): Observable<Marker[]> {
    return this.httpClient.get<Marker[]>(`${this.apiUrl}/getAll`);
  }

  addMarker(marker: Marker) {
    return this.httpClient.post(`${this.apiUrl}/add`, marker);
  }

  updateMarker(marker: Marker) {
    return this.httpClient.put(`${this.apiUrl}/update`, marker);
  }

  deleteMarker(id: number) {
    return this.httpClient.delete(`${this.apiUrl}/delete/${id}`);
  }
}
