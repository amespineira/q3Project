package q3Project.main;

public class QueryBuilder {
      
        public static String createPlace(String user_id, String place_name){
        	String sql;
            sql = "insert into places values(default, " + user_id + ", '" + place_name  + "') RETURNING id;";
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
        public static String deletePeopleFromPlace(String place_id) {
        	String sql;
            sql = "delete from people where person_id="+place_id;
			return sql;
        } 
        public static String deletePlace(String place_id){
        	String sql;
            sql = "delete from places where id=" + place_id + " returning id";
            return sql;
        }
        public static String deleteNotesFromPeople(String person_id) {
        	String sql;
            sql = "delete from notes where person_id="+person_id;
			return sql;
        }
        public static String deleteLinksFromPeople(String person_id) {
        	String sql;
            sql = "delete from links where person_id="+person_id;
			return sql;
        }
        public static String deletePerson(String person_id) {
        	String sql;
            sql = "delete from people where id="+person_id;
			return sql;
        }
        
        public static String updatePlace(String id, String new_place_name){
        	String sql;
            sql = "update places set name ='" + new_place_name + "' where id=" + id + " RETURNING id;";
            return sql;
        }
        
        public static String createPerson(String user_id, String place_id, String first, String last){
        	String sql;
            sql = "insert into people values(default,'" + first + "','"+ last + "'," + user_id + ","+ place_id+") RETURNING id;";
            return sql;
        }
        
        public static String updatePerson(String id, String place_id, String first, String last){
        	String sql;
            sql = "update people set place_id =" + place_id + ", first_name ='" + first + "', last_name='" + last  +"' where id=" + id + " RETURNING id;";
            return sql;
        }
        
        public static String createNote(String text, String type, String person_id){
        	String sql;
            sql = "insert into notes values(default,'" + text + "','"+ type + "'," + person_id + ") RETURNING id;";
            return sql;
        }
        
        public static String deleteNote(String note_id){
        	String sql;
            sql = "delete from notes where id=" + note_id + " returning id";
            return sql;
        }
        
        public static String updateNote(String id, String text, String type){
        	String sql;
            sql = "update notes set text='"+text+"', type='" +type+ "' where id=" + id + " RETURNING id;";
            return sql;
        }
        
        public static String createLink(String name,  String url, String person_id){
        	String sql;
            sql = "insert into links values(default,'" + name + "','"+ url + "'," + person_id +") RETURNING id;";
            return sql;
        }
        
        public static String deleteLink(String link_id){
        	String sql;
            sql = "delete from links where id=" + link_id + " returning id";
            return sql;
        }
        
        public static String updateLink(String id, String link_name, String url){
        	String sql;
            sql = "update links set name='"+link_name+"', url='" +url+ "' where id=" + id + " RETURNING id;";
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
        
        public static String getPeople(String id){
        	return "select * from people where user_id="+id;
        }
        
        public static String getNotes(String input, String type){
        	String sql;
        	if (!type.equals("text")){
        		sql = "select * from notes where person_id="+input;
        	} else {
        		sql = "select * from notes where text='"+input+"'";	
        	}
			return sql;
        }
        
        public static String getLinks(String input, String type){
        	String sql;
        	if (!type.equals("name")){
        		sql = "select * from links where person_id="+input;
        	} else {
        		sql = "select * from links where name='"+input+"'";
        	}
        	return sql;
        }
        
        public static String getPlaces(String input, String user_id, String type){
        	String sql;
        	if (!type.equals("name")){
        		sql = "select * from places where user_id='"+ input + "'";
        	} else {
        		sql = "select * from places where name='"+ input + "' and user_id="+user_id;
        	}
            return sql;
        }
    
}
