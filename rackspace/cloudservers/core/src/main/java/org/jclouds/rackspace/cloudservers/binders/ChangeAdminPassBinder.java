package org.jclouds.rackspace.cloudservers.binders;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.binders.JsonBinder;

import com.google.common.collect.ImmutableMap;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class ChangeAdminPassBinder extends JsonBinder {

   @Override
   public void addEntityToRequest(Map<String, String> postParams, HttpRequest request) {
      throw new IllegalStateException("Change Admin Pass is a PUT operation");
   }

   @Override
   public void addEntityToRequest(Object toBind, HttpRequest request) {
      checkArgument(toBind instanceof String, "this binder is only valid for Strings!");
      super.addEntityToRequest(ImmutableMap.of("server", ImmutableMap.of("adminPass", checkNotNull(
               toBind, "adminPass"))), request);
   }
}
