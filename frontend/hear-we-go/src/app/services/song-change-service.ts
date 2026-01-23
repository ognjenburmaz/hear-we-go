import {Injectable} from '@angular/core';
import {Subject} from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class SongChangeService {
  private trigger = new Subject<void>();
  trigger$ = this.trigger.asObservable();

  requestChange() {
    this.trigger.next();
  }
}
