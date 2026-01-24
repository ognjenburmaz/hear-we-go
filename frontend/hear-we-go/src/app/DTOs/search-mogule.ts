
export interface ArtistResponse {
  id: string;
  name: string;
  biography: string;
  genres: string[];
}


export interface AlbumResponse {
  id: string;
  title: string;
  releaseDate: string;
  genre: string;
  artistIds: string[];
}


export interface SongResponse {
  id: string;
  title: string;
  durationSeconds: number;
  genre: string;
  albumId: string;
  artistIds: string[];
}


export interface SearchResponse {
  artists: ArtistResponse[];
  albums: AlbumResponse[];
  songs: SongResponse[];
}
