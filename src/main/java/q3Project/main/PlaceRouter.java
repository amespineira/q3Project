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


public class PlaceRouter {
	
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
	static final QueryBuilder Model= new QueryBuilder();
	
	public static String updatePlace(Request req, String id){
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
			ResultSet place = check_stmt.executeQuery(Model.getPlaces(req.params("place_id"), id, "id"));
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

    public static String newPlace(Request req, String id){
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
			ResultSet place = check_stmt.executeQuery(Model.getPlaces(place_name.getAsString(), id, "name"));
			if(!place.next()){
				new_stmt = conn.createStatement();
				ResultSet new_place = new_stmt.executeQuery(Model.createPlace(id, place_name.getAsString()));
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

    public static String deletePlace(Request req, String id){
    	Connection conn = null;
    	Statement check_stmt = null;
		Statement people_stmt = null;
		Statement note_stmt= null;
		Statement link_stmt= null;
		Statement delete_stmt= null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.createStatement();
			ResultSet place = check_stmt.executeQuery("SELECT * FROM places WHERE id= "+req.params("place_id"));
			while(place.next()){
				System.out.println(place.getFetchSize());
				System.out.println("printing id of place");
				System.out.println(place.getString("user_id"));
				System.out.println("pringing id from token");
				System.out.println(id);
				if(Integer.parseInt(place.getString("user_id"))==Integer.parseInt(id)){
					System.out.println("here");
					people_stmt = conn.createStatement();
					ResultSet people = check_stmt.executeQuery(Model.selectPeopleFromPlace(req.params("place_id")));
					while(people.next()){
						note_stmt= conn.createStatement();
						note_stmt.executeQuery(Model.deleteNotesFromPeople(people.getString("id")));
						link_stmt= conn.createStatement();
						link_stmt.executeQuery(Model.deleteLinksFromPeople(people.getString("id")));
						people_stmt = conn.createStatement();
						people_stmt.executeQuery(Model.deletePerson(people.getString("id")));
					}
					delete_stmt = conn.createStatement();
					delete_stmt.executeQuery(Model.deletePlace(req.params("place_id")));
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