import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {AuthService} from '../services/auth.service';
import {Router, RouterModule} from '@angular/router';
import {Artist, ArtistService} from '../services/artist-service';
import {ContentService} from '../services/content-service';
import {debounceTime, distinctUntilChanged, of, Subject, switchMap} from 'rxjs';
import {CommonModule} from '@angular/common';
import {Song, SongService} from '../services/song-service';
import {SongChangeService} from '../services/song-change-service';

@Component({
  selector: 'app-home-page-component',
  standalone: true,
  imports: [RouterModule, CommonModule],
  templateUrl: './home-page-component.html',
  styleUrl: './home-page-component.css',
})
export class HomePageComponent implements OnInit {
  artists: Artist[] = [];
  filteredArtists: Artist[] = [];
  allAvailableGenres: string[] = [];
  searchResults: any = null;
  selectedGenres: string[] = [];
  showGenres: boolean = false;
  recommendedSongs: Song[] = []

  private searchTerms = new Subject<string>();
  protected readonly localStorage = localStorage;

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private service: ArtistService,
    private contentService: ContentService,
    private songService: SongService,
    private changeService: SongChangeService
  ) {
  }

  ngOnInit(): void {
    // Prvi load - inicijalizujemo i artiste i listu dugmića (žanrova)
    this.LoadAllArtists(true);
    this.GetRecommendedSongs(localStorage.getItem("username"))

    this.searchTerms.pipe(
      debounceTime(300),
      distinctUntilChanged(),
      switchMap(term => term.length >= 2 ? this.contentService.globalSearch(term) : of(null))
    ).subscribe(results => {
      this.searchResults = results;
      this.cdr.detectChanges();
    });
  }

  LoadAllArtists(isInitial: boolean = false): void {
    this.service.getAll(this.selectedGenres).subscribe({
      next: (artists: Artist[]) => {
        this.artists = artists || [];
        this.filteredArtists = this.artists;

        // Ako je prvi put, napuni listu dugmića svim mogućim žanrovima iz baze
        if (isInitial) {
          const all = this.artists.flatMap(a => a.genres || []);
          this.allAvailableGenres = [...new Set(all)];
        }

        this.cdr.detectChanges();
      },
      error: (err) => console.error("Greška pri učitavanju:", err)
    });
  }

  get availableGenres(): string[] {
    return this.allAvailableGenres;
  }

  toggleGenre(genre: string): void {
    if (genre === '') {
      this.selectedGenres = [];
    } else {
      const index = this.selectedGenres.indexOf(genre);
      if (index > -1) {
        this.selectedGenres.splice(index, 1);
      } else {
        this.selectedGenres.push(genre);
      }
    }
    // Ponovo zove bek, ali NE menja listu dugmića (isInitial je false)
    this.LoadAllArtists(false);
  }

  isGenreSelected(genre: string): boolean {
    if (genre === '' && this.selectedGenres.length === 0) return true;
    return this.selectedGenres.includes(genre);
  }

  onSearch(event: any): void {
    this.searchTerms.next(event.target.value);
  }

  onLogout(): void {
    this.authService.logout();
  }

  GetRecommendedSongs(username: string | null) {
    this.songService.getRecommended(username).subscribe({
      next: (songs: Song[]) => {
        this.recommendedSongs = songs;

        this.cdr.detectChanges();
      },
      error: (err) => console.error("Greška pri učitavanju propruka:", err)
    });
  }

  playSong(song: Song): void {
    this.setCurrentlyPlayingSong(song.title, song.id, song.durationSeconds, song.genre, song.albumId);
  }

  setCurrentlyPlayingSong(name: string, id: string, duration: number, genre: string, albumId: string): void {
    localStorage.setItem("currentSongName", name);
    localStorage.setItem("currentSongId", id)
    localStorage.setItem("currentSongDuration", duration as unknown as string)
    localStorage.setItem("currentSongGenre", genre)
    localStorage.setItem("currentSongAlbumId", albumId)
    this.changeService.requestChange()
  }

  formatDuration(seconds: number): string {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  }
}
