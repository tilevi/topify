package applicationname.companydomain.simpleapp;

public class TrackItem {
    private String title;
    private String artist;
    private String url;
    private String id;
    private boolean color;

    private int rank;

    private float popularity;
    private float danceability;
    private float energy;
    private float happiness;

    public int getRank() {
        return rank;
    }

    public float getPopularity() {
        return popularity;
    }


    public float getDanceability() {
        return danceability;
    }

    public float getEnergy() {
        return energy;
    }
    public float getHappiness() {
        return happiness;
    }

    public TrackItem(String title, String artist, String url, boolean color, String id,
                     int rank,
                     float popularity, float danceability, float energy, float happiness) {

        this.title = title;
        this.artist = artist;
        this.url = url;
        this.color = color;

        this.rank = rank;
        this.id = id;

        this.popularity = popularity;
        this.danceability = danceability;
        this.energy = energy;
        this.happiness = happiness;
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
