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
    this.setCurrentlyPlayingSong(song.title, song.id, song.durationSeconds, song.genre,song.albumId);
  }

  setCurrentlyPlayingSong(name: string, id: string, duration: number, genre: string,albumId:string): void {
    localStorage.setItem("currentSongName", name);
    localStorage.setItem("currentSongId", id)
    localStorage.setItem("currentSongDuration", duration as unknown as string)
    localStorage.setItem("currentSongGenre", genre)
    localStorage.setItem("currentSongAlbumId", albumId)
    this.changeService.requestChange()
  }

  formatDuration(seconds: number): string {
    if (!seconds && seconds !== 0) return '0:00';

    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;


    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }

  protected readonly localStorage = localStorage;
}
