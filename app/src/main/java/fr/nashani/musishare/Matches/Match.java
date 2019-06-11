package fr.nashani.musishare.Matches;

/**
 * The type Match.
 */
public class Match {

    private String userId;
    private String name;
    private String profileImageUrl;
    private String trackName;

    /**
     * Instantiates a new Match.
     *
     * @param userId          the user id
     * @param name            the name
     * @param profileImageUrl the profile image url
     * @param trackName       the track name
     */
    public Match(String userId, String name, String profileImageUrl, String trackName) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.trackName = trackName;
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
}
