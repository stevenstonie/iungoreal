import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent implements OnInit {
  email: string;
  declare map: L.Map;

  toggleMap() {
    console.log('toggle map');
  }

  constructor() {
    this.email = localStorage.getItem('email') ?? '';

  }

  ngOnInit() {
    this.map = L.map('map').setView([45.65, 25.603], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
     attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);
   }   

}
