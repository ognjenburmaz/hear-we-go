import {ChangeDetectorRef, Component, OnInit, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './navbar/navbar';
import {GlobalAudioPlayer} from './global-audio-player/global-audio-player';
import {SongChangeService} from './services/song-change-service';
import {Subscription} from 'rxjs';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
  imports: [
    RouterOutlet,
    NavbarComponent,
    GlobalAudioPlayer
  ],
  styleUrl: './app.css'
})
export class App implements OnInit {

  protected readonly title = signal('hear-we-go');
  protected readonly localStorage = localStorage;
  private sub!: Subscription;

  constructor(private changeService: SongChangeService, private cdr: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.sub = this.changeService.trigger$.subscribe(() => {
      this.cdr.detectChanges()
    });
  }


}
