package fr.nashani.musishare.Cards;

/**
 * The type Card.
 */
public class Card {
    private String userId;
    private String name;
    private String phone;
    private String profileImageUrl;
    private String trackName;
    private String trackArtist;
    private String trackAlbum;
    private String trackAlbumCover;

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    private String address;

    /**
     * Instantiates a new Card.
     *
     * @param userId          the user id
     * @param name            the name
     * @param profileImageUrl the profile image url
     */
    public Card(String userId, String name, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;

    }

    /**
     * Instantiates a new Card.
     *
     * @param userId          the user id
     * @param name            the name
     * @param trackName       the track name
     * @param trackArtist     the track artist
     * @param trackAlbum      the track album
     * @param trackAlbumCover the track album cover
     * @param profileImageUrl the profile image url
     */
    public Card(String userId, String name, String trackName, String trackArtist, String trackAlbum, String trackAlbumCover, String profileImageUrl, String address) {
        this.userId = userId;
        this.name = name;
        this.trackName = trackName;
        this.trackArtist = trackArtist;
        this.trackAlbum = trackAlbum;
        this.profileImageUrl = profileImageUrl;
        this.trackAlbumCover = trackAlbumCover;
        this.address = address;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets profile image url.
     *
     * @return the profile image url
     */
    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    /**
     * Sets profile image url.
     *
     * @param profileImageUrl the profile image url
     */
    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
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
     * Gets phone.
     *
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets phone.
     *
     * @param phone the phone
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Gets track artist.
     *
     * @return the track artist
     */
    public String getTrackArtist() {
        return trackArtist;
    }

    /**
     * Sets track artist.
     *
     * @param trackArtist the track artist
     */
    public void setTrackArtist(String trackArtist) {
        this.trackArtist = trackArtist;
    }

    /**
     * Gets track album.
     *
     * @return the track album
     */
    public String getTrackAlbum() {
        return trackAlbum;
    }

    /**
     * Sets track album.
     *
     * @param trackAlbum the track album
     */
    public void setTrackAlbum(String trackAlbum) {
        this.trackAlbum = trackAlbum;
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
}
