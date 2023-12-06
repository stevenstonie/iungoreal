import { Component, OnInit } from '@angular/core';
import * as L from 'leaflet';

@Component({  
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements OnInit{
  declare map: L.Map;

  ngOnInit() {
      this.map = L.map('map').setView([45.65, 25.603], 13);
      L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
      }).addTo(this.map);
    }
}
