package applicationname.companydomain.simpleapp;

public class ArtistItem {

    private String name;
    private String url;
    private boolean color;

    ArtistItem(String name, String url, boolean color) {
        this.name = name;
        this.url = url;
        this.color = color;
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
}
