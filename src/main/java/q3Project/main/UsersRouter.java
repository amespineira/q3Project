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
	static final QueryBuilder Model= new QueryBuilder();
	public static String getUser(String idIn){
		Gson gson=new Gson();
    	Connection conn = null;
    	int id=Integer.parseInt(idIn);
		PreparedStatement stmt = null;
		PreparedStatement people_stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.prepareStatement(Model.getUserNoHash());
		  stmt.setInt(1, id);
		  ResultSet userRes = stmt.executeQuery();
		  User userOut= new User();
		  while(userRes.next()){
			  userOut.id=userRes.getInt("id");
			  userOut.username=userRes.getString("username");

		  }
		  stmt.close();
		  conn.close();
	
		  return gson.toJson(userOut);
		 
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
    public static String userData (String idIn){
    	Gson gson=new Gson();
    	Connection conn = null;
    	int id=Integer.parseInt(idIn);
		PreparedStatement stmt = null;
		PreparedStatement temp=null;
		PreparedStatement people_stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.prepareStatement(Model.getPeople());
		  stmt.setInt(1, id);
		  ResultSet people = stmt.executeQuery();
		  UserData userData=new UserData();
		  while(people.next()){
			 Person next= new Person(people.getString("id"), people.getString("first_name"), people.getString("last_name"), people.getString("user_id"), people.getString("place_id"));
			 people_stmt = conn.prepareStatement(Model.getNotes("id"));
			 people_stmt.setInt(1, people.getInt("id"));
			 ResultSet notes = people_stmt.executeQuery();
			 while(notes.next()){
				 Note note=new Note(notes.getString("id"), notes.getString("type"), notes.getString("text"));
				 next.addNote(note);
			 }
			 people_stmt = conn.prepareStatement(Model.getLinks("id"));
			 people_stmt.setInt(1, people.getInt("id"));
			 ResultSet links = people_stmt.executeQuery();
			 while(links.next()){
				 Link link=new Link(links.getString("id"), links.getString("name"), links.getString("url"));
				 next.addLink(link);
			 }
			 userData.addPerson(next);
		  }
		  temp = conn.prepareStatement(Model.getPlaces("id"));
		  temp.setInt(1, id);
		  ResultSet places = temp.executeQuery();
		  while(places.next()){
			  Place place=new Place(places.getString("id"), places.getString("name"));
			  userData.addPlace(place);
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
    
   
    
   
    

}
