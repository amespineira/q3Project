package q3Project.main;
import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
import static spark.Spark.get;
import static spark.Spark.post;

import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import q3Project.main.App.Model;


public class Users {
	
	
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
    interface Validable {
        boolean isValid();
    }
    private static final int HTTP_BAD_REQUEST = 400;
    public static class Model {        
        public String createUser(String name, String email){
        	String sql;
            sql = "insert into users values(default, '" + name + "', '" + email  + "') RETURNING id;";
            return sql;
        }
        public String createPlace(int user_id, String name){
        	String sql;
            sql = "insert into places values(default, " + user_id + ", '" + name  + "')";
            return sql;
        }
        public String createPass(int user_id, String hash){
        	String sql;
            sql = "insert into passwords values(default, " + user_id + ", '" + hash  + "')";
            return sql;
        }
        public String getAllUsers(JsonElement email){
        	String sql;
            sql = "select * from users where email='" + email + "'";
            return sql;
        }
        public String getPass(int user_id){
        	String sql;
            sql = "select * from passwords where user_id='" + user_id + "'";
            return sql;
        }
        
    }
    public String getAll (){
    	 Connection conn = null;
		 Statement stmt = null;
		 try{
		   Model model = new Model();
		  Class.forName("org.postgresql.Driver");
		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  ResultSet rs = stmt.executeQuery("SELECT * FROM test");
		  while(rs.next()){
		     //Retrieve by column name
		    System.out.println(rs.getString(1));
		    return rs.getString(2);
		    
		  }
		  rs.close();
		  stmt.close();
		  conn.close();
		 }
   
		  catch(SQLException se){
				  //Handle errors for JDBC
				  se.printStackTrace();
		   }catch(Exception e){
				  //Handle errors for Class.forName
				  e.printStackTrace();
		   }finally{
				  //finally block used to close resources
				  try{
				     if(stmt!=null)
				        stmt.close();
				  }catch(SQLException se2){
				  }// nothing we can do
				  try{
				     if(conn!=null)
				        conn.close();
				  }catch(SQLException se){
				     se.printStackTrace();
				  }//end finally try
		   }
		 return "words";
    }
}
