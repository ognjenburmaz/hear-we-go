import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumsGetByArtistComponent } from './albums-get-by-artist-component';

describe('AlbumsGetByArtistComponent', () => {
  let component: AlbumsGetByArtistComponent;
  let fixture: ComponentFixture<AlbumsGetByArtistComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumsGetByArtistComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlbumsGetByArtistComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
