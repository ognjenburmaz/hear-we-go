export interface UserActivity {
  eventType: string;
  payload: Record<string, any>;
  timestamp: string;
}
export interface UserAnalytics {
  totalSongsListened: number;
  averageRating: number;
  songsByGenre: { [key: string]: number };
  top5Artists: { [key: string]: number };
  subscribedArtistsCount: number;
}
