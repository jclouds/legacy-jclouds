package org.jclouds.http.binders;

import java.util.Collections;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.PostEntityBinder;

import com.google.gson.Gson;
import com.google.inject.Inject;

/**
 * Binds the object to the request as a json object.
 * 
 * @author adriancole
 * @since 4.0
 */
public class JsonBinder implements PostEntityBinder {
   protected final Gson gson;

   @Inject
   public JsonBinder(Gson gson) {
      this.gson = gson;
   }

   public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
      addEntityToRequest((Object) postParams, request);
   }

   public void addEntityToRequest(Object toBind, HttpRequest request) {
      String json = gson.toJson(toBind);
      request.setEntity(json);
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_LENGTH,
               Collections.singletonList(json.getBytes().length + ""));
      request.getHeaders().replaceValues(HttpHeaders.CONTENT_TYPE,
               Collections.singletonList(MediaType.APPLICATION_JSON));
   }

}