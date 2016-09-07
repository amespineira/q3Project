package q3Project.main;

public class QueryBuilder {
      
        public static String createPlace(){
        	String sql;
            sql = "insert into places values(default, ?,  ? ) RETURNING id;";
            return sql;
        }
        public static String deleteNotesFromPlace(String place_id) {
        	String sql;
            sql = "delete from notes where id=(select notes.id from notes join people on notes.people_id = people.id where people.place_id=" + place_id + ")";
			return sql;
        }  
        public static String deleteLinksFromPlace(String place_id) {
        	String sql;
        	
            sql = "delete from links where id=(select links.id from links join people on links.people_id = people.id where people.place_id=" + place_id + ")";
			return sql;
        } 
        public static String selectPeopleFromPlace(){
        	return "SELECT people.id FROM places JOIN people ON people.place_id=places.id WHERE places.id= ?";
        }
        public static String deletePeopleFromPlace(String place_id) {
        	String sql;
            sql = "delete from people where person_id="+place_id+" returning id";
			return sql;
        } 
        public static String deletePlace(){
        	String sql;
            sql = "delete from places where id= ? returning id";
            return sql;
        }
        public static String deleteNotesFromPeople() {
        	String sql;
            sql = "delete from notes where person_id= ? returning id";
			return sql;
        }
        public static String deleteLinksFromPeople() {
        	String sql;
            sql = "delete from links where person_id= ? returning id";
			return sql;
        }
        public static String deletePerson() {
        	String sql;
            sql = "delete from people where id= ? returning id";
			return sql;
        }
        
        public static String updatePlace(){
        	String sql;
            sql = "update places set name = ? where id= ? RETURNING id;";
            return sql;
        }
        
        public static String createPerson(){
        	String sql;
            sql = "insert into people values(default, ? , ?, ? , ?) RETURNING id;";
            return sql;
        }
        
        public static String updatePerson(){
        	String sql;
            sql = "update people set place_id = ? , first_name = ? , last_name= ? where id= ? RETURNING id;";
            return sql;
        }
        
        public static String createNote(){
        	String sql;
            sql = "insert into notes values(default, ? , ? , ?) RETURNING id;";
            return sql;
        }
        
        public static String deleteNote(){
        	String sql;
            sql = "delete from notes where id= ? returning id";
            return sql;
        }
        
        public static String updateNote(){
        	String sql;
            sql = "update notes set text= ?, type= ? where id= ? RETURNING id;";
            return sql;
        }
        
        public static String createLink(){
        	String sql;
            sql = "insert into links values(default, ? , ? , ?) RETURNING id;";
            return sql;
        }
        
        public static String deleteLink(){
        	String sql;
            sql = "delete from links where id= ? returning id";
            return sql;
        }
        
        public static String updateLink(){
        	String sql;
            sql = "update links set name= ?, url= ? where id= ? RETURNING id;";
            return sql;
        }
        
        public static String getUser(String input, String type){
        	String sql;
        	if (type.equals("username")){
        		sql = "select * from users where username='" + input + "'";
        	} else {
        		sql = "select * from users where id='" + input + "'";
        	}
            return sql;
        }
        public static String getUserNoHash(){
        	return "SELECT id, username FROM users WHERE id= ?";
        }
        public static String getPeople(){
        	return "select * from people where user_id= ?";
        }
        
        public static String getNotes(String type){
        	String sql;
        	if (!type.equals("text")){
        		sql = "select * from notes where person_id= ?";
        	} else {
        		sql = "select * from notes where text= ?";	
        	}
			return sql;
        }
        
        public static String getLinks(String type){
        	String sql;
        	if (!type.equals("name")){
        		sql = "select * from links where person_id= ?";
        	} else {
        		sql = "select * from links where name= ?";
        	}
        	return sql;
        }
        
        public static String getPlaces(String type){
        	String sql;
        	if (!type.equals("name")){
        		sql = "select * from places where user_id= ?";
        	} else {
        		sql = "select * from places where name= ? and user_id= ? ";
        	}
            return sql;
        }
    
}
