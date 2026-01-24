import {CommonModule} from '@angular/common';
import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {Song, SongService} from '../../services/song-service';
import {Album, AlbumService} from '../../services/album-service';
import {SongChangeService} from '../../services/song-change-service';

@Component({
  selector: 'app-songs-get-by-album-user-component',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './songs-get-by-album-user-component.html',
  styleUrl: './songs-get-by-album-user-component.css',
})
export class SongsGetByAlbumUserComponent implements OnInit {

  songs: Song[] = [];
  albumId: string | null = null;
  album: Album | null = null;
  token: any = null;

  constructor(private router: Router,
              private cdr: ChangeDetectorRef,
              private service: SongService,
              private albumService: AlbumService,
              private route: ActivatedRoute,
              private changeService: SongChangeService) {

  }

  ngOnInit(): void {
    this.albumId = this.route.snapshot.paramMap.get('id');
    this.FindAlbum();
    this.LoadAllSongs();
    this.token = localStorage.getItem("authToken");
  }

  LoadAllSongs(): void {
    this.service.getAllByAlbum(this.albumId).subscribe({
      next: (songs) => {
        this.songs = songs;
        // Manually tell Angular to update the UI now that we have data
        this.cdr.detectChanges();
      },
      error: (err) => console.error("Error loading songs:", err)
    });
  }

  FindAlbum() {
    if (this.albumId != null) {
      this.albumService.getOne(this.albumId).subscribe({
        next: (album: Album) => {
          this.album = album;
          this.cdr.detectChanges(); // Update UI for album details
        },
        error: (err) => console.error("Error loading album:", err)
      });
    }
  }

  playSong(song: Song): void {
    this.setCurrentlyPlayingSong(song.title, song.id, song.durationSeconds, song.genre);
  }

  setCurrentlyPlayingSong(name: string, id: string, duration: number, genre: string): void {
    localStorage.setItem("currentSongName", name);
    localStorage.setItem("currentSongId", id)
    localStorage.setItem("currentSongDuration", duration as unknown as string)
    localStorage.setItem("currentSongGenre", genre)
    this.changeService.requestChange()
  }

  formatDuration(seconds: number): string {
    if (!seconds && seconds !== 0) return '0:00';

    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;


    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  protected readonly localStorage = localStorage;

  refreshSingleSong(songId: string): void {
    console.log("Osvežavam pesmu sa ID:", songId);
    this.service.getOne(songId).subscribe({
      next: (updatedSong) => {
        console.log("Stigli novi podaci sa beka:", updatedSong);
        const index = this.songs.findIndex(s => s.id === songId);
        if (index !== -1) {
          this.songs[index] = updatedSong;
          this.cdr.detectChanges();
        }
      }
    });
  }

  isChanging(song: any, v: number): boolean {
    if (!song.oldRating && song.oldRating !== 0) return v <= song.userRating;
    const min = Math.min(song.oldRating, song.userRating);
    const max = Math.max(song.oldRating, song.userRating);
    return v > min && v <= max;
  }

  rate(song: any, value: number): void {
    song.oldRating = song.userRating || 0;
    song.userRating = value;
    song.isLoading = true;

    setTimeout(() => {
      this.service.rateSong(song.id, value).subscribe({
        next: () => {
          this.refreshSingleSong(song.id);
          setTimeout(() => {
            song.isLoading = false;
            song.oldRating = value;
          }, 600);
        },
        error: () => song.isLoading = false
      });
    }, 800);
  }
  removeRating(song: any) {
    song.isBreaking = true;

    setTimeout(() => {
      this.service.deleteRating(song.id).subscribe({
        next: () => {
          song.userRating = 0;
          this.refreshSingleSong(song.id);
          setTimeout(() => song.isBreaking = false, 300);
        },
        error: () => song.isBreaking = false
      });
    }, 600);
  }


}
