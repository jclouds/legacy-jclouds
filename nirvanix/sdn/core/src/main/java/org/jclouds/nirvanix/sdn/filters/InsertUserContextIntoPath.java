package org.jclouds.nirvanix.sdn.filters;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.nirvanix.sdn.reference.SDNConstants;
import org.jclouds.rest.internal.GeneratedHttpRequest;

/**
 * Adds the Session Token to the request. This will update the Session Token before 20 minutes is
 * up.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class InsertUserContextIntoPath implements HttpRequestFilter {

   private final AddSessionTokenToRequest sessionManager;
   private final String pathPrefix;

   @Inject
   public InsertUserContextIntoPath(AddSessionTokenToRequest sessionManager,
            @Named(SDNConstants.PROPERTY_SDN_APPNAME) String appname,
            @Named(SDNConstants.PROPERTY_SDN_USERNAME) String username) {
      this.sessionManager = sessionManager;
      this.pathPrefix = String.format("/%s/%s/", appname, username);
   }

   public void filter(HttpRequest request) throws HttpException {
      checkArgument(checkNotNull(request, "input") instanceof GeneratedHttpRequest<?>,
               "this decorator is only valid for GeneratedHttpRequests!");
      String sessionToken = sessionManager.getSessionToken();
      int prefixIndex = request.getEndpoint().getPath().indexOf(pathPrefix);
      String path;
      if (prefixIndex == -1) { // addToken
         path = "/" + sessionToken + pathPrefix + request.getEndpoint().getPath().substring(1);
      } else { // replace token
         path = "/" + sessionToken + request.getEndpoint().getPath().substring(prefixIndex);
      }
      ((GeneratedHttpRequest<?>) request).replacePath(path);

   }

}