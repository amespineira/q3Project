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
public class NoteRouter {
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
	static final QueryBuilder Model= new QueryBuilder();
	
	public static String newNote(Request req, String id){
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
				ResultSet new_note = new_stmt.executeQuery(QueryBuilder.createNote(text.getAsString(), type.getAsString(), id));
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
				ResultSet note = check_stmt.executeQuery(QueryBuilder.getNotes(req.params("note_id"), "id"));
				if(note.next()){
					up_stmt = conn.createStatement();
					ResultSet updated = up_stmt.executeQuery(QueryBuilder.updateNote(req.params("note_id"), text.getAsString(), type.getAsString()));
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
	   public static String deleteNote(Request req){
	    	Connection conn = null;
	    	Statement stmt = null;
			try{
				Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(DB_URL);
				stmt = conn.createStatement();
				ResultSet note = stmt.executeQuery(QueryBuilder.deleteNote(req.params("id")));
				while(note.next()){
					return "note deleted";
				}
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
		 return "deleteNote";
	    }
}

