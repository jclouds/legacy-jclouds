package org.jclouds.rackspace.cloudservers.functions;

import org.jclouds.http.HttpResponseException;
import org.jclouds.rackspace.cloudservers.domain.Image;

import com.google.common.base.Function;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class ReturnImageNotFoundOn404 implements Function<Exception, Image> {

   public Image apply(Exception from) {
      if (from instanceof HttpResponseException) {
         HttpResponseException responseException = (HttpResponseException) from;
         if (responseException.getResponse().getStatusCode() == 404) {
            return Image.NOT_FOUND;
         }
      }
      return null;
   }

}
