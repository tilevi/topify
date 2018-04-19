package applicationname.companydomain.simpleapp;

public class CategoryItem {

    private String title;
    private String timeRange;

    public CategoryItem(String title, String timeRange) {
        this.title = title;
        this.timeRange = timeRange;
    }

    public String getTitle() {
        return title;
    }
    public String getTimeRange() {
        return timeRange;
    }
}