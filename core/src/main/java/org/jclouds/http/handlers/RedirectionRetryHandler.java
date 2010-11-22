/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.http.handlers;

import static java.util.Collections.singletonList;
import static javax.ws.rs.core.HttpHeaders.HOST;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
@Singleton
public class RedirectionRetryHandler implements HttpRetryHandler {
   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_REDIRECTS)
   protected int retryCountLimit = 5;

   @Resource
   protected Logger logger = Logger.NULL;

   protected final BackoffLimitedRetryHandler backoffHandler;
   protected final Provider<UriBuilder> uriBuilderProvider;

   @Inject
   protected RedirectionRetryHandler(Provider<UriBuilder> uriBuilderProvider, BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
      this.uriBuilderProvider = uriBuilderProvider;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      closeClientButKeepContentStream(response);
      String hostHeader = response.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (command.incrementRedirectCount() < retryCountLimit && hostHeader != null) {
         URI redirectionUrl = URI.create(hostHeader);

         // if you are sent the same uri, assume there's a transient problem and retry.
         if (redirectionUrl.equals(command.getRequest().getEndpoint()))
            return backoffHandler.shouldRetryRequest(command, response);

         UriBuilder builder = uriBuilderProvider.get().uri(command.getRequest().getEndpoint());
         assert redirectionUrl.getPath() != null : "no path in redirect header from: " + response;
         builder.replacePath(redirectionUrl.getPath());

         if (redirectionUrl.getScheme() != null)
            builder.scheme(redirectionUrl.getScheme());

         if (redirectionUrl.getHost() != null) {
            builder.host(redirectionUrl.getHost());
            if (command.getRequest().getFirstHeaderOrNull(HOST) != null)
               command.getRequest().getHeaders().replaceValues(HOST, singletonList(redirectionUrl.getHost()));
         }
         if (redirectionUrl.getPort() != command.getRequest().getEndpoint().getPort())
            builder.port(redirectionUrl.getPort());

         if (redirectionUrl.getQuery() != null)
            builder.replaceQuery(redirectionUrl.getQuery());

         command.getRequest().setEndpoint(builder.build());
         return true;
      } else {
         return false;
      }
   }

}