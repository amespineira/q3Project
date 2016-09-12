package q3Project.main;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import spark.Request;

public class Validator {
	public String valUser(Request req){
		JsonParser parser = new JsonParser();
		  JsonElement jsonTree = parser.parse(req.body());
		  JsonElement username = null;
		  JsonElement password = null;
		  if(jsonTree.isJsonObject()) {
			  JsonObject jsonObject = jsonTree.getAsJsonObject();
			  username = jsonObject.get("username");
			  password = jsonObject.get("password");
		  }
		 if(username.getAsString().length()<5){
			 return "invalid username";
		 }
		 if(username.getAsString().length()>20){
			 return "invalid username";
		 }
		 if(password.getAsString().length()<5){
			 return "invalid password";
		 }
		 if(password.getAsString().length()>20){
			 return "invalid password";
		 }
		 return "valid";
	}
	
}
