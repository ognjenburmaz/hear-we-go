import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SongPutComponent } from './song-put-component';

describe('SongPutComponent', () => {
  let component: SongPutComponent;
  let fixture: ComponentFixture<SongPutComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SongPutComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(SongPutComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
