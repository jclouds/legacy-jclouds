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

package org.jclouds.aws.handlers;

import static org.jclouds.http.HttpUtils.changeSchemeHostAndPortTo;
import static org.jclouds.http.HttpUtils.changeToGETRequest;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
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
public class AWSRedirectionRetryHandler extends RedirectionRetryHandler {
   private final AWSUtils utils;

   @Inject
   public AWSRedirectionRetryHandler(Provider<UriBuilder> uriBuilderProvider,
            BackoffLimitedRetryHandler backoffHandler, AWSUtils utils) {
      super(uriBuilderProvider, backoffHandler);
      this.utils = utils;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getFirstHeaderOrNull(HttpHeaders.LOCATION) == null
               && (response.getStatusCode() == 301 || response.getStatusCode() == 307)) {
         if (command.getRequest().getMethod() == HttpMethod.HEAD) {
            changeToGETRequest(command.getRequest());
            return true;
         } else {
            command.incrementRedirectCount();
            closeClientButKeepContentStream(response);
            AWSError error = utils.parseAWSErrorFromContent(command.getRequest(), response);
            String host = error.getDetails().get("Endpoint");
            if (host != null) {
               if (host.equals(command.getRequest().getEndpoint().getHost())) {
                  // must be an amazon error related to
                  // http://developer.amazonwebservices.com/connect/thread.jspa?messageID=72287&#72287
                  return backoffHandler.shouldRetryRequest(command, response);
               } else {
                  changeSchemeHostAndPortTo(command.getRequest(), command.getRequest()
                           .getEndpoint().getScheme(), host, command.getRequest().getEndpoint()
                           .getPort(), uriBuilderProvider.get());
               }
               return true;
            } else {
               return false;
            }
         }
      } else {
         return super.shouldRetryRequest(command, response);
      }
   }
}
