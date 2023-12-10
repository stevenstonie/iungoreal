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
  @Input() currentUser: User;
  showMarkerInputs: boolean = false;
  latitude!: number;
  longitude!: number;

  toggleAddMarker() {
    this.showMarkerInputs = !this.showMarkerInputs;

    if (this.showMarkerInputs) {
      this.map.on('click', this.handleMapClick);
    } else {
      this.map.off('click', this.handleMapClick);
    }
  }

  constructor(private mapService: MapService) {
    this.currentUser = { id: 0, email: '', password: '', firstname: '', lastname: '', role: Role.USER };
  }

  ngAfterViewInit() {
    console.log('MapComponent ngAfterViewInit() called');

    this.initializeMap();
    L.marker([45.663, 25.653]).addTo(this.map);
    L.marker([45.65, 25.613]).addTo(this.map);
  }

  ngOnDestroy() {
    if (this.map) {
      console.log('MapComponent ngOnDestroy() called');

      this.map.remove();
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

  get isCurrentUserAdmin(): boolean {
    return this.currentUser?.role === Role.ADMIN;
  }

  submitMarker() {
    const marker: Marker = {
      id: 0,
      title: (document.getElementById('marker-title') as HTMLInputElement).value,
      description: (document.getElementById('marker-description') as HTMLInputElement).value,
      latitude: this.latitude,
      longitude: this.longitude,
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

  getMarkerSelectionCursor() {
    return {
      'click-cursor': this.showMarkerInputs
    }
  }
}
