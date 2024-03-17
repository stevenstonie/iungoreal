import { Component } from '@angular/core';
import { CountryOrRegionPayload } from 'src/app/models/Payloads';
import { CountryAndRegionService } from 'src/app/services/country-and-region.service';
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

  showCountryOptions: boolean = false;
  showPrimaryRegionOptions: boolean = false;
  showSecondaryRegionOptions: boolean = false;

  availableRegions: CountryOrRegionPayload[] = [];
  allCountries: CountryOrRegionPayload[] = [];

  constructor(private userService: UserService, private countryAndRegionService: CountryAndRegionService) { }

  ngOnInit(): void {
    this.getCountryFromDb(this.loggedUserUsername);
    this.getPrimaryRegionFromDb(this.loggedUserUsername);
    this.getSecondaryRegionsFromDb(this.loggedUserUsername);

    this.getAvailableRegions(this.loggedUserUsername);
    this.getAllCountries();
  }

  getAllCountries() {
    this.countryAndRegionService.getAllCountries().subscribe({
      next: (countries: CountryOrRegionPayload[]) => {
        this.allCountries = countries;
      },
      error: (error) => {
        console.error(error);
      }
    });
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

  getAvailableRegions(username: string) {
    this.userService.getAvailableRegions(username).subscribe({
      next: (regions: CountryOrRegionPayload[]) => {
        this.availableRegions = regions;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  selectPrimaryRegion(region: CountryOrRegionPayload) {
    this.userService.setPrimaryRegion(this.loggedUserUsername, region.id).subscribe({
      next: () => {
        this.getPrimaryRegionFromDb(this.loggedUserUsername);

        this.getAvailableRegions(this.loggedUserUsername);
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showPrimaryRegionOptions = false;
  }

  selectCountry(country: CountryOrRegionPayload) {
    this.userService.setCountry(this.loggedUserUsername, country.id).subscribe({
      next: () => {
        this.getCountryFromDb(this.loggedUserUsername);

        this.ngOnInit();
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showCountryOptions = false;
  }

  selectSection(section: string) {
    this.selectedSection = section;
  }
}
