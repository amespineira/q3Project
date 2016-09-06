package q3Project.main;

import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
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

//import app.NewPlaces;


public class App {
   // JDBC driver name and database URL
	static final String JDBC_DRIVER = "org.postgresql.Driver";
	static final String DB_URL = "jdbc:postgresql://localhost/testdb";
    interface Validable {
        boolean isValid();
    }
    private static final int HTTP_BAD_REQUEST = 400;

   public static void main(String[] args) {
	   // Create the Flyway instance
       Flyway flyway = new Flyway();
       
       // Point it to the database
       flyway.setDataSource("jdbc:postgresql://localhost/testdb", null, null);
       flyway.clean();
       // Start the migration
       flyway.migrate();
       Key key = MacProvider.generateKey();

	   post("/auth/signup", (req, res) -> {
		return Auth.createUser(req, res, key);
	   });
	   
	   post("/auth/login", (req, res) -> {
		  return Auth.Login(req, res, key);
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
	   
	   post("/users/:id/places/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("id")))){
			   return UsersRouter.newPlace(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   
	   post("/users/:user_id/places/:place_id/people/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("user_id")))){
			   return UsersRouter.newPerson(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   
	   post("/users/:user_id/people/:person_id/notes/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("user_id")))){
			   return UsersRouter.newNote(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
	   post("/users/:user_id/people/:person_id/links/:token", (req, res) -> {
		   if(Auth.checkToken(req.params("token"), key, Integer.parseInt(req.params("user_id")))){
			   return UsersRouter.newLink(req);
		   }else{
			   return "mismatched user ids";
			}
	   });
   }

}