import {CommonModule} from '@angular/common';
import {ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {firstValueFrom} from 'rxjs';
import {ActivatedRoute, Router, RouterModule} from '@angular/router';
import {Album, AlbumService} from '../../services/album-service';
import {ArtistService} from '../../services/artist-service';
import {SubRequest, SubscriptionService} from '../../services/subscription-service';

@Component({
  selector: 'app-albums-get-by-artist-component',
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './albums-get-by-artist-component.html',
  styleUrl: './albums-get-by-artist-component.css',
})
export class AlbumsGetByArtistComponent implements OnInit {

  albums: Album[] = [];
  artistId: string | null = null;
  artistName = "";

  // Artist State
  isArtistSubscribed: boolean = false;

  // Genre State
  uniqueGenres: string[] = [];
  genreSubscriptionStatus: { [genre: string]: boolean } = {};

  subscriptionErrorMessage: string | null = null;
  private errorTimeout?: number;

  constructor(
    private router: Router,
    private cdr: ChangeDetectorRef,
    private service: AlbumService,
    private artistService: ArtistService,
    private subService: SubscriptionService,
    private route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      if (!id) return;

      this.artistId = id;
      this.LoadData(id);
    })
  }

  async LoadData(artistId: string): Promise<void> {
    try {
      const data = await firstValueFrom(this.service.getArtistAlbums(artistId));

      this.artistName = data.artistName;
      this.albums = data.albums;

      const allGenres = this.albums.map(a => a.genre).filter(g => !!g);
      this.uniqueGenres = [...new Set(allGenres)];

      this.uniqueGenres.forEach(g => this.genreSubscriptionStatus[g] = false);

      await this.checkSubscriptionStatus(artistId);

      this.cdr.detectChanges();
    } catch (error) {
      console.error("Error loading artist data:", error);
    }
  }

  async checkSubscriptionStatus(artistId: string) {
    try {
      const mySubs = await firstValueFrom(this.subService.getMySubscriptions());

      // Check Artist
      this.isArtistSubscribed = mySubs.some(sub => sub.targetId === artistId);

      // Check Genres (Compare Uppercase IDs because Backend stores Genre IDs as Uppercase)
      this.uniqueGenres.forEach(genre => {
        const isSubbed = mySubs.some(sub =>
          sub.type === 'GENRE' && sub.targetId === genre.toUpperCase()
        );
        this.genreSubscriptionStatus[genre] = isSubbed;
      });

    } catch (error) {
      console.error("Could not fetch subscriptions", error);
    }
  }

  async toggleArtistSubscription() {
    if (!this.artistId) return;
    try {
      if (this.isArtistSubscribed) {
        this.isArtistSubscribed = false;
        this.cdr.detectChanges();
        await firstValueFrom(this.subService.unsubscribe(this.artistId));
      } else {
        const req: SubRequest = {
          targetId: this.artistId,
          targetName: this.artistName,
          type: 'ARTIST'
        };
        this.isArtistSubscribed = true;
        this.cdr.detectChanges();
        await firstValueFrom(this.subService.subscribe(req));
      }
      this.cdr.detectChanges();
    } catch (error) {
      if (this.isArtistSubscribed) {

        this.showSubscriptionError(
          "Failed to subscribe to an artist, please try again :("
        );

        this.isArtistSubscribed = false;
        this.cdr.detectChanges();
      } else {

        this.showSubscriptionError(
          "Failed to unsubscribe from an artist, please try again :("
        );

        this.isArtistSubscribed = true;
        this.cdr.detectChanges();
      }
      console.error("Error toggling artist:", error);
    }
  }

  async toggleGenreSubscription(genre: string) {
    // Backend expects Genre ID to be Uppercase (usually) or normalized
    const genreId = genre.toUpperCase();

    try {
      if (this.genreSubscriptionStatus[genre]) {
        // Unsubscribe
        this.genreSubscriptionStatus[genre] = false;
        this.cdr.detectChanges()

        await firstValueFrom(this.subService.unsubscribe(genreId));
        console.log(`Unsubscribed from ${genre}`);
      } else {
        // Subscribe
        const req: SubRequest = {
          targetId: genreId,
          targetName: genre,
          type: 'GENRE'
        };
        this.genreSubscriptionStatus[genre] = true;
        this.cdr.detectChanges()

        await firstValueFrom(this.subService.subscribe(req));
        console.log(`Subscribed to ${genre}`);
      }
      this.cdr.detectChanges();
    } catch (error) {
      if (this.genreSubscriptionStatus[genre]) {

        this.showSubscriptionError(
          "Failed to subscribe to a genre, please try again :("
        );

        this.genreSubscriptionStatus[genre] = false;
        this.cdr.detectChanges();
      } else {

        this.showSubscriptionError(
          "Failed to unsubscribe from a genre, please try again :("
        );

        this.genreSubscriptionStatus[genre] = true;
        this.cdr.detectChanges();
      }
      console.error("Error toggling genre:", error);
    }
  }

  showSubscriptionError(message: string) {
    this.subscriptionErrorMessage = message;
    this.cdr.detectChanges()

    clearTimeout(this.errorTimeout);
    this.errorTimeout = window.setTimeout(() => {
      this.subscriptionErrorMessage = null;
      this.cdr.detectChanges();
    }, 5000);
  }

}
