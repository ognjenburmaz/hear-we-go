import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SongDeleteComponent } from './song-delete-component';

describe('SongDeleteComponent', () => {
  let component: SongDeleteComponent;
  let fixture: ComponentFixture<SongDeleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SongDeleteComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SongDeleteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
