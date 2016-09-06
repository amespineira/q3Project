package q3Project.main;
import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
import java.net.URLDecoder;

import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.UnsupportedEncodingException;

import org.eclipse.jetty.server.Response;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.mindrot.jbcrypt.BCrypt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.impl.crypto.MacProvider;
import java.security.Key;
import spark.Request;


public class Auth {

	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
    public static void main(String args[]){
    	// Hash a password for the first time
//    	String hashed = BCrypt.hashpw("password", BCrypt.gensalt());
		
    	// gensalt's log_rounds parameter determines the complexity
    	// the work factor is 2**log_rounds, and the default is 10
    	String hashed = BCrypt.hashpw("test", BCrypt.gensalt(12));
    	System.out.println(hashed);
    	// Check that an unencrypted password matches one that has
    	// previously been hashed
    	if (BCrypt.checkpw("test", hashed))
    		System.out.println("It matches");
    	else
    		System.out.println("It does not match");    	
    	
    }
    public static class QueryBuilder {        
        public static String createUser(String username, String password){
        	String sql;
            sql = "insert into users values(default, '" + username + "', '" + BCrypt.hashpw(password, BCrypt.gensalt(12))  + "') RETURNING id;";
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
    }
    
    public static String createUser (Request request, spark.Response response, Key key){
   	 Connection conn = null;
   	 Statement ue_stmt = null;
   	 Statement stmt = null;
   	System.out.println("************************");
	System.out.println(request.body());
	System.out.println("************************");
	
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
		  ResultSet results = ue_stmt.executeQuery(QueryBuilder.getUser(username.getAsString(), "username"));
		  if(results.next()){
			  String DBusername = results.getString("username");
			  System.out.print("Username taken" + DBusername);
			  return "Username taken";
		  } else {
			  stmt = conn.createStatement();
			  ResultSet rs = stmt.executeQuery(QueryBuilder.createUser(username.getAsString(), password.getAsString()));
			  System.out.print("Username created");
			  while(rs.next()){
				  String compactJws = Jwts.builder()
				    .setSubject(rs.getString("id"))
				    .signWith(SignatureAlgorithm.HS512, key)
				    .compact();
				  return compactJws;
			  } 
			  rs.close();
			  stmt.close();
		  }
		  results.close();
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
    
    public static String Login (Request request, spark.Response response, Key key){
    	Connection conn = null;
		Statement stmt = null;
		System.out.println("************************");
		System.out.println(request.body());
		System.out.println("************************");

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
		  ResultSet rs = stmt.executeQuery(QueryBuilder.getUser(inputUsername.getAsString(), "username"));
		  if(rs.next()){
		    String hash = rs.getString("password");
		    System.out.println(hash);
//		    System.out.println(inputPassword.getAsString()+" : is the thing");
		   
	    	if (BCrypt.checkpw(inputPassword.getAsString(), hash)){
	    	//generate token here, do whatever you need to to store it
			  String compactJws = Jwts.builder()
			    .setSubject(rs.getString("id"))
			    .signWith(SignatureAlgorithm.HS512, key)
			    .compact();
			  return compactJws;		    	
		     } 
	    	 else {
		    	 System.out.print("Not Found"); 
		    	 return "Incorrect Password";
		     }
		  }
		  else{
			 System.out.print("User Not Found");
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
		 return "User not found";
      }
    public static boolean checkToken(String compactJws, Key key){
    	try {

    	    Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws);
    	    return true;
    	    //OK, we can trust this JWT

    	} catch (SignatureException e) {
    		return false;
    	    //don't trust the JWT!
    	}
    }
    public static boolean checkToken(String compactJws, Key key, int id){
    	try {
    		System.out.println("printing this");
    		System.out.println(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject());
    	    if(id==Integer.parseInt(Jwts.parser().setSigningKey(key).parseClaimsJws(compactJws).getBody().getSubject()))
    	    {
    	    return true;
    	    //OK, we can trust this JWT
    	    }
    	    else{
    	    	return false;
    	    }
    	} catch (SignatureException e) {
    		System.out.println("sig exception happended");
    		return false;
    	    //don't trust the JWT!
    	}
    }    
    
}
