package q3Project.main;

import java.sql.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;

public class LinkRouter {
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = System.getenv("JDBC_DATABASE_URL");
	static final QueryBuilder Model= new QueryBuilder();
	
    public static String newLink(Request req){
    	Connection conn = null;
		PreparedStatement new_stmt = null;
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
				new_stmt = conn.prepareStatement(Model.createLink());
				new_stmt.setString(1, link_name.getAsString());
				new_stmt.setString(2, url.getAsString());
				new_stmt.setInt(3,  Integer.parseInt(req.params("person_id")));

				ResultSet new_link = new_stmt.executeQuery();
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
    
    public static String updateLink(Request req){
    	Connection conn = null;
    	PreparedStatement check_stmt = null;
    	PreparedStatement up_stmt = null;
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
			check_stmt = conn.prepareStatement(Model.getLinks("id"));
			check_stmt.setInt(1, Integer.parseInt(req.params("link_id")));
			ResultSet link = check_stmt.executeQuery();
			if(link.next()){
				up_stmt = conn.prepareStatement(Model.updateLink());
				up_stmt.setString(1, link_name.getAsString());
				up_stmt.setString(2, url.getAsString());
				up_stmt.setInt(3, Integer.parseInt(req.params("link_id"))); 
				ResultSet updated = up_stmt.executeQuery();
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
	   public static String deleteLink(Request req){
	    	Connection conn = null;
	    	PreparedStatement stmt = null;
			try{
				Class.forName("org.postgresql.Driver");
				conn = DriverManager.getConnection(DB_URL);
				stmt = conn.prepareStatement(QueryBuilder.deleteLink());
				stmt.setInt(1, Integer.parseInt(req.params("id")));
				ResultSet link = stmt.executeQuery();
				while(link.next()){
					return "link deleted";
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
		 return "deleteLink";
	    }
}
