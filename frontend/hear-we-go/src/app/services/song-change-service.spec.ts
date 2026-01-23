import {TestBed} from '@angular/core/testing';

import {SongChangeService} from './song-change-service';

describe('SongChangeService', () => {
  let service: SongChangeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SongChangeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
