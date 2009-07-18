package org.jclouds.rest;

import java.util.Map;

import org.jclouds.http.HttpRequest;

/**
 * Builds the entity of a Post request.
 * 
 * @author Adrian Cole
 * 
 */
public interface PostEntityBinder extends EntityBinder {

   /**
    * creates and binds the POST entity to the request using parameters specified.
    * 
    * @see PostParam
    */
   public void addEntityToRequest(Map<String,String> postParams, HttpRequest request);
   
}