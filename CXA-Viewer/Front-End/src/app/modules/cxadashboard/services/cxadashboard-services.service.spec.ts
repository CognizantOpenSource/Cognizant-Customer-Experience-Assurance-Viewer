import { TestBed } from '@angular/core/testing';

import { CxadashboardServicesService } from './cxadashboard-services.service';

describe('CxadashboardServicesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CxadashboardServicesService = TestBed.get(CxadashboardServicesService);
    expect(service).toBeTruthy();
  });
});
