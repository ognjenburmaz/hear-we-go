import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumAddComponent } from './album-add-component';

describe('AlbumAddComponent', () => {
  let component: AlbumAddComponent;
  let fixture: ComponentFixture<AlbumAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumAddComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlbumAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
