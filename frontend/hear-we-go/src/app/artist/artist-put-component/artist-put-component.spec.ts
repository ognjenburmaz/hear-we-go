import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ArtistPutComponent } from './artist-put-component';

describe('ArtistPutComponent', () => {
  let component: ArtistPutComponent;
  let fixture: ComponentFixture<ArtistPutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ArtistPutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ArtistPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
