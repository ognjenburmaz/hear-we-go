import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserHistoryComponent } from './user-history-component';

describe('UserHistoryComponent', () => {
  let component: UserHistoryComponent;
  let fixture: ComponentFixture<UserHistoryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserHistoryComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(UserHistoryComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
