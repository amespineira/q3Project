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
	static final String DB_URL = System.getenv("JDBC_DATABASE_URL");
	static final QueryBuilder Model= new QueryBuilder();
	
	public static String updatePlace(Request req, String id){
    	Connection conn = null;
    	PreparedStatement check_stmt = null;
		PreparedStatement up_stmt = null;
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
			check_stmt = conn.prepareStatement(Model.getPlaces("place_id"));
			check_stmt.setInt(1, Integer.parseInt(req.params("place_id")));
			ResultSet place = check_stmt.executeQuery();
			if(place.next()){
				up_stmt = conn.prepareStatement(Model.updatePlace());
				up_stmt.setString(1, place_name.getAsString());
				up_stmt.setInt(2, Integer.parseInt(req.params("place_id")));
				ResultSet updated = up_stmt.executeQuery();
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

    public static String newPlace(Request req, String idIn){
    	Connection conn = null;
    	PreparedStatement check_stmt = null;
		PreparedStatement new_stmt = null;
		int id=Integer.parseInt(idIn);
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
			check_stmt = conn.prepareStatement(Model.getPlaces("name"));
			check_stmt.setString(1, place_name.getAsString());
			check_stmt.setInt(2, id);

			ResultSet place = check_stmt.executeQuery();
			if(!place.next()){
				new_stmt = conn.prepareStatement(Model.createPlace());
				new_stmt.setInt(1, id);
				new_stmt.setString(2, place_name.getAsString());

				ResultSet new_place = new_stmt.executeQuery();
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
    	PreparedStatement check_stmt = null;
    	PreparedStatement people_stmt = null;
		PreparedStatement note_stmt= null;
		PreparedStatement link_stmt= null;
		PreparedStatement delete_stmt= null;
		try{
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(DB_URL);
			check_stmt = conn.prepareStatement("SELECT * FROM places WHERE id= ?");
			check_stmt.setInt(1, Integer.parseInt(req.params("place_id")));
			ResultSet place = check_stmt.executeQuery();
			while(place.next()){
				if(Integer.parseInt(place.getString("user_id"))==Integer.parseInt(id)){
					check_stmt= conn.prepareStatement(Model.selectPeopleFromPlace());
					check_stmt.setInt(1, Integer.parseInt(req.params("place_id")));
					ResultSet people = check_stmt.executeQuery();
					while(people.next()){
						note_stmt= conn.prepareStatement(Model.deleteNotesFromPeople());
						note_stmt.setInt(1, people.getInt("id"));
						note_stmt.executeQuery();
						link_stmt= conn.prepareStatement(Model.deleteLinksFromPeople());
						link_stmt.setInt(1, people.getInt("id"));
						link_stmt.executeQuery();
						people_stmt = conn.prepareStatement(Model.deletePerson());
						people_stmt.setInt(1, people.getInt("id"));
						people_stmt.executeQuery();
					}
					delete_stmt = conn.prepareStatement(Model.deletePlace());
					delete_stmt.setInt(1, Integer.parseInt(req.params("place_id")));
					delete_stmt.executeQuery();
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