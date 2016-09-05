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


public class Users {
	
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
    	Connection conn = null;
		Statement stmt = null;
		Statement people_stmt = null;
		 try{
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery(Model.getPlaces(id));
		  while(rs.next()){
			  String place_name = rs.getString("name");
			  System.out.print(place_name);
			  people_stmt = conn.createStatement();
			  ResultSet people_rs = stmt.executeQuery(Model.getPeople(id, "place_id"));
			  while(people_rs.next()){
				  String people_fname = rs.getString("first_name");
				  String people_lname = rs.getString("last_name");
			  }
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
    
}
