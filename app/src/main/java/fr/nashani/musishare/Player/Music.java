package fr.nashani.musishare.Player;

import java.util.List;

public class Music {

    private String ID;
    private String Name;
    private List<String> artists;
    private String album;
    private String albumImage;

    public Music(String ID, String name, List<Artist> artists, String album, String albumImage, List<Gender> genders) {
        this.ID = ID;
        Name = name;
        this.album = album;
        this.albumImage = albumImage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }


    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumImage() {
        return albumImage;
    }

    public void setAlbumImage(String albumImage) {
        this.albumImage = albumImage;
    }

}
