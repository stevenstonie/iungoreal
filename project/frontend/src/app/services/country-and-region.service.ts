import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { CountryOrRegionPayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class CountryAndRegionService {
  private apiUrlCountry = 'http://localhost:8083/api/country';

  constructor(private http: HttpClient){}

  getAllCountries(): Observable<CountryOrRegionPayload[]> {
    return this.http.get<CountryOrRegionPayload[]>(this.apiUrlCountry + '/getAll');
  }
}
