package applicationname.companydomain.simpleapp;

public class ArtistItem {

    private String name;
    private String url;
    private String hdURL;
    private String id;
    private boolean color;
    private int rank;

    private float popularity;

    ArtistItem(String name, String url, String hdURL, boolean color, String id, int rank,
               float popularity) {
        this.name = name;
        this.url = url;
        this.hdURL = hdURL;
        this.color = color;
        this.id = id;
        this.rank = rank;
        this.popularity = popularity;
    }

    public int getRank() {
        return rank;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getName() {
        return name;
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

    public String getHdURL() {
        return hdURL;
    }
}
