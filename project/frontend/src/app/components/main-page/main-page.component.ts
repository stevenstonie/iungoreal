import { Component } from '@angular/core';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.scss']
})
export class MainPageComponent {
  email: string;
  showMap: boolean = false;

  toggleMap() {
    this.showMap = !this.showMap;
   }   

  constructor() {
    this.email = localStorage.getItem('email') ?? '';
  }
}
