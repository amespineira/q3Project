package q3Project.main;

import java.util.ArrayList;

public class Person {
    public String people_id;
    public String first_name;
    public String last_name;
    public String user_id;
    public String place_id;
    public ArrayList<Note> notes;
    public ArrayList<Link> links;
    public Person (String id, String first, String last, String user, String place){
    	this.people_id=id;
    	this.first_name=first;
    	this.last_name=last;
    	this.user_id=user;
    	this.place_id=place;
    	this.notes=new ArrayList<Note>();
    	this.links=new ArrayList<Link>();
    }
    public void addNote(Note note){
    	this.notes.add(note);
    }
    public void addLink(Link link){
    	this.links.add(link);
    }
}
