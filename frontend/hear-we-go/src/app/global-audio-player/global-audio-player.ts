import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {SongService} from '../services/song-service';
import {AlbumService} from '../services/album-service';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {SongChangeService} from '../services/song-change-service';
import {DatePipe} from '@angular/common';

@Component({
  selector: 'app-global-audio-player',
  imports: [
    DatePipe
  ],
  templateUrl: './global-audio-player.html',
  styleUrl: './global-audio-player.css',
})
export class GlobalAudioPlayer implements OnInit, OnDestroy {

  audio!: HTMLAudioElement;

  time = new Date().toLocaleTimeString();
  currentSongName?: string | null;
  currentSongGenre?: string | null;

  currentSongId?: string | null;
  private sub!: Subscription;
  private token?: string | undefined | null

  isPlaying = false;
  currentSeconds = 0;
  duration = 0;
  currentTime = new Date(0);

  constructor(private router: Router,
              private cdr: ChangeDetectorRef,
              private service: SongService,
              private albumService: AlbumService,
              private route: ActivatedRoute,
              private changeService: SongChangeService) {

  }

  ngOnInit(): void {
    this.audio = document.getElementById(
      'globalAudioPlayer'
    ) as HTMLAudioElement;

    this.audio.addEventListener('timeupdate', () => {
      this.currentSeconds = this.audio.currentTime;
      this.currentTime = new Date(this.audio.currentTime * 1000);
      this.cdr.detectChanges();
    });

    this.audio.addEventListener('loadedmetadata', () => {
      this.duration = this.audio.duration;
    });

    this.audio.addEventListener('ended', () => {
      this.isPlaying = false;
    });

    this.sub = this.changeService.trigger$.subscribe(() => {
      this.time = new Date().toLocaleTimeString();
      this.token = localStorage.getItem("authToken")
      this.currentSongName = localStorage.getItem("currentSongName")
      this.currentSongId = localStorage.getItem("currentSongId")
      this.duration = localStorage.getItem("currentSongDuration") as unknown as number
      this.currentSongGenre = localStorage.getItem("currentSongGenre")

      const audio = document.getElementById(`globalAudioPlayer`) as HTMLAudioElement;
      fetch(`/api/content/songs/${this.currentSongId}/audio`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.token}`
        }
      })
        .then(res => res.blob())
        .then(blob => {
          const url = URL.createObjectURL(blob);
          audio.src = url;
          // audio.play();
          this.cdr.detectChanges()
        });
      audio.play().catch(err => {
        console.warn('Autoplay blocked:', err);
        // TODO autoplay is only working the first time after refresh
      });
      this.cdr.detectChanges();
      this.token = localStorage.getItem("authToken")
    });
    // this.currentSongName = localStorage.getItem("currentSongName")
    // this.currentSongId = localStorage.getItem("currentSongId")
  }

  togglePlay() {
    if (this.audio.paused) {
      this.audio.play();
      this.isPlaying = true;
    } else {
      this.audio.pause();
      this.isPlaying = false;
    }
  }

  seek(seconds: number) {
    this.audio.currentTime = Math.max(
      0,
      Math.min(this.audio.currentTime + seconds, this.audio.duration)
    );
  }

  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audio.currentTime = Number(value);
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
