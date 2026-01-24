import {TestBed} from '@angular/core/testing';

import {RegistrationRequestsService} from './registration-requests-service';

describe('RegistrationRequestsService', () => {
  let service: RegistrationRequestsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RegistrationRequestsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
