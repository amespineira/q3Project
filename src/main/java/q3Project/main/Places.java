package q3Project.main;

public class Places {
    private String user_name;
    private Dataset[] places;
 
    public void setUserName(String user_name) {
        this.user_name = user_name;
    }
 
    public void setPlaces(Dataset[] places) {
        this.places = places;
    }
 
    public String userName() {
        return user_name;
    }
 
    public Dataset[] getPlaces() {
        return places;
    }
}
