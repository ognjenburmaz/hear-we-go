import { ChangeDetectorRef, Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { SongChangeService } from '../services/song-change-service';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-global-audio-player',
  standalone: true, // or imports if not standalone
  imports: [DatePipe],
  templateUrl: './global-audio-player.html',
  styleUrl: './global-audio-player.css',
})
export class GlobalAudioPlayer implements OnInit, OnDestroy {
  audio!: HTMLAudioElement;
  currentSongName: string | null = null;
  currentSongGenre: string | null = null;
  currentSongId: string | null = null;

  isPlaying = false;
  currentSeconds = 0;
  duration = 0;
  currentTime = new Date(0);

  private sub!: Subscription;
  private currentBlobUrl?: string;

  constructor(
    private cdr: ChangeDetectorRef,
    private changeService: SongChangeService
  ) {}

  ngOnInit(): void {
    // 1. IMMEDIATELY check storage so the player displays on refresh
    this.currentSongId = localStorage.getItem("currentSongId");
    this.currentSongName = localStorage.getItem("currentSongName");
    this.currentSongGenre = localStorage.getItem("currentSongGenre");
    const savedDuration = localStorage.getItem("currentSongDuration");
    this.duration = savedDuration ? Number(savedDuration) : 0;

    // 2. Link the audio element
    this.audio = document.getElementById('globalAudioPlayer') as HTMLAudioElement;

    // 3. Setup Listeners
    this.audio.addEventListener('timeupdate', () => {
      this.currentSeconds = this.audio.currentTime;
      this.currentTime = new Date(this.audio.currentTime * 1000);
      this.cdr.detectChanges();
    });

    this.audio.addEventListener('loadedmetadata', () => {
      this.duration = this.audio.duration;
      this.cdr.detectChanges();
    });

    this.audio.addEventListener('play', () => { this.isPlaying = true; this.cdr.detectChanges(); });
    this.audio.addEventListener('pause', () => { this.isPlaying = false; this.cdr.detectChanges(); });
    this.audio.addEventListener('ended', () => { this.isPlaying = false; this.cdr.detectChanges(); });

    // 4. Listen for new song requests
    this.sub = this.changeService.trigger$.subscribe(() => {
      this.currentSongId = localStorage.getItem("currentSongId");
      this.currentSongName = localStorage.getItem("currentSongName");
      this.currentSongGenre = localStorage.getItem("currentSongGenre");
      this.loadAndPlaySong();
    });
  }

  async loadAndPlaySong() {
    const token = localStorage.getItem("authToken");
    if (!this.currentSongId || !token) return;

    try {
      const response = await fetch(`/api/content/songs/${this.currentSongId}/audio`, {
        method: 'GET',
        headers: { 'Authorization': `Bearer ${token}` }
      });

      if (!response.ok) throw new Error("File not found");

      const blob = await response.blob();

      if (this.currentBlobUrl) URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = URL.createObjectURL(blob);

      this.audio.src = this.currentBlobUrl;
      this.audio.load(); // Forces reset
      await this.audio.play();
      this.cdr.detectChanges();
    } catch (err) {
      console.error("Playback error:", err);
    }
  }

  togglePlay() {
    if (this.audio.paused) this.audio.play();
    else this.audio.pause();
  }

  seek(seconds: number) {
    this.audio.currentTime = Math.max(0, Math.min(this.audio.currentTime + seconds, this.audio.duration));
  }

  onSeek(event: Event) {
    const value = (event.target as HTMLInputElement).value;
    this.audio.currentTime = Number(value);
  }

  formatSecondsToDate(seconds: number): Date {
    return new Date((seconds || 0) * 1000);
  }

  // Add this method inside your GlobalAudioPlayer class
  closePlayer() {
    // 1. Stop the audio
    if (this.audio) {
      this.audio.pause();
      this.audio.src = '';
    }

    // 2. Clear Blob URL to free memory
    if (this.currentBlobUrl) {
      URL.revokeObjectURL(this.currentBlobUrl);
      this.currentBlobUrl = undefined;
    }

    // 3. Clear logic state
    this.currentSongId = null;
    this.currentSongName = null;
    this.currentSongGenre = null;
    this.isPlaying = false;

    // 4. Clear LocalStorage so it doesn't come back on refresh
    localStorage.removeItem("currentSongId");
    localStorage.removeItem("currentSongName");
    localStorage.removeItem("currentSongGenre");
    localStorage.removeItem("currentSongDuration");

    this.cdr.detectChanges();
  }

  ngOnDestroy() {
    if (this.sub) this.sub.unsubscribe();
    if (this.currentBlobUrl) URL.revokeObjectURL(this.currentBlobUrl);
  }
}
