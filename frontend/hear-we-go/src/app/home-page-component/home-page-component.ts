import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router, RouterModule } from '@angular/router';
import { Artist, ArtistService } from '../services/artist-service';
import { ContentService } from '../services/content-service';
import { Subject, debounceTime, distinctUntilChanged, switchMap, of } from 'rxjs';
import { CommonModule } from '@angular/common';

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

  private searchTerms = new Subject<string>();

  constructor(
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private service: ArtistService,
    private contentService: ContentService
  ) { }

  ngOnInit(): void {
    // Prvi load - inicijalizujemo i artiste i listu dugmića (žanrova)
    this.LoadAllArtists(true);

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
}
