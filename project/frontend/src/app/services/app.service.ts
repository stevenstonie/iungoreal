import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AppService {
  private postApiUrl = 'http://localhost:8083/api/post';

  constructor() { }
}
