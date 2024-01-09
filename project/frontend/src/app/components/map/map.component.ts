import { Component, OnDestroy, AfterViewInit, ViewChild, ElementRef, Input } from '@angular/core';
import * as L from 'leaflet';
import { Marker } from 'src/app/models/marker';
import { Role, User } from 'src/app/models/user';
import { MapService } from 'src/app/services/map.service';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit, OnDestroy {
  declare map: L.Map;
  @ViewChild('map') mapElement!: ElementRef;
  @Input() loggedUser: User | null = null;
  showMarkerInputs: boolean = false;
  latitude!: number;
  longitude!: number;
  markers: Marker[] = [];

  toggleAddMarker() {
    this.showMarkerInputs = !this.showMarkerInputs;

    if (this.showMarkerInputs) {
      this.map.on('click', this.handleMapClick);
    } else {
      this.map.off('click', this.handleMapClick);
    }
  }

  constructor(private mapService: MapService) {

  }

  ngAfterViewInit() {
    console.log('MapComponent ngAfterViewInit() called');

    this.initializeMap();

    this.mapService.getMarkers().subscribe({
      next: (response) => {
        this.markers = response;
        this.placeMarkersOnMap();
      },
      error: (error) => {
        console.error('Error getting markers', error);
      }
    });
  }

  ngOnDestroy() {
    if (this.map) {
      console.log('MapComponent ngOnDestroy() called');

      this.map.remove();

      this.markers = [];
    }
  }

  initializeMap() {
    this.map = L.map(this.mapElement.nativeElement).setView([45.65, 25.603], 13);
    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      attribution: '© <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
    }).addTo(this.map);
  }

  handleMapClick = (event: L.LeafletMouseEvent) => {
    if (this.showMarkerInputs) {
      this.latitude = event.latlng.lat;
      this.longitude = event.latlng.lng;
      console.log('Clicked LatLng:', this.latitude, ", " + this.longitude);
    }
  };

  submitMarker() {
    const marker: Marker = {
      id: 0,
      title: (document.getElementById('marker-title') as HTMLInputElement).value,
      description: (document.getElementById('marker-description') as HTMLInputElement).value,
      latitude: parseFloat((document.getElementById('marker-latitude') as HTMLInputElement).value),
      longitude: parseFloat((document.getElementById('marker-longitude') as HTMLInputElement).value),
      startDate: new Date((document.getElementById('marker-start-date') as HTMLInputElement).value),
      endDate: new Date((document.getElementById('marker-end-date') as HTMLInputElement).value)
    };

    this.mapService.addMarker(marker).subscribe({
      next: (response) => {
        console.log('Marker added successfully', response);
        // Additional logic on successful submission (e.g., close input form, refresh map markers)
      },
      error: (error) => {
        console.error('Error adding marker', error);
      }
    });
  }

  placeMarkersOnMap() {
    for (const marker of this.markers) {
      L.marker([marker.latitude, marker.longitude]).addTo(this.map);
    }
  }

  getMarkerSelectionCursor() {
    return {
      'click-cursor': this.showMarkerInputs
    }
  }

  get isLoggedUserAdmin(): boolean {
    return this.loggedUser?.role === Role.ADMIN;
  }
}
