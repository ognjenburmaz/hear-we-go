import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtistAddComponent } from './artist-add-component';

describe('ArtistAddComponent', () => {
  let component: ArtistAddComponent;
  let fixture: ComponentFixture<ArtistAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArtistAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
