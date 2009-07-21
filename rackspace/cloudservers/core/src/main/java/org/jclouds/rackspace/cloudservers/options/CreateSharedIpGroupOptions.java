package org.jclouds.rackspace.cloudservers.options;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.binders.JsonBinder;

import com.google.common.collect.ImmutableMap;
import com.google.inject.internal.Nullable;

/**
 * 
 * 
 * @author Adrian Cole
 * 
 */
public class CreateSharedIpGroupOptions extends JsonBinder {
   Integer serverId;

   @SuppressWarnings("unused")
   private class SharedIpGroupRequest {
      final String name;
      Integer server;

      private SharedIpGroupRequest(String name, @Nullable Integer serverId) {
         this.name = name;
         this.server = serverId;
      }

   }

   @Override
   public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
      SharedIpGroupRequest createRequest = new SharedIpGroupRequest(checkNotNull(postParams
               .get("name")), serverId);
      super.addEntityToRequest(ImmutableMap.of("sharedIpGroup", createRequest), request);
   }

   @Override
   public void addEntityToRequest(Object toBind, HttpRequest request) {
      throw new IllegalStateException("CreateSharedIpGroup is a POST operation");
   }

   /**
    * 
    * @param id
    *           of the server to include with this request.
    */
   public CreateSharedIpGroupOptions withServer(int id) {
      checkArgument(id > 0, "server id must be a positive number");
      this.serverId = id;
      return this;
   }

   public static class Builder {

      /**
       * @see CreateSharedIpGroupOptions#withServer(int)
       */
      public static CreateSharedIpGroupOptions withServer(int id) {
         CreateSharedIpGroupOptions options = new CreateSharedIpGroupOptions();
         return options.withServer(id);
      }
   }
}
