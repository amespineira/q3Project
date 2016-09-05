package q3Project.main;

public class Links {
	private String link_id;
    private String name;
    private String url;
    
    public void setId(String link_id) {
        this.link_id = link_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setURl(String url) {
        this.url = url;
    }
    public String getLinkName() {
        return name;
    }
    public String getLinkId() {
        return link_id;
    }
    public String getLinkUrl() {
        return url;
    }
}
