package applicationname.companydomain.simpleapp;

public class ArtistItem {

    private String name;
    private String url;
    private String hdURL;
    private String id;
    private boolean color;

    ArtistItem(String name, String url, String hdURL, boolean color, String id) {
        this.name = name;
        this.url = url;
        this.hdURL = hdURL;
        this.color = color;
        this.id = id;
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
