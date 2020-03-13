package com.rest.service;

import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.JsonObject;

@Stateless
@Path("/api")
public class ResourceRestApi {

	// private static final String jwtTokenCookieName = "JWT-TOKEN";
	private static final String AUTH_HEADER_STRING = "Authorization";
	private static final Integer EXPIRATION_TIME = 3600000;
	private static final String SECRET_KEY = "signingKey";

	private enum Status {
		SUCCESS, ERROR
	}

	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doGet() {
		JsonObject json = new JsonObject();
		addReponseToJson(json, Status.SUCCESS.name(), Response.Status.OK.getStatusCode(), "");
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}

	@GET
	@JsonTokenNeeded
	@Path("/secured")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doGetSecuredInfo() {
		JsonObject json = new JsonObject();
		addReponseToJson(json, Status.SUCCESS.name(), Response.Status.OK.getStatusCode(), "");
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}

	@GET
	@Path("/login/{username}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response getJWTToken(@PathParam("username") String username) {
		if (username != null && !username.isEmpty()) {
			String token = new JWTUtil().createJWT("userId", username, SECRET_KEY, EXPIRATION_TIME);
			JsonObject json = new JsonObject();
			addReponseToJson(json, Status.SUCCESS.name(), Response.Status.OK.getStatusCode(), "Token generated with success.");
			json.addProperty("userName", username);
			json.addProperty("token", token);
			return Response.status(Response.Status.OK).entity(json.toString()).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/login")
	@Produces({ MediaType.APPLICATION_JSON })
	public Response login(@HeaderParam("username") String username, @HeaderParam("password") String password) {
		if (username != null && password != null) {
			String token = new JWTUtil().createJWT("userId", username, SECRET_KEY, EXPIRATION_TIME);
			JsonObject json = new JsonObject();
			json.addProperty("token", token);
			return Response.status(Response.Status.OK).entity(json.toString()).build();
		} else {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@JsonTokenNeeded
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response doPost(@HeaderParam("param1") String headerSampleParam1) {
		JsonObject json = new JsonObject();
		json.addProperty("name", "Java Restful API with Jersey Example using JWT authentication (POST)");
		json.addProperty("param1", headerSampleParam1);
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}

	@PUT
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response putExample(@HeaderParam("param1") String headerParam1) {
		JsonObject json = new JsonObject();
		json.addProperty("name", "Java Restful API with Jersey Example using JWT authentication (PUT)");
		json.addProperty("param1", headerParam1);
		addReponseToJson(json, Status.SUCCESS.name(), Response.Status.OK.getStatusCode(), "Updated with success.");
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}

	@DELETE
	@Consumes({ MediaType.TEXT_XML })
	@Produces({ MediaType.APPLICATION_JSON })
	public Response deleteExample(@HeaderParam("param1") String headerParam1, @HeaderParam(AUTH_HEADER_STRING) String authString) {
		JsonObject json = new JsonObject();
		json.addProperty("name", "Java Restful API with Jersey Example using JWT authentication (DELETE)");
		json.addProperty("param1", headerParam1);
		addReponseToJson(json, Status.SUCCESS.name(), Response.Status.OK.getStatusCode(), "Deleted with success.");
		return Response.status(Response.Status.OK).entity(json.toString()).build();
	}

	// Sample method to add status information to Response
	public void addReponseToJson(JsonObject json, String status, Integer code, String message) {
		json.addProperty("AppName", "RESTful Java Web Services using JAX-RS and Jersey");
		json.addProperty("status", status);
		json.addProperty("code", code);
		json.addProperty("message", message);
	}

}