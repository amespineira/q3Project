package q3Project.main;

import java.util.ArrayList;

public class UserData {
	public ArrayList<Person> people;
	public ArrayList<Place> places;
	public UserData(){
		this.people=new ArrayList<Person>();
		this.places=new ArrayList<Place>();

	}
	public void addPerson(Person person){
		this.people.add(person);
	}
	public void addPlace(Place place){
		this.places.add(place);
	}

}

