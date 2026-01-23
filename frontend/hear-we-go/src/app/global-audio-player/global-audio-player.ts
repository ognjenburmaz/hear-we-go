import {ChangeDetectorRef, Component, OnDestroy, OnInit} from '@angular/core';
import {SongService} from '../services/song-service';
import {AlbumService} from '../services/album-service';
import {ActivatedRoute, Router} from '@angular/router';
import {Subscription} from 'rxjs';
import {SongChangeService} from '../services/song-change-service';

@Component({
  selector: 'app-global-audio-player',
  imports: [],
  templateUrl: './global-audio-player.html',
  styleUrl: './global-audio-player.css',
})
export class GlobalAudioPlayer implements OnInit, OnDestroy {

  time = new Date().toLocaleTimeString();
  currentSongName?: string | null;

  // songs: Song[] = [];
  // albumId: string | null = null;
  // album: Album | null = null;
  // token: currentSong any = null;
  currentSongId?: string | null;
  private sub!: Subscription;
  private token?: string | undefined | null

  constructor(private router: Router,
              private cdr: ChangeDetectorRef,
              private service: SongService,
              private albumService: AlbumService,
              private route: ActivatedRoute,
              private changeService: SongChangeService) {

  }

  ngOnInit(): void {
    this.sub = this.changeService.trigger$.subscribe(() => {
      this.time = new Date().toLocaleTimeString();
      this.token = localStorage.getItem("authToken")
      this.currentSongName = localStorage.getItem("currentSongName")
      this.currentSongId = localStorage.getItem("currentSongId")
      fetch(`/api/content/songs/${this.currentSongId}/audio`, {
        method: 'GET',
        headers: {
          'Authorization': `Bearer ${this.token}`
        }
      })
        .then(res => res.blob())
        .then(blob => {
          const url = URL.createObjectURL(blob);
          const audio = document.getElementById(`globalAudioPlayer`) as HTMLAudioElement;
          audio.src = url;
          audio.play();
          this.cdr.detectChanges()
        });
      this.cdr.detectChanges();
      this.token = localStorage.getItem("authToken")
    });
    // this.currentSongName = localStorage.getItem("currentSongName")
    // this.currentSongId = localStorage.getItem("currentSongId")
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }

}
