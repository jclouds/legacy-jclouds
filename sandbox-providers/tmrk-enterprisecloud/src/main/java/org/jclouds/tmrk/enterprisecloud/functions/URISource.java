package org.jclouds.tmrk.enterprisecloud.functions;

import com.google.common.base.Function;
import org.jclouds.tmrk.enterprisecloud.domain.vm.CreateVirtualMachine;

import java.net.URI;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Defines the URI Source interface and a function that uses it to obtain the URI
 * @author Jason King
 */
public interface URISource {

   URI getURI();

   /**
    * Converts a URI Source to a URI
    */
   public class GetURI implements Function<Object, URI> {

      /**
       * Expects to be called with an object implementing the URISource interface
       * Calls the getURI method to obtain the URI.
       * @param source a URISource
       * @return the URI
       */
      @Override
      public URI apply(Object source) {
         checkArgument(checkNotNull(source, "source") instanceof URISource, "this function is only valid for URISource instances");
         URISource uriSource = URISource.class.cast(source);
         return uriSource.getURI();
      }
   }
}
