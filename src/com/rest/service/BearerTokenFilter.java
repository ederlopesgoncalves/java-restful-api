package com.rest.service;

import java.io.IOException;
import java.util.List;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.log4j.Logger;

import com.google.gson.JsonObject;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;

@Provider
@JsonTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class BearerTokenFilter implements ContainerRequestFilter {

	final static Logger log = Logger.getLogger(BearerTokenFilter.class);

	private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
	private static final String AUTHORIZATION_HEADER_PREFIX = "Bearer ";
	private static final String SECURED_URL_PREFIX = "api";

	private static final String SECRET_KEY = "signingKey";

	private enum Status {
		SUCCESS, ERROR
	}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		log.debug("-- Request Filter Token --");
		if (requestContext.getUriInfo().getPath().contains(SECURED_URL_PREFIX)) {
			List<String> authHeader = requestContext.getHeaders().get(AUTHORIZATION_HEADER_KEY);
			JsonObject json = new JsonObject();
			if (authHeader != null && authHeader.size() > 0) {
				String authToken = authHeader.get(0);
				authToken = authToken.replaceFirst(AUTHORIZATION_HEADER_PREFIX, "");
				Claims authData = null;
				try {
					authData = new JWTUtil().decodeJWT(authToken, SECRET_KEY);

					if (authData != null && !authData.isEmpty()) {
						return;
					} else {
						addReponseToJson(json, Status.ERROR.name(), Response.Status.NOT_FOUND.getStatusCode(), "User not found.");
						Response unauthorizedStatus = Response.status(Response.Status.NOT_FOUND).entity(json.toString()).build();
						requestContext.abortWith(unauthorizedStatus);
					}

				} catch (ExpiredJwtException e) {
					addReponseToJson(json, Status.ERROR.name(), Response.Status.UNAUTHORIZED.getStatusCode(), "Token expired.");
					Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity(json.toString()).build();
					requestContext.abortWith(unauthorizedStatus);
				} catch (MalformedJwtException e) {
					addReponseToJson(json, Status.ERROR.name(), Response.Status.FORBIDDEN.getStatusCode(), "User not authorized.");
					Response unauthorizedStatus = Response.status(Response.Status.FORBIDDEN).entity(json.toString()).build();
					requestContext.abortWith(unauthorizedStatus);
				} catch (Exception e) {
					requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
				}
			} else {
				addReponseToJson(json, Status.ERROR.name(), Response.Status.UNAUTHORIZED.getStatusCode(), "Authorization Header is empty.");
				Response unauthorizedStatus = Response.status(Response.Status.UNAUTHORIZED).entity(json.toString()).build();
				requestContext.abortWith(unauthorizedStatus);
			}
		}

	}

	// Sample method to add status information to Response
	public void addReponseToJson(JsonObject json, String status, Integer code, String message) {
		json.addProperty("status", status);
		json.addProperty("code", code);
		json.addProperty("message", message);
	}

}
