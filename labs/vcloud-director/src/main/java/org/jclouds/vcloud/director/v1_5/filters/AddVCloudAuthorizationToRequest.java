package org.jclouds.vcloud.director.v1_5.filters;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpRequestFilter;
import org.jclouds.http.utils.ModifyRequest;
import org.jclouds.vcloud.director.v1_5.annotations.Session;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AddVCloudAuthorizationToRequest implements HttpRequestFilter {

   private final Supplier<String> sessionSupplier;

   @Inject
   public AddVCloudAuthorizationToRequest(@Session Supplier<String> sessionSupplier) {
      this.sessionSupplier = sessionSupplier;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      return ModifyRequest.replaceHeader(request, "x-vcloud-authorization", sessionSupplier.get());
   }

}