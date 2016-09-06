package q3Project.main;
import java.sql.* ;
import java.math.* ;
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.eclipse.jetty.server.Response;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;


public class UsersRouter {
	
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
 
    public static class Model {        
        public static String createPlace(String user_id, String place_name){
        	String sql;
            sql = "insert into places values(default, " + user_id + ", '" + place_name  + "') RETURNING id;";
            return sql;
        }
        
        public static String deletePlace(String place_id){
        	String sql;
            sql = "delete from places where id=" + place_id + " returning id";
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
        
        public static String deletePerson(String person_id){
        	String sql;
            sql = "delete from people where id=" + person_id + " returning id";
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
    
    public static String userData (String id){
    	Gson gson=new Gson();
    	Connection conn = null;
		Statement stmt = null;
		Statement people_stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  ResultSet people = stmt.executeQuery(Model.getPeople(id));
		  UserData userData=new UserData();
		  while(people.next()){
			 Person next= new Person(people.getString("id"), people.getString("first_name"), people.getString("last_name"), people.getString("user_id"), people.getString("place_id"));
			 people_stmt = conn.createStatement();
			 ResultSet notes = people_stmt.executeQuery(Model.getNotes(people.getString("id"), "id"));
			 while(notes.next()){
				 Note note=new Note(notes.getString("id"), notes.getString("type"), notes.getString("text"));
				 next.addNote(note);
			 }
			 people_stmt = conn.createStatement();
			 ResultSet links = people_stmt.executeQuery(Model.getLinks(people.getString("id"), "id"));
			 while(links.next()){
				 Link link=new Link(links.getString("id"), links.getString("name"), links.getString("url"));
				 next.addLink(link);
			 }
			 userData.addPerson(next);
		  }
		  stmt = conn.createStatement();
		  ResultSet places = stmt.executeQuery(Model.getPlaces(id, id, "id"));
		  while(places.next()){
			  Place place=new Place(places.getString("id"), places.getString("name"));
			  userData.addPlace(place);
		  }
		  for(int i=0; i<userData.people.size(); i++){
			  System.out.println(userData.people.get(i).first_name);
			  for(int j=0; j<userData.people.get(i).notes.size(); j++){
				  System.out.println(userData.people.get(i).notes.get(j).text);
			  }
		  }
		  people.close();
		  stmt.close();
		  conn.close();
		  return gson.toJson(userData);
		 
		 }
		  catch(SQLException se){
				  se.printStackTrace();
		   }catch(Exception e){
				  e.printStackTrace();
		   }finally{
				  try{
				     if(stmt!=null)
				        stmt.close();
				  }catch(SQLException se2){
				  }
				  try{
				     if(conn!=null)
				        conn.close();
				  }catch(SQLException se){
				     se.printStackTrace();
				  }
		   }
		 return "JsonData";
      }
    
    public static String newPlace(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement new_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement place_name = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  place_name = jsonObject.get("name");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet place = check_stmt.executeQuery(Model.getPlaces(place_name.getAsString(), req.params("id"), "name"));
			if(!place.next()){
				new_stmt = conn.createStatement();
				ResultSet new_place = new_stmt.executeQuery(Model.createPlace(req.params("id"), place_name.getAsString()));
				while(new_place.next()){
					check_stmt.close();
					new_stmt.close();
					conn.close();
					return "place created";
				}
			}else{
				return "place already exists";
			}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(check_stmt!=null)
			    	 check_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "newPlace";
    }
    
    public static String newPerson(Request req){
    	Connection conn = null;
		Statement new_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement first = null;
		  JsonElement last = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  first = jsonObject.get("first_name");
			  last = jsonObject.get("last_name");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
				new_stmt = conn.createStatement();
				ResultSet new_person = new_stmt.executeQuery(Model.createPerson(req.params("user_id"), req.params("place_id"), first.getAsString(), last.getAsString()));
				while(new_person.next()){
					new_stmt.close();
					conn.close();
					return "person created";
				}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(new_stmt!=null)
			    	 new_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "newPerson";
    }
    
    public static String newNote(Request req){
    	Connection conn = null;
		Statement new_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement text = null;
		  JsonElement type = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  text = jsonObject.get("text");
			  type = jsonObject.get("type");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
				new_stmt = conn.createStatement();
				ResultSet new_note = new_stmt.executeQuery(Model.createNote(text.getAsString(), type.getAsString(), req.params("person_id")));
				while(new_note.next()){
					new_stmt.close();
					conn.close();
					return "note created";
				}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(new_stmt!=null)
			    	 new_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "newNote";
    }
    
    public static String newLink(Request req){
    	Connection conn = null;
		Statement new_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement link_name = null;
		  JsonElement url = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  link_name = jsonObject.get("link_name");
			  url = jsonObject.get("url");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
				new_stmt = conn.createStatement();
				ResultSet new_link = new_stmt.executeQuery(Model.createLink(link_name.getAsString(), url.getAsString(), req.params("person_id")));
				while(new_link.next()){
					new_stmt.close();
					conn.close();
					return "link created";
				}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(new_stmt!=null)
			    	 new_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "newLink";
    }
    
    public static String updatePlace(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement up_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement place_name = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  place_name = jsonObject.get("name");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet place = check_stmt.executeQuery(Model.getPlaces(req.params("place_id"), req.params("id"), "id"));
			if(place.next()){
				up_stmt = conn.createStatement();
				ResultSet updated = up_stmt.executeQuery(Model.updatePlace(req.params("place_id"), place_name.getAsString()));
				while(updated.next()){
					up_stmt.close();
					up_stmt.close();
					conn.close();
					return "place updated";
				}
			}else{
				return "place doesn't exist";
			}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(check_stmt!=null)
			    	 check_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "updatedPlace";
    }
    
    public static String updatePerson(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement up_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement first = null;
		  JsonElement last = null;
		  JsonElement place_id = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  place_id = jsonObject.get("place_id");
			  first = jsonObject.get("first_name");
			  last = jsonObject.get("last_name");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet person = check_stmt.executeQuery(Model.getPeople(req.params("user_id")));
			if(person.next()){
				up_stmt = conn.createStatement();
				ResultSet updated = up_stmt.executeQuery(Model.updatePerson(req.params("person_id"), place_id.getAsString(), first.getAsString(), last.getAsString()));
				while(updated.next()){
					check_stmt.close();
					up_stmt.close();
					conn.close();
					return "person updated";
				}
			}else{
				return "person doesn't exist";
			}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(check_stmt!=null)
			    	 check_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "updatePerson";
    }
    
    public static String updateNote(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement up_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement text = null;
		  JsonElement type = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  text = jsonObject.get("text");
			  type = jsonObject.get("type");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet note = check_stmt.executeQuery(Model.getNotes(req.params("note_id"), "id"));
			if(note.next()){
				up_stmt = conn.createStatement();
				ResultSet updated = up_stmt.executeQuery(Model.updateNote(req.params("note_id"), text.getAsString(), type.getAsString()));
				while(updated.next()){
					check_stmt.close();
					up_stmt.close();
					conn.close();
					return "note updated";
				}
			}else{
				return "note doesn't exist";
			}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(check_stmt!=null)
			    	 check_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "updateNote";
    }
    
    public static String updateLink(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement up_stmt = null;
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement link_name = null;
		  JsonElement url = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  link_name = jsonObject.get("link_name");
			  url = jsonObject.get("url");
		  }
		  
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet link = check_stmt.executeQuery(Model.getLinks(req.params("link_id"), "id"));
			if(link.next()){
				up_stmt = conn.createStatement();
				ResultSet updated = up_stmt.executeQuery(Model.updateLink(req.params("link_id"), link_name.getAsString(), url.getAsString()));
				while(updated.next()){
					check_stmt.close();
					up_stmt.close();
					conn.close();
					return "link updated";
				}
			}else{
				return "link doesn't exist";
			}
		}
		catch(SQLException se){
			  se.printStackTrace();
	   }catch(Exception e){
			  e.printStackTrace();
	   }finally{
			  try{
			     if(check_stmt!=null)
			    	 check_stmt.close();
			  }catch(SQLException se2){
			  }
			  try{
			     if(conn!=null)
			        conn.close();
			  }catch(SQLException se){
			     se.printStackTrace();
			  }
	   }
	 return "updateLink";
    }
    
}
