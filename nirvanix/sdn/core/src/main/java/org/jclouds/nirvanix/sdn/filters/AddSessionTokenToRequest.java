package org.jclouds.nirvanix.sdn.filters;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.nirvanix.sdn.SessionToken;
import org.jclouds.nirvanix.sdn.reference.SDNQueryParams;

/**
 * Adds the Session Token to the request. This will update the Session Token before 20 minutes is
 * up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class AddSessionTokenToRequest implements HttpRequestFilter {

   private final Provider<String> authTokenProvider;

   public final long BILLION = 1000000000;
   public final long MINUTES = 60 * BILLION;

   private final AtomicReference<String> authToken;
   private final AtomicLong trigger = new AtomicLong(0);

   /**
    * Start the time update service. Nirvanix clocks need to be 20 minutes of the session token.
    * This is not performed per-request, as creation of the token is a slow, synchronized command.
    */
   synchronized void updateIfTimeOut() {

      if (trigger.get() - System.nanoTime() <= 0) {
         createNewToken();
      }

   }

   // this is a hotspot when submitted concurrently, so be lazy.
   // session tokens expire in 20 minutes of no use, but let's be a little paraniod and go 19
   public String createNewToken() {
      authToken.set(authTokenProvider.get());
      trigger.set(System.nanoTime() + System.nanoTime() + 19 * MINUTES);
      return authToken.get();

   }

   public String getSessionToken() {
      updateIfTimeOut();
      return authToken.get();
   }

   @Inject
   public AddSessionTokenToRequest(@SessionToken Provider<String> authTokenProvider) {
      this.authTokenProvider = authTokenProvider;
      authToken = new AtomicReference<String>();
   }

   public HttpRequest filter(HttpRequest request) throws HttpException {
      UriBuilder builder = UriBuilder.fromUri(request.getEndpoint());
      builder.replaceQueryParam(SDNQueryParams.SESSIONTOKEN, getSessionToken());
      List<HttpRequestFilter> oldFilters = request.getFilters();
      request = new HttpRequest(request.getMethod(), builder.build(), request.getHeaders(), request
               .getEntity());
      request.getFilters().addAll(oldFilters);
      return request;
   }

}