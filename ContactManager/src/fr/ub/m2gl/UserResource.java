package fr.ub.m2gl;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.and;

@Path("")
public class UserResource {
	
	@GET
	@Path("/users")
	@Produces(MediaType.APPLICATION_JSON)
    public List<User> getUsers() {
    	List<User> users = new ArrayList<User>();
    	MongoClient mongoClient = new MongoClient();
		try {
		    MongoDatabase db = mongoClient.getDatabase("myBase");
		    MongoCollection<Document> collection = db.getCollection("myCollection");
		    MongoCursor<Document> iter = collection.find().iterator();
		    ObjectMapper mapper = new MyObjectMapperProvider().getContext(User.class);
		    while (iter.hasNext()) {
		    	Document doc = iter.next();
		    	String obj = doc.toJson();
		    	User userFromDb = mapper.readValue(obj, User.class);
		    	if (userFromDb.getFirstname() != null && userFromDb.getLastname() != null) {
		    		users.add(userFromDb);
		    	}
		    }
		} catch (Exception e) {
		    e.printStackTrace();
		} finally{
		    mongoClient.close();
		}
    	return users;
    }
	
	@GET
	@Path("/user/{firstname}/{lastname}")
	@Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("firstname") String firstname, @PathParam("lastname") String lastname) {
    	MongoClient mongoClient = new MongoClient();
		try {
		    MongoDatabase db = mongoClient.getDatabase("myBase");
		    MongoCollection<Document> collection = db.getCollection("myCollection");
		    ObjectMapper mapper = new MyObjectMapperProvider().getContext(User.class);
	    	Document doc = collection.find(and(eq("First Name", firstname),eq("Last Name", lastname))).first();
	    	if (doc != null) {
	    		String obj = doc.toJson();
		    	User userFromDb = mapper.readValue(obj, User.class);
	    		return userFromDb;
	    	}
		} catch (Exception e) {
		    e.printStackTrace();
		} finally{
		    mongoClient.close();
		}
    	throw new NotFoundException("User not found");
    }
	
	@PUT
	@Path("/user/{firstname}/{lastname}")
    @Consumes(MediaType.APPLICATION_JSON)
	@Produces("text/plain")
    public Response editUser(@PathParam("firstname") String firstname, @PathParam("lastname") String lastname, User user) {
		User userOrig = new User(firstname, lastname);
		String res = userOrig.updateInDB(user.getFirstname(), user.getLastname());
		if (res != null) {
			return Response.ok(res).build();
		}
		else {
			return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
		}
	}
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
    @Produces("text/plain")
    public Response addUser(User user) {
		String res = user.saveToDB();
		if (res != null) {
			return Response.ok(res).build();
		}
		else {
			return Response.status(Response.Status.CONFLICT).entity("User already exists").build();
		}
    }
	
	@DELETE
	@Path("/user/{firstname}/{lastname}")
    @Produces("text/plain")
    public Response delUser(@PathParam("firstname") String firstname, @PathParam("lastname") String lastname) {
		User user = new User(firstname, lastname);
    	String res = user.delfromDB();
    	if (res != null) {
			return Response.ok(res).build();
		}
		else {
			return Response.status(Response.Status.NOT_FOUND).entity("User not found").build();
		}
    }
}
