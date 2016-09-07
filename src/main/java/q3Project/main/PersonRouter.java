package q3Project.main;

import java.sql.*;

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
		PreparedStatement new_stmt = null;
		int id=Integer.parseInt(userId);
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
				new_stmt = conn.prepareStatement(Model.createPerson());
				new_stmt.setString(1, first.getAsString());
				new_stmt.setString(2, last.getAsString());
				new_stmt.setInt(3, id);
				new_stmt.setInt(4, Integer.parseInt(req.params("place_id")));
				ResultSet new_person = new_stmt.executeQuery();
				while(new_person.next()){
					return new_person.getString("id");
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
    	PreparedStatement check_stmt = null;
		PreparedStatement up_stmt = null;
		int id=Integer.parseInt(userId);
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
			check_stmt = conn.prepareStatement(Model.getPeople());
			check_stmt.setInt(1, id);
			ResultSet person = check_stmt.executeQuery();
			if(person.next()){
				up_stmt = conn.prepareStatement(Model.updatePerson());
				up_stmt.setInt(1, id);
				up_stmt.setInt(2, place_id.getAsInt());
				up_stmt.setString(3, first.getAsString());
				up_stmt.setString(4, last.getAsString());
				
				ResultSet updated = up_stmt.executeQuery();
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
    public static String deletePerson(Request req, String id){
    	Connection conn = null;
    	PreparedStatement check_stmt = null;
    	PreparedStatement people_stmt = null;
    	PreparedStatement note_stmt= null;
    	PreparedStatement link_stmt= null;
    	PreparedStatement delete_stmt= null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.prepareStatement("SELECT * FROM people WHERE id= ?");
			check_stmt.setInt(1, Integer.parseInt(req.params("person_id")));
			ResultSet person = check_stmt.executeQuery();
			while(person.next()){
				System.out.println(person.getFetchSize());
				System.out.println("printing id of person");
				System.out.println(person.getString("user_id"));
				System.out.println("pringing id from token");
				System.out.println(id);
				if(Integer.parseInt(person.getString("user_id"))==Integer.parseInt(id)){
					note_stmt= conn.prepareStatement(Model.deleteNotesFromPeople());
					note_stmt.setInt(1, person.getInt("id"));
					note_stmt.executeQuery();
					link_stmt= conn.prepareStatement(Model.deleteLinksFromPeople());
					link_stmt.setInt(1, person.getInt("id"));
					link_stmt.executeQuery();
					people_stmt = conn.prepareStatement(Model.deletePerson());
					people_stmt.setInt(1, (person.getInt("id")));
					people_stmt.executeQuery();
					return "deleted";
				}
				else{
					return "incorrect user id";
				}
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
	 return "end of delete";
    }
    
}
