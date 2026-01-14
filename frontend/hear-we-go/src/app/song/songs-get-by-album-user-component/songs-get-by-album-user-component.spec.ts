import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SongsGetByAlbumUserComponent } from './songs-get-by-album-user-component';

describe('SongsGetByAlbumUserComponent', () => {
  let component: SongsGetByAlbumUserComponent;
  let fixture: ComponentFixture<SongsGetByAlbumUserComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SongsGetByAlbumUserComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SongsGetByAlbumUserComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
