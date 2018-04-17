package applicationname.companydomain.simpleapp;

public class TrackItem {
    private String title;
    private String artist;
    private String url;
    private boolean color;

    public TrackItem(String title, String artist, String url, boolean color) {
        this.title = title;
        this.artist = artist;
        this.url = url;
        this.color = color;
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
}
