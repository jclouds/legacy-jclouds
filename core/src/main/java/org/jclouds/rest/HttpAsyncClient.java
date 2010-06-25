package org.jclouds.rest;

import java.io.InputStream;
import java.net.URI;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;

import org.jclouds.http.Payload;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.functions.ReturnFalseOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Simple rest client
 * 
 * @author Adrian Cole
 */
public interface HttpAsyncClient {
   /**
    * @see HttpClient#post
    */
   @PUT
   ListenableFuture<Void> put(@EndpointParam URI location, Payload payload);

   /**
    * @see HttpClient#post
    */
   @POST
   ListenableFuture<Void> post(@EndpointParam URI location, Payload payload);

   /**
    * @see HttpClient#exists
    */
   @HEAD
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> exists(@EndpointParam URI location);

   /**
    * @see HttpClient#get
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InputStream> get(@EndpointParam URI location);

   /**
    * @see HttpClient#delete
    */
   @DELETE
   @ExceptionParser(ReturnFalseOnNotFoundOr404.class)
   ListenableFuture<Boolean> delete(@EndpointParam URI location);

}
