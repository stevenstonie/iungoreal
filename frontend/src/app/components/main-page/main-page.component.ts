import { Component } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent {
  email: string;
  declare map: L.Map;
  showMap: boolean = false;

  toggleMap() {
    this.showMap = !this.showMap;
  }

  constructor() {
    this.email = localStorage.getItem('email') ?? '';
  }

}
