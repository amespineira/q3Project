package q3Project.main;

public class People {
    private String people_id;
    private String first_name;
    private String last_name;
    private Dataset[] notes;
    private Dataset[] links;
 
    public String getPeopleId() {
        return people_id;
    }
 
    public void setPeopleId(String people_id) {
        this.people_id = people_id;
    }
 
    public String getPeopleFName() {
        return first_name;
    }
 
    public void setFName(String first_name) {
        this.first_name = first_name;
    }
    
    public String getPeopleLName() {
        return last_name;
    }
 
    public void setLName(String last_name) {
        this.last_name = last_name;
    }
    
    public Dataset[] getNotes() {
        return notes;
    }
    
    public void setNotes(Dataset[] notes) {
        this.notes = notes;
    }
    public Dataset[] getLinks() {
        return links;
    }
    
    public void setLinks(Dataset[] links) {
        this.notes = links;
    }
}
