import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TestServiceService {
  private apiUrl = 'http://localhost:8080/';

  constructor(private http: HttpClient) { }

  getMessage() {
    return this.http.get(this.apiUrl, { responseType: 'text' });
  }

  getSecuredMessage() {
    return this.http.get(this.apiUrl + 'secured', { responseType: 'text' });
  }
}
