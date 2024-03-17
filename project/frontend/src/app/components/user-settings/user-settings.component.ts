import { Component } from '@angular/core';
import { CountryOrRegionPayload } from 'src/app/models/Payloads';
import { UserService } from 'src/app/services/user.service';


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent {
  selectedSection: string = '';
  loggedUserUsername = localStorage.getItem('username') ?? '';
  currentCountry: CountryOrRegionPayload | null = null;
  currentPrimaryRegion: CountryOrRegionPayload | null = null;
  currentSecondaryRegions: CountryOrRegionPayload[] = [];
  
  constructor(private userService: UserService) { }

  ngOnInit(): void {
    this.getCountryFromDb(this.loggedUserUsername);
    this.getPrimaryRegionFromDb(this.loggedUserUsername);
    this.getSecondaryRegionsFromDb(this.loggedUserUsername);
  }

  getCountryFromDb(username: string) {
    this.userService.getCountry(username).subscribe({
      next: (country: CountryOrRegionPayload) => {
        this.currentCountry = country;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getPrimaryRegionFromDb(username: string) {
    this.userService.getPrimaryRegion(username).subscribe({
      next: (region: CountryOrRegionPayload) => {
        this.currentPrimaryRegion = region;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getSecondaryRegionsFromDb(username: string) {
    this.userService.getSecondaryRegions(username).subscribe({
      next: (regions: CountryOrRegionPayload[]) => {
        this.currentSecondaryRegions = regions;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  selectSection(section: string) {
    this.selectedSection = section;
  }
}
