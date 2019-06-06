package fr.nashani.musishare.Player;

public class Music {

    private String trackId;
    private String trackName;
    private String trackArtists;
    private String trackAlbumName;
    private String trackAlbumCover;

    public Music() {
    }

    public Music(String trackId, String trackName, String trackArtists, String trackAlbumName, String trackAlbumCover) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackArtists = trackArtists;
        this.trackAlbumName = trackAlbumName;
        this.trackAlbumCover = trackAlbumCover;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }


    public String getTrackAlbumName() {
        return trackAlbumName;
    }

    public void setTrackAlbumName(String trackAlbumName) {
        this.trackAlbumName = trackAlbumName;
    }

    public String getTrackAlbumCover() {
        return trackAlbumCover;
    }

    public void setTrackAlbumCover(String trackAlbumCover) {
        this.trackAlbumCover = trackAlbumCover;
    }

    public String getTrackArtists() {
        return trackArtists;
    }

    public void setTrackArtists(String trackArtists) {
        this.trackArtists = trackArtists;
    }
}
