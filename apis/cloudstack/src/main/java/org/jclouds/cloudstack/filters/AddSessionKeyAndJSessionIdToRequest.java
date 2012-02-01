package org.jclouds.cloudstack.filters;

import static org.jclouds.http.utils.ModifyRequest.addQueryParam;
import static org.jclouds.http.utils.ModifyRequest.replaceHeader;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.cloudstack.domain.LoginResponse;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpRequest;

import com.google.common.base.Supplier;
import com.google.common.net.HttpHeaders;

/**
 * 
 * @author Andrei Savu, Adrian Cole
 * @see <a href="http://docs.cloud.com/CloudStack_Documentation/Customizing_the_CloudStack_UI#Cross_Site_Request_Forgery_%28CSRF%29"
 *      />
 */
@Singleton
public class AddSessionKeyAndJSessionIdToRequest implements AuthenticationFilter {

   private final Supplier<LoginResponse> loginResponseSupplier;
   private final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   public AddSessionKeyAndJSessionIdToRequest(Supplier<LoginResponse> loginResponseSupplier,
            Provider<UriBuilder> uriBuilderProvider) {
      this.loginResponseSupplier = loginResponseSupplier;
      this.uriBuilderProvider = uriBuilderProvider;
   }

   @Override
   public HttpRequest filter(HttpRequest request) throws HttpException {
      LoginResponse loginResponse = loginResponseSupplier.get();

      request = replaceHeader(request, HttpHeaders.COOKIE, "JSESSIONID=" + loginResponse.getJSessionId());
      request = addQueryParam(request, "sessionkey", loginResponse.getSessionKey(), uriBuilderProvider.get());
      return request;

   }

}