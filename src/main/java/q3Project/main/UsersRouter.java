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
        public static String createPerson(String user_id, String place_id, String first, String last){
        	String sql;
            sql = "insert into people values(default,'" + first + "','"+ last + "'," + user_id + ","+ place_id+") RETURNING id;";
            return sql;
        }
        public static String createNote(String text, String type, String person_id){
        	String sql;
            sql = "insert into notes values(default,'" + text + "','"+ type + "'," + person_id + ") RETURNING id;";
            System.out.print(sql);
            return sql;
        }
        public static String createLink(String name,  String url, String person_id){
        	String sql;
            sql = "insert into links values(default,'" + name + "','"+ url + "'," + person_id +") RETURNING id;";
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
        public static String getNotes(String id){
        	return "select * from notes where person_id="+id;
        }
        public static String getLinks(String id){
        	return "select * from links where person_id="+id;
        }
        public static String getPlaces(String user_id){
        	String sql;
            sql = "select * from places where user_id='"+ user_id + "'";;
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
			 ResultSet notes = people_stmt.executeQuery(Model.getNotes(people.getString("id")));
			 while(notes.next()){
				 Note note=new Note(notes.getString("id"), notes.getString("type"), notes.getString("text"));
				 next.addNote(note);
			 }
			 people_stmt = conn.createStatement();
			 ResultSet links = people_stmt.executeQuery(Model.getLinks(people.getString("id")));
			 while(links.next()){
				 Link link=new Link(links.getString("id"), links.getString("name"), links.getString("url"));
				 next.addLink(link);
			 }
			 userData.addPerson(next);
		  }
		  stmt = conn.createStatement();
		  ResultSet places = stmt.executeQuery(Model.getPlaces(id));
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
		 return "jsonData";
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
			ResultSet place = check_stmt.executeQuery(Model.getPlaces(req.params("id")));
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
				return "note already exists";
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
    	Statement check_stmt = null;
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
			check_stmt = conn.createStatement();
			ResultSet person = check_stmt.executeQuery(Model.getPeople(req.params("user_id")));
			if(!person.next()){
				new_stmt = conn.createStatement();
				ResultSet new_person = new_stmt.executeQuery(Model.createPerson(req.params("user_id"), req.params("place_id"), first.getAsString(), last.getAsString()));
				while(new_person.next()){
					check_stmt.close();
					new_stmt.close();
					conn.close();
					return "person created";
				}
			}else{
				return "person already exists";
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
	 return "newPerson";
    }
    
    public static String newNote(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
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
			check_stmt = conn.createStatement();
			ResultSet note = check_stmt.executeQuery(Model.getNotes(req.params("person_id")));
			if(!note.next()){
				new_stmt = conn.createStatement();
				ResultSet new_note = new_stmt.executeQuery(Model.createNote(text.getAsString(), type.getAsString(), req.params("person_id")));
				while(new_note.next()){
					check_stmt.close();
					new_stmt.close();
					conn.close();
					return "note created";
				}
			}else{
				return "note already exists";
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
	 return "newNote";
    }
    public static String newLink(Request req){
    	Connection conn = null;
    	Statement check_stmt = null;
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
			check_stmt = conn.createStatement();
			ResultSet link = check_stmt.executeQuery(Model.getLinks(req.params("person_id")));
			if(!link.next()){
				new_stmt = conn.createStatement();
				ResultSet new_link = new_stmt.executeQuery(Model.createLink(link_name.getAsString(), url.getAsString(), req.params("person_id")));
				while(new_link.next()){
					check_stmt.close();
					new_stmt.close();
					conn.close();
					return "link created";
				}
			}else{
				return "link already exists";
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
	 return "newLink";
    }
}
