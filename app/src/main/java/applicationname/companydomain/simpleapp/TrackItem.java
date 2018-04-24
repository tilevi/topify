package applicationname.companydomain.simpleapp;

public class TrackItem {
    private String title;
    private String artist;
    private String url;
    private String id;
    private boolean color;

    public TrackItem(String title, String artist, String url, boolean color, String id) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.color = color;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public String getURL() {
        return url;
    }

    public boolean getTheColor() {
        return color;
    }

    public String getID() {
        return id;
    }
}
