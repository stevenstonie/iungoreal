import { Component } from '@angular/core';
import { CountryOrRegionPayload } from 'src/app/models/Payloads';
import { CountryAndRegionsMenuOptions } from 'src/app/models/app';
import { CountryAndRegionService } from 'src/app/services/country-and-region.service';
import { UserService } from 'src/app/services/user.service';


@Component({
  selector: 'app-user-settings',
  templateUrl: './user-settings.component.html',
  styleUrls: ['./user-settings.component.scss']
})
export class UserSettingsComponent {
  selectedSection: string = 'account';
  loggedUserUsername = localStorage.getItem('username') ?? '';

  countryAndRegionsMenuNames: string[] = ['Country', 'Primary Region', 'Secondary Regions', 'Remove Secondary Region'];
  countryAndRegionsMenuOptions: CountryAndRegionsMenuOptions = {
    showCountryOptions: false,
    showPrimaryRegionOptions: false,
    showSecondaryRegionOptions: false,
    showSecondaryRegionOptionsToRemove: false
  };

  countryOfUser: CountryOrRegionPayload | null = null;
  primaryRegionOfUser: CountryOrRegionPayload | null = null;
  secondaryRegionsOfUser: CountryOrRegionPayload[] = [];

  availableRegions: CountryOrRegionPayload[] = [];
  allCountries: CountryOrRegionPayload[] = [];

  constructor(private userService: UserService, private countryAndRegionService: CountryAndRegionService) { }

  ngOnInit(): void {
    this.ngOnInitCountriesAndRegions(this.loggedUserUsername);
  }

  // countries and regions section -------------------------------------------------------------------------------------

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

          this.ngOnInitCountriesAndRegions(this.loggedUserUsername);
        }
      },
      error: (error) => {
        console.error(error);
      }
    });

    this.setAllCountryAndRegionMenuOptionsToFalse();
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

    this.setAllCountryAndRegionMenuOptionsToFalse();
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

    this.setAllCountryAndRegionMenuOptionsToFalse();
  }

  removeCountryOfUser() {
    this.userService.removeCountryOfUser(this.loggedUserUsername).subscribe({
      next: () => {
        this.getCountryOfUser(this.loggedUserUsername);

        this.ngOnInitCountriesAndRegions(this.loggedUserUsername);
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

    this.setAllCountryAndRegionMenuOptionsToFalse();
  }

  toggleShowOptions(optionMenu: string) {
    this.setAllCountryAndRegionMenuOptionsToFalse();
    if (optionMenu === this.countryAndRegionsMenuNames.at(0)!) {
      this.countryAndRegionsMenuOptions.showCountryOptions = true;
    } else if (optionMenu === this.countryAndRegionsMenuNames.at(1)!) {
      this.countryAndRegionsMenuOptions.showPrimaryRegionOptions = true;
    } else if (optionMenu === this.countryAndRegionsMenuNames.at(2)!) {
      this.countryAndRegionsMenuOptions.showSecondaryRegionOptions = true;
    } else if (optionMenu === this.countryAndRegionsMenuNames.at(3)!) {
      this.countryAndRegionsMenuOptions.showSecondaryRegionOptionsToRemove = true;
    }
  }

  setAllCountryAndRegionMenuOptionsToFalse() {
    this.countryAndRegionsMenuOptions.showCountryOptions = false;
    this.countryAndRegionsMenuOptions.showPrimaryRegionOptions = false;
    this.countryAndRegionsMenuOptions.showSecondaryRegionOptions = false;
    this.countryAndRegionsMenuOptions.showSecondaryRegionOptionsToRemove = false;
  }

  //  -----------------------------------------------------------------------------------------------

  selectSection(sectionName: string) {
    this.selectedSection = sectionName;
  }

}
