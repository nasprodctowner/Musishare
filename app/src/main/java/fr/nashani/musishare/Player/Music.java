package fr.nashani.musishare.Player;

/**
 * The type Music.
 */
public class Music {

    private String trackId;
    private String trackName;
    private String trackArtists;
    private String trackAlbumName;
    private String trackAlbumCover;

    /**
     * Instantiates a new Music.
     */
    public Music() {
    }

    /**
     * Instantiates a new Music.
     *
     * @param trackId         the track id
     * @param trackName       the track name
     * @param trackArtists    the track artists
     * @param trackAlbumName  the track album name
     * @param trackAlbumCover the track album cover
     */
    public Music(String trackId, String trackName, String trackArtists, String trackAlbumName, String trackAlbumCover) {
        this.trackId = trackId;
        this.trackName = trackName;
        this.trackArtists = trackArtists;
        this.trackAlbumName = trackAlbumName;
        this.trackAlbumCover = trackAlbumCover;
    }

    /**
     * Gets track id.
     *
     * @return the track id
     */
    public String getTrackId() {
        return trackId;
    }

    /**
     * Sets track id.
     *
     * @param trackId the track id
     */
    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    /**
     * Gets track name.
     *
     * @return the track name
     */
    public String getTrackName() {
        return trackName;
    }

    /**
     * Sets track name.
     *
     * @param trackName the track name
     */
    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }


    /**
     * Gets track album name.
     *
     * @return the track album name
     */
    public String getTrackAlbumName() {
        return trackAlbumName;
    }

    /**
     * Sets track album name.
     *
     * @param trackAlbumName the track album name
     */
    public void setTrackAlbumName(String trackAlbumName) {
        this.trackAlbumName = trackAlbumName;
    }

    /**
     * Gets track album cover.
     *
     * @return the track album cover
     */
    public String getTrackAlbumCover() {
        return trackAlbumCover;
    }

    /**
     * Sets track album cover.
     *
     * @param trackAlbumCover the track album cover
     */
    public void setTrackAlbumCover(String trackAlbumCover) {
        this.trackAlbumCover = trackAlbumCover;
    }

    /**
     * Gets track artists.
     *
     * @return the track artists
     */
    public String getTrackArtists() {
        return trackArtists;
    }

    /**
     * Sets track artists.
     *
     * @param trackArtists the track artists
     */
    public void setTrackArtists(String trackArtists) {
        this.trackArtists = trackArtists;
    }
}
