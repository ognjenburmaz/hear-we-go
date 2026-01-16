import {ComponentFixture, TestBed} from '@angular/core/testing';

import {RegistrationRequests} from './registration-requests';

describe('RegistrationRequests', () => {
  let component: RegistrationRequests;
  let fixture: ComponentFixture<RegistrationRequests>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RegistrationRequests]
    })
      .compileComponents();

    fixture = TestBed.createComponent(RegistrationRequests);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
