package fr.ub.m2gl;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

@JsonIgnoreProperties({"_id"})
public class User {
	@JsonProperty("First Name")
	private String firstname;
	@JsonProperty("Last Name")
	private String lastname;
	
	public User() {
		//super();
	}

	public User(String firstname, String lastname) {
		this.firstname = firstname;
		this.lastname = lastname;
	}

	@JsonProperty("First Name")
	public String getFirstname() {
		return firstname;
	}

	@JsonProperty("First Name")
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	@JsonProperty("Last Name")
	public String getLastname() {
		return lastname;
	}

	@JsonProperty("Last Name")
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	
	public String saveToDB() {
		MongoClient mongoClient = new MongoClient();
		try {
		    MongoDatabase db = mongoClient.getDatabase("myBase");
		    MongoCollection<Document> collection = db.getCollection("myCollection");
		    ObjectMapper mapper = new MyObjectMapperProvider().getContext(User.class);
		    
		    String jsonString = mapper.writeValueAsString(this);
		    Document doc = Document.parse(jsonString);
		    Document search = collection.find(and(eq("First Name", firstname),eq("Last Name", lastname))).first();
		    if (search != null) {
		    	return null;
		    }
		    collection.insertOne(doc);
		    return "User " + getFirstname() + " " + getLastname() + " added successfully.";
		} catch (Exception e) {
		    e.printStackTrace();
		} finally{
		    mongoClient.close();
		}
		return null;
	}
	
	public String updateInDB(String newfirstname, String newlastname) {
		MongoClient mongoClient = new MongoClient();
		try {
		    MongoDatabase db = mongoClient.getDatabase("myBase");
		    MongoCollection<Document> collection = db.getCollection("myCollection");
		    ObjectMapper mapper = new MyObjectMapperProvider().getContext(User.class);
		    
		    String jsonString = mapper.writeValueAsString(this);
		    Document doc = Document.parse(jsonString);
		    this.setFirstname(newfirstname);
		    this.setLastname(newlastname);
		    String newjsonString = mapper.writeValueAsString(this);
		    Document newdoc = Document.parse(newjsonString);
		    System.out.println(newjsonString);
		    //collection.updateOne(doc, newdoc);
		    Document set = new Document("$set", newdoc);
		    //collection.findOneAndUpdate(doc, set);
		    Document res = collection.findOneAndUpdate(doc, set);
		    if (res == null) {
		    	//throw new NoSuchElementException("User does not exist in database");
		    	return null;
		    }
		    return "User " + getFirstname() + " " + getLastname() + " edited successfully.";
		} catch (Exception e) {
		    e.printStackTrace();
		} finally{
		    mongoClient.close();
		}
		//throw new NoSuchElementException("User does not exist in database");
		return null;
	}

	public String delfromDB() {
		MongoClient mongoClient = new MongoClient();
		try {
		    MongoDatabase db = mongoClient.getDatabase("myBase");
		    MongoCollection<Document> collection = db.getCollection("myCollection");
		    ObjectMapper mapper = new MyObjectMapperProvider().getContext(User.class);
		    
		    String jsonString = mapper.writeValueAsString(this);
		    Document doc = Document.parse(jsonString);
		    //DeleteResult res = collection.deleteOne(doc);
		    //collection.findOneAndDelete(doc);
		    Document res = collection.findOneAndDelete(doc);
		    if (res == null) {
		    	//throw new NoSuchElementException("User does not exist in database");
		    	return null;
		    }
		    return "User " + getFirstname() + " " + getLastname() + " deleted successfully.";
		} catch (Exception e) {
		    e.printStackTrace();
		} finally{
		    mongoClient.close();
		}
		//throw new NoSuchElementException("User does not exist in database");
		return null;
	}
	
}
