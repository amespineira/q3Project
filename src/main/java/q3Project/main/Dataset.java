package q3Project.main;

import java.util.HashMap;
import java.util.Map;
 
public class Dataset {
    private String place_id;
    private String place_name;
    private Dataset[] people;
 
    public String getPlaceId() {
        return place_id;
    }
 
    public void setPlaceId(String place_id) {
        this.place_id = place_id;
    }
 
    public String getPlaceName() {
        return place_name;
    }
 
    public void setPlaceName(String place_name) {
        this.place_name = place_name;
    }
    
    public Dataset[] getPeople() {
        return people;
    }
    
    public void setPeople(Dataset[] people) {
        this.people = people;
    }
}