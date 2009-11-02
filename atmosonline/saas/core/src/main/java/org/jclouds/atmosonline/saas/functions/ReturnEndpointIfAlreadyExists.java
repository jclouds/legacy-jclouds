package org.jclouds.atmosonline.saas.functions;

import java.net.URI;

import org.jclouds.blobstore.KeyAlreadyExistsException;
import org.jclouds.rest.InvocationContext;
import org.jclouds.rest.internal.GeneratedHttpRequest;

import com.google.common.base.Function;

/**
 * 
 * @author Adrian Cole
 */
public class ReturnEndpointIfAlreadyExists implements Function<Exception, URI>, InvocationContext {

   private URI endpoint;

   public URI apply(Exception from) {
      if (from instanceof KeyAlreadyExistsException) {
         return endpoint;
      }
      return null;
   }

   public void setContext(GeneratedHttpRequest<?> request) {
      this.endpoint = request == null?null:request.getEndpoint();
   }

}