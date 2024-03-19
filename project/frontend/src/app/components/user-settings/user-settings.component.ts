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

  showCountryOptions: boolean = false;
  showPrimaryRegionOptions: boolean = false;
  showSecondaryRegionOptions: boolean = false;
  showSecondaryRegionOptionsToRemove: boolean = false;

  countryOfUser: CountryOrRegionPayload | null = null;
  primaryRegionOfUser: CountryOrRegionPayload | null = null;
  secondaryRegionsOfUser: CountryOrRegionPayload[] = [];

  availableRegions: CountryOrRegionPayload[] = [];
  allCountries: CountryOrRegionPayload[] = [];

  constructor(private userService: UserService, private countryAndRegionService: CountryAndRegionService) { }

  ngOnInit(): void {
    this.selectedSection = 'regions';

    this.ngOnInitCountriesAndRegions(this.loggedUserUsername);
  }

  // countries and regions -------------------------------------------------------------------------------------

  ngOnInitCountriesAndRegions(username: string) {
    this.getCountryOfUser(username);
    this.getPrimaryRegionOfUser(username);
    this.getSecondaryRegionsOfUser(username);

    this.getAvailableRegionsForUser(username);
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

  getAvailableRegionsForUser(username: string) {
    this.userService.getAvailableRegionsForUser(username).subscribe({
      next: (regions: CountryOrRegionPayload[]) => {
        this.availableRegions = regions;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getCountryOfUser(username: string) {
    this.userService.getCountryOfUser(username).subscribe({
      next: (country: CountryOrRegionPayload) => {
        this.countryOfUser = country;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getPrimaryRegionOfUser(username: string) {
    this.userService.getPrimaryRegionOfUser(username).subscribe({
      next: (region: CountryOrRegionPayload) => {
        this.primaryRegionOfUser = region;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getSecondaryRegionsOfUser(username: string) {
    this.userService.getSecondaryRegionsOfUser(username).subscribe({
      next: (regions: CountryOrRegionPayload[]) => {
        this.secondaryRegionsOfUser = regions;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  setCountryForUser(country: CountryOrRegionPayload) {
    this.userService.setCountryForUser(this.loggedUserUsername, country.id).subscribe({
      next: () => {
        if (this.countryOfUser !== country) {
          this.getCountryOfUser(this.loggedUserUsername);

          this.ngOnInit();
        }
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showCountryOptions = false;
  }

  setPrimaryRegionForUser(region: CountryOrRegionPayload) {
    this.userService.setPrimaryRegionForUser(this.loggedUserUsername, region.id).subscribe({
      next: () => {
        this.getPrimaryRegionOfUser(this.loggedUserUsername);

        this.getAvailableRegionsForUser(this.loggedUserUsername);
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showPrimaryRegionOptions = false;
  }

  addSecondaryRegionForUser(region: CountryOrRegionPayload) {
    this.userService.addSecondaryRegionForUser(this.loggedUserUsername, region.id).subscribe({
      next: () => {
        this.getSecondaryRegionsOfUser(this.loggedUserUsername);

        this.getAvailableRegionsForUser(this.loggedUserUsername);
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showSecondaryRegionOptions = false;
  }

  removeCountryOfUser() {
    this.userService.removeCountryOfUser(this.loggedUserUsername).subscribe({
      next: () => {
        this.getCountryOfUser(this.loggedUserUsername);

        this.ngOnInit();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  removePrimaryRegionOfUser() {
    this.userService.removePrimaryRegionOfUser(this.loggedUserUsername).subscribe({
      next: () => {
        this.getPrimaryRegionOfUser(this.loggedUserUsername);

        this.getAvailableRegionsForUser(this.loggedUserUsername);
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  removeSecondaryRegionOfUser(region: CountryOrRegionPayload) {
    this.userService.removeSecondaryRegionOfUser(this.loggedUserUsername, region.id).subscribe({
      next: () => {
        this.getSecondaryRegionsOfUser(this.loggedUserUsername);

        this.getAvailableRegionsForUser(this.loggedUserUsername);
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.showSecondaryRegionOptionsToRemove = false;
  }

  //  -----------------------------------------------------------------------------------------------

  selectSection(section: string) {
    this.selectedSection = section;
  }
}
