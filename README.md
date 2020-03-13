
Example REST APIs in Java with JAX-RS and Jersey using JWT Authentication.


*** Filter Checking the JSON Web Token
The magic hides behind JsonTokenNeeded. Well, not really, it hides behind the BearerTokenFilter. JsonTokenNeeded is just a JAX-RS name binding (think of it as a CDI interceptor binding), so it’s just an annotation that binds to a filter.


1 - Gets the HTTP Authorization header from the request and checks for the JSON Web Token (the Bearer string)
2 - It validates the token (using the JWT library)
3 - If the token is valid, the method is invoked and provided correct result of request.
4 - If the token is invalid, a 401 Unauthorized is sent to the client
