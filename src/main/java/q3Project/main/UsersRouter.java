package q3Project.main;
import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
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
    interface Validable {
        boolean isValid();
    }
    public static class Model {        
        public static String createUser(String username, String password){
        	String sql;
            sql = "insert into users values(default, '" + username + "', '" + password  + "') RETURNING id;";
            return sql;
        }
        public static String createPlace(int user_id, String name){
        	String sql;
            sql = "insert into places values(default, " + user_id + ", '" + name  + "')";
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
        public static String getPeople(String id, String type){
        	String sql;
        	if (type.equals("user_id")){
        		sql = "select * from places where user_id='"+ id + "'";
        	} else {
        		sql = "select * from places where place_id='"+ id + "'";;	
        	}
            return sql;
        }
    }
    
    public static String createUser (Request request, spark.Response response){
   	 Connection conn = null;
   	 Statement ue_stmt = null;
   	 Statement stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  ue_stmt = conn.createStatement();
		  JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(request.body());
		  JsonElement username = null;
		  JsonElement password = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  username = jsonObject.get("username");
			  password = jsonObject.get("password");
		  }
		  ResultSet ue_rs = ue_stmt.executeQuery(Model.getUser(username.getAsString(), "username"));
		  if(ue_rs.next()){
			  String DBusername = ue_rs.getString("username");
			  System.out.print("Username taken" + DBusername);
			  return "Username taken";
		  } else {
			  stmt = conn.createStatement();
			  ResultSet rs = stmt.executeQuery(Model.createUser(username.getAsString(), password.getAsString()));
			  System.out.print("Username created");
			  while(ue_rs.next()){
				  return rs.getString("username") + " created";
			  } 
			  rs.close();
			  stmt.close();
		  }
		  ue_rs.close();
		  ue_stmt.close();
		  conn.close();
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
		 return "users";
   }
    
    public static String Verify (Request request, spark.Response response){
    	Connection conn = null;
		Statement stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  JsonParser parser = new JsonParser();
		   JsonElement jsonTree = parser.parse(request.body());
		   JsonElement inputUsername = null;
		   JsonElement inputPassword = null;
		   if(jsonTree.isJsonObject()) {
			    JsonObject jsonObject = jsonTree.getAsJsonObject();
			    inputUsername = jsonObject.get("username");
			    inputPassword = jsonObject.get("password");
			}
		  ResultSet rs = stmt.executeQuery(Model.getUser(inputUsername.getAsString(), "username"));
		  if(rs.next()){
		     String password = rs.getString("password");
		     if (password.equals(inputPassword.getAsString())){
		    	 System.out.print("Authorized");
		    	 return "Authorized";
		     } else {
		    	 System.out.print("Not Found"); 
		    	 return "Not Found";
		     }
		  }
		  else{
			 System.out.print("Not Found");
		  }
		  rs.close();
		  stmt.close();
		  conn.close();
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
		 return "authorized?";
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
		 return "authorized?";
      }
    
}