package org.jclouds.rackspace.cloudservers.functions;

import org.jclouds.http.HttpResponseException;
import org.jclouds.rackspace.cloudservers.domain.Flavor;

import com.google.common.base.Function;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ReturnFlavorNotFoundOn404 implements Function<Exception, Flavor> {

   public Flavor apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return Flavor.NOT_FOUND;
         }
      }
      return null;
   }

}
