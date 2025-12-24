import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlbumPutComponent } from './album-put-component';

describe('AlbumPutComponent', () => {
  let component: AlbumPutComponent;
  let fixture: ComponentFixture<AlbumPutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AlbumPutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AlbumPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
