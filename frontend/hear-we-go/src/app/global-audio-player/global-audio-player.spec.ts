import {ComponentFixture, TestBed} from '@angular/core/testing';

import {GlobalAudioPlayer} from './global-audio-player';

describe('GlobalAudioPlayer', () => {
  let component: GlobalAudioPlayer;
  let fixture: ComponentFixture<GlobalAudioPlayer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GlobalAudioPlayer]
    })
      .compileComponents();

    fixture = TestBed.createComponent(GlobalAudioPlayer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
