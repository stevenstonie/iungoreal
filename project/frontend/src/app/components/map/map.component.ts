import { Component, OnDestroy, AfterViewInit, ViewChild, ElementRef, Input } from '@angular/core';
import * as L from 'leaflet';
import { RegionDetailsPayload } from 'src/app/models/Payloads';
import { Marker } from 'src/app/models/marker';
import { Role, User } from 'src/app/models/user';
import { MapService } from 'src/app/services/map.service';
import { UserService } from 'src/app/services/user.service';


@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.scss']
})
export class MapComponent implements AfterViewInit, OnDestroy {
  declare map: L.Map;
  @ViewChild('map') mapElement!: ElementRef;
  @Input() loggedUser!: User;
  showMarkerInputs: boolean = false;
  latitude!: number;
  longitude!: number;
  latMapInit: number = 0;
  longMapInit: number = 0;
  zoomMapInit: number = 3;
  markers: Marker[] = [];

  toggleAddMarker() {
    this.showMarkerInputs = !this.showMarkerInputs;

    if (this.showMarkerInputs) {
      this.map.on('click', this.handleMapClick);
    } else {
      this.map.off('click', this.handleMapClick);
    }
  }

  constructor(private mapService: MapService, private userService: UserService) {
    this.userService.getPrimaryRegionDetailsOfUser(localStorage.getItem('username') ?? '').subscribe({
      next: (response: RegionDetailsPayload) => {
        if (response.id != null) {
          this.latMapInit = response.latitude;
          this.longMapInit = response.longitude;
          this.zoomMapInit = 11;
        }
        // Initialize the map once the response is received
        this.initializeMap();
      }
    });
  }

  ngAfterViewInit() {
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
    this.map = L.map(this.mapElement.nativeElement).setView([this.latMapInit, this.longMapInit], this.zoomMapInit);
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
      next: (response: Marker) => {
        console.log('Marker added successfully', response);

        this.markers.push(response);
        this.placeMarkerOnTheMap(response);
      },
      error: (error) => {
        console.error('Error adding marker', error);
      }
    });
  }

  placeMarkersOnMap() {
    for (const marker of this.markers) {
      this.placeMarkerOnTheMap(marker);
    }
  }

  getMarkerSelectionCursor() {
    return {
      'click-cursor': this.showMarkerInputs
    }
  }

  placeMarkerOnTheMap(marker: Marker) {
    L.marker([marker.latitude, marker.longitude]).addTo(this.map).bindPopup(
      `<h3>${marker.title}</h3>
      <p>${marker.description}</p>
      <p>start: ${marker.startDate}</p>
      <p>end: ${marker.endDate}</p>`);
  }

  get isLoggedUserAdmin(): boolean {
    return this.loggedUser?.role === Role.ADMIN;
  }
}
