import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CountryAndRegionService {
  private apiUrlCountry = 'http://localhost:8083/api/country';
  private apiUrlRegion = 'http://localhost:8083/api/region';

  constructor() { }
}
