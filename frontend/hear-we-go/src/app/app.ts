import {Component, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {NavbarComponent} from './navbar/navbar';
import {GlobalAudioPlayer} from './global-audio-player/global-audio-player';

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
export class App {
  protected readonly title = signal('hear-we-go');
}
