package org.jclouds.deltacloud.handlers;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
@Singleton
public class DeltacloudRedirectionRetryHandler extends RedirectionRetryHandler {

   @Inject
   public DeltacloudRedirectionRetryHandler(Provider<UriBuilder> uriBuilderProvider,
         BackoffLimitedRetryHandler backoffHandler) {
      super(uriBuilderProvider, backoffHandler);
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (command.getCurrentRequest().getMethod().equals("DELETE")) {
         return false;
      } else {
         return super.shouldRetryRequest(command, response);
      }
   }
}
