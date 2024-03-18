import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { User } from '../models/user';
import { StringInJson } from '../models/app';
import { CountryOrRegionPayload, ResponsePayload } from '../models/Payloads';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/user';

  constructor(private http: HttpClient) { }

  getUserByUsername(username: string, isTheUserTheLoggedOne: boolean): Observable<User> {
    let urlPart = 'getPublicByUsername';
    if (isTheUserTheLoggedOne) {
      urlPart = 'getPrivateByUsername';
    }
    const params = new HttpParams().set('username', username);

    return this.http.get<User>(`${this.apiUrl}/${urlPart}`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getUserByEmail(email: string): Observable<User> {
    const params = new HttpParams().set('email', email);

    return this.http.get<User>(`${this.apiUrl}/getByEmail`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getProfilePictureLink(username: string): Observable<StringInJson> {
    const params = new HttpParams().set('username', username);

    return this.http.get<StringInJson>(`${this.apiUrl}/getProfilePictureLink`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  saveProfilePicture(file: File): Observable<ResponsePayload> {
    const formData = new FormData();

    formData.append('username', localStorage.getItem('username') ?? '');
    formData.append('file', file);

    return this.http.put<ResponsePayload>(`${this.apiUrl}/saveProfilePicture`, formData)
      .pipe(
        catchError(this.handleError)
      );
  }

  // countries and regions --------------------------------------------------

  getAvailableRegionsForUser(username: string): Observable<CountryOrRegionPayload[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<CountryOrRegionPayload[]>(`${this.apiUrl}/getAvailableRegions`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getCountryOfUser(username: string): Observable<CountryOrRegionPayload> {
    const params = new HttpParams().set('username', username);

    return this.http.get<CountryOrRegionPayload>(`${this.apiUrl}/getCountry`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getPrimaryRegionOfUser(username: string): Observable<CountryOrRegionPayload> {
    const params = new HttpParams().set('username', username);

    return this.http.get<CountryOrRegionPayload>(`${this.apiUrl}/getPrimaryRegion`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  getSecondaryRegionsOfUser(username: string): Observable<CountryOrRegionPayload[]> {
    const params = new HttpParams().set('username', username);

    return this.http.get<CountryOrRegionPayload[]>(`${this.apiUrl}/getSecondaryRegions`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  setCountryForUser(username: string, countryId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('countryId', countryId.toString());

    return this.http.put<ResponsePayload>(`${this.apiUrl}/setCountry`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  setPrimaryRegionForUser(username: string, regionId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('regionId', regionId.toString());

    return this.http.put<ResponsePayload>(`${this.apiUrl}/setPrimaryRegion`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  addSecondaryRegionForUser(username: string, regionId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('regionId', regionId.toString());

    return this.http.post<ResponsePayload>(`${this.apiUrl}/addSecondaryRegion`, null, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removeCountryOfUser(username: string): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username);

    return this.http.delete<ResponsePayload>(`${this.apiUrl}/removeCountry`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removePrimaryRegionOfUser(username: string): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username);

    return this.http.delete<ResponsePayload>(`${this.apiUrl}/removePrimaryRegion`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  removeSecondaryRegionOfUser(username: string, regionId: number): Observable<ResponsePayload> {
    const params = new HttpParams().set('username', username).set('regionId', regionId.toString());

    return this.http.delete<ResponsePayload>(`${this.apiUrl}/removeSecondaryRegion`, { params })
      .pipe(
        catchError(this.handleError)
      );
  }

  // -------------------------------------------------------

  private handleError(error: HttpErrorResponse) {
    console.error(error);
    return throwError(() => new Error('An error occurred in user service.'));
  }
}
