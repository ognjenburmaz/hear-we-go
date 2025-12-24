import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SongsGetByAlbumComponent } from './songs-get-by-album-component';

describe('SongsGetByAlbumComponent', () => {
  let component: SongsGetByAlbumComponent;
  let fixture: ComponentFixture<SongsGetByAlbumComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SongsGetByAlbumComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SongsGetByAlbumComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
