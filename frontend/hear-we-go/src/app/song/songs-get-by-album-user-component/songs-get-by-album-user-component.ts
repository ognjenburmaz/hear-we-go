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


  protected readonly localStorage = localStorage;


  FindAlbum() {
    if (this.albumId != null) {
      this.albumService.getOne(this.albumId).subscribe
      ({
        next: (album: Album) => {

          this.album = album;

          if (this.album != undefined) {
            this.cdr.detectChanges();
          }
          this.cdr.detectChanges();

        },
        error: (_) => console.log("greska")
      })
    }
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

  LoadAllSongs(): void {
    this.service.getAllByAlbum(this.albumId).subscribe(songs => {
      this.songs = songs;

      for (const song of this.songs) {
        fetch(`/api/content/songs/${song.id}/audio`, {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${this.token}`
          }
        })
          .then(res => {
            if (!res.ok) {
              console.warn(`Audio not found for song ${song.id}, status: ${res.status}`);
              this.cdr.detectChanges();
              return null;
            }
            return res.blob();
          })
          .then(blob => {
            if (!blob) return;
            const url = URL.createObjectURL(blob);
            const audio = document.getElementById(`audioPlayer${song.id}`) as HTMLAudioElement;
            audio.src = url;
            // audio.play();
            this.cdr.detectChanges();
          })
          .catch(err => {
            console.error(`Error fetching audio for song ${song.id}:`, err);
            this.cdr.detectChanges();
          });
      }
    });
  }
}
