package fr.nashani.musishare.Matches;

public class Match {

    private String userId;
    private String name;
    private String profileImageUrl;
    private String trackName;

    public Match(String userId, String name, String profileImageUrl, String trackName) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.trackName = trackName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}
