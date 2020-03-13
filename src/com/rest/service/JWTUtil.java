package com.rest.service;

import java.security.Key;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.apache.log4j.Logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class JWTUtil {

	final static Logger log = Logger.getLogger(JWTUtil.class);

	private static final String TOKEN_PREFIX = "Bearer";

	// Sample method to construct a JWT
	public String createJWT(String id, String userName, String secretKey, long expirationTimeMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		// Let's set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(id).setIssuedAt(now).setSubject(userName).setIssuer(userName).signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (expirationTimeMillis >= 0) {
			long expMillis = nowMillis + expirationTimeMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	// Sample method to validate and read the JWT
	public Claims decodeJWT(String jwt, String secretKey) throws Exception {
		// Get Bearer token
		String token = jwt.substring(TOKEN_PREFIX.length()).trim();

		// This line will throw an exception if it is not a signed JWS (as expected)
		Claims claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(secretKey)).parseClaimsJws(token).getBody();

		// Debug Logs
		log.debug("ID: " + claims.getId());
		log.debug("Subject: " + claims.getSubject());
		log.debug("Issuer: " + claims.getIssuer());
		log.debug("Expiration: " + claims.getExpiration());

		return claims;
	}

}
