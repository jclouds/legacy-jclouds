package org.jclouds.vcloud.director.v1_5.filters;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.vcloud.director.v1_5.annotations.Session;

import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddVCloudAuthorizationAndCookieToRequest implements HttpRequestFilter {

   private final Supplier<String> sessionSupplier;

   @Inject
   public AddVCloudAuthorizationAndCookieToRequest(@Session Supplier<String> sessionSupplier) {
      this.sessionSupplier = sessionSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      String token = sessionSupplier.get();
      return request
               .toBuilder()
               .replaceHeaders(
                        ImmutableMultimap.of("x-vcloud-authorization", token, HttpHeaders.COOKIE, "vcloud-token="
                                 + token)).build();
   }

}
