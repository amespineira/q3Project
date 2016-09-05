package q3Project.main;

import java.sql.* ;  // for standard JDBC programs
import java.math.* ; // for BigDecimal and BigInteger support
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

import q3Project.main.Users;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
       flyway.setDataSource("jdbc:postgresql://localhost/testdb", "Attacktic", null);
       flyway.clean();
       // Start the migration
       flyway.migrate();
       
	   post("/users", (req, res) -> {
		Users.createUser(req, res);
		return req;
	   });
	   
	   post("/verify", (req, res) -> {
		 Users.Verify(req, res);
		 return req;
	   });
	   
	   get("/users/:id/data", (req, res) -> {
		 String id = req.params("id");
		 Users.userData(id);
		 return req;
	   });
   }

}