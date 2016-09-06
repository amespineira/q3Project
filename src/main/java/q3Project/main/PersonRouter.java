package q3Project.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;

public class PersonRouter {
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
	static final QueryBuilder Model= new QueryBuilder();
	
    public static String newPerson(Request req, String userId){
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
				ResultSet new_person = new_stmt.executeQuery(Model.createPerson(userId, req.params("place_id"), first.getAsString(), last.getAsString()));
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
    public static String updatePerson(Request req, String userId){
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
			ResultSet person = check_stmt.executeQuery(Model.getPeople(userId));
			if(person.next()){
				up_stmt = conn.createStatement();
				ResultSet updated = up_stmt.executeQuery(Model.updatePerson(userId, place_id.getAsString(), first.getAsString(), last.getAsString()));
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
    
    
}
