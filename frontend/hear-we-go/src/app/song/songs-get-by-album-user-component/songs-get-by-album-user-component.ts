import {CommonModule} from '@angular/common';
import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {Song, SongService} from '../../services/song-service';
import {Album, AlbumService} from '../../services/album-service';

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
              private route: ActivatedRoute) {

  }

  ngOnInit(): void {
    this.albumId = this.route.snapshot.paramMap.get('id');
    this.FindAlbum();
    this.LoadAllSongs();
    this.token = localStorage.getItem("authToken");
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
          .then(res => res.blob())
          .then(blob => {
            const url = URL.createObjectURL(blob);
            const audio = document.getElementById(`audioPlayer${song.id}`) as HTMLAudioElement;
            audio.src = url;
            // audio.play();
            this.cdr.detectChanges()
          });
      }
    });
  }

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

  formatDuration(seconds: number): string {
    if (!seconds && seconds !== 0) return '0:00';

    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;


    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
}
