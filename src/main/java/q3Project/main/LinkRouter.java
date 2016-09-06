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

public class LinkRouter {
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
	static final QueryBuilder Model= new QueryBuilder();
	
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
