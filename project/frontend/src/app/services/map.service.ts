import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Marker } from '../models/marker';

@Injectable({
  providedIn: 'root'
})
export class MapService {
  constructor(private httpClient: HttpClient) { }

  getMarkers() {
    return this.httpClient.get('http://localhost:8081/api/markers');
  }

  addMarker(marker: Marker) {
    return this.httpClient.post('http://localhost:8081/api/markers/addMarker', marker);
  }

  updateMarker(marker: Marker) {
    return this.httpClient.put('http://localhost:8081/api/markers/updateMarker', marker);
  }

  deleteMarker(id: number) {
    return this.httpClient.delete(`http://localhost:8081/api/markers/deleteMarker/${id}`);
  }
}
