/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import javax.inject.Inject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.HttpHeaders;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.http.handlers.RedirectionRetryHandler;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
public class AWSRedirectionRetryHandler extends RedirectionRetryHandler {
   private final AWSUtils utils;

   @Inject
   public AWSRedirectionRetryHandler(BackoffLimitedRetryHandler backoffHandler, AWSUtils utils) {
      super(backoffHandler);
      this.utils = utils;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getFirstHeaderOrNull(HttpHeaders.LOCATION) == null
               && (response.getStatusCode() == 301 || response.getStatusCode() == 307)) {
         byte[] content = HttpUtils.closeClientButKeepContentStream(response);
         if (command.getRequest().getMethod() == HttpMethod.HEAD) {
            command.redirectAsGet();
            return true;
         } else {
            command.incrementRedirectCount();
            try {
               AWSError error = utils.parseAWSErrorFromContent(command, response, new String(
                        content));
               String host = error.getDetails().get(AWSConstants.ENDPOINT);
               if (host != null) {
                  if (host.equals(command.getRequest().getEndpoint().getHost())) {
                     // must be an amazon error related to
                     // http://developer.amazonwebservices.com/connect/thread.jspa?messageID=72287&#72287
                     return backoffHandler.shouldRetryRequest(command, response);
                  } else {
                     command.redirect(host, command.getRequest().getEndpoint().getPort());
                  }
                  return true;
               } else {
                  return false;
               }
            } catch (HttpException e) {
               logger.error(e, "error on redirect for command %s; response %s; retrying...",
                        command, response);
               return false;
            }
         }
      } else {
         return super.shouldRetryRequest(command, response);
      }
   }
}
