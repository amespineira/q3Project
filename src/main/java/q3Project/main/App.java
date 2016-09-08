package q3Project.main;

import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
import java.net.URISyntaxException;
import java.security.Key;

import static spark.Spark.get;
import static spark.Spark.post;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import org.flywaydb.core.Flyway;

import q3Project.main.UsersRouter;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import io.jsonwebtoken.impl.crypto.MacProvider;
import spark.Route;
import spark.Spark;
import spark.Filter;
import spark.Request;
import spark.Response;
//import app.NewPlaces;


public class App {
   // JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
//	static final String DB_URL = System.getenv("JDBC_DATABASE_URL");
    interface Validable {
        boolean isValid();
    }
    private static final int HTTP_BAD_REQUEST = 400;

   public static void main(String[] args) {
	   // Create the Flyway instance
       Flyway flyway = new Flyway();
       
       // Point it to the database
       flyway.setDataSource(DB_URL, null, null);
       
       // Start the migration
       flyway.migrate();
       
       Key key = MacProvider.generateKey();
       enableCORS("*", "*", "*");
	   post("/auth/signup", (req, res) -> {
		return Auth.createUser(req, res, key);
	   });
	   
	   post("/auth/login", (req, res) -> {
		  return Auth.Login(req, res, key);
	   });
	   get("/verify/:token", (req, res) -> {
		   return Auth.checkToken(req.params("token"), key) ;
	   });

	   get("/users/:id/data/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("id")))){
			 String id = req.params("id");
			 return UsersRouter.userData(id);
		   }
		   else{
			  return "mismatched user ids";
		   }
	   });
	   get("/users/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			 return UsersRouter.getUser(Auth.getId(req.params("token"), key));
		   }
		   else{
			  return "invalid token";
		   }
	   });
	   post("/places/update/:place_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PlaceRouter.updatePlace(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   get("/places/delete/:place_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PlaceRouter.deletePlace(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/places/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PlaceRouter.newPlace(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/people/update/:person_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PersonRouter.updatePerson(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   get("/people/delete/:person_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PersonRouter.deletePerson(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/people/:place_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return PersonRouter.newPerson(req, Auth.getId(req.params("token"), key));
		   }else{
			   return "mismatched user ids";
			}
	   });
	 
	   post("/notes/update/:note_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return NoteRouter.updateNote(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/notes/:person_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   
			   return NoteRouter.newNote(req, req.params("person_id"));
		   }else{
			   return "mismatched user ids";
			}
	   });
	   
	   post("/links/update/:link_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("user_id")))){
			   return LinkRouter.updateLink(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   
	   post("/links/:person_id/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return LinkRouter.newLink(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   
	   post("/notes/:id/delete/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return NoteRouter.deleteNote(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/links/:id/delete/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key)){
			   return LinkRouter.deleteLink(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
   }
   private static void enableCORS(final String origin, final String methods, final String headers) {
	    Spark.before(new Filter() {
	        @Override
	        public void handle(Request request, Response response) {
	            response.header("Access-Control-Allow-Origin", origin);
	            response.header("Access-Control-Request-Method", methods);
	            response.header("Access-Control-Allow-Headers", headers);
	        }

			
	    });
	    Spark.options("/*", (request,response)->{

    	    String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
    	    if (accessControlRequestHeaders != null) {
    	        response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
    	    }

    	    String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
    	    if(accessControlRequestMethod != null){
    		response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
    	    }

    	    return "OK";
    	});  
	}
   private static Connection getConnection() throws URISyntaxException, SQLException {
	    String dbUrl = System.getenv("JDBC_DATABASE_URL");
	    return DriverManager.getConnection(dbUrl);
	}
}