package com.rest.service;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/* A name-binding annotation need to be attached to a custom JAX-RS Application subclass */
@ApplicationPath("/")
public class MyApplication extends Application {
}