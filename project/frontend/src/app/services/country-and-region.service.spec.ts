import { TestBed } from '@angular/core/testing';

import { CountryAndRegionService } from './country-and-region.service';

describe('CountryAndRegionService', () => {
  let service: CountryAndRegionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CountryAndRegionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
