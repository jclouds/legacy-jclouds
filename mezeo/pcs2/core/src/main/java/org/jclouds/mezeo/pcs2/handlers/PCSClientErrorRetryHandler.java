package org.jclouds.mezeo.pcs2.handlers;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class PCSClientErrorRetryHandler implements HttpRetryHandler {

   private final BackoffLimitedRetryHandler backoffHandler;

   @Inject
   public PCSClientErrorRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   @Resource
   protected Logger logger = Logger.NULL;

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getStatusCode() == 400) {
         return backoffHandler.shouldRetryRequest(command, response);
      }
      return false;
   }

}
