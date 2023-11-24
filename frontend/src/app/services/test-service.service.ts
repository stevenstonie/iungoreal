import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class TestServiceService {
  private apiUrl = 'http://localhost:8080/api';

  constructor(private http: HttpClient) { }

  getMessage() {
    return this.http.get(this.apiUrl + "/home", { responseType: 'text' });
  }

  getSecuredMessage() {
    return this.http.get(this.apiUrl + '/secured', { responseType: 'text' });
  }
}
