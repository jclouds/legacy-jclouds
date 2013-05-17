/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.http.handlers;

import static com.google.common.net.HttpHeaders.HOST;
import static com.google.common.net.HttpHeaders.LOCATION;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 3xx range, backing off
 * when redirecting to itself.
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

   @Inject
   protected RedirectionRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      closeClientButKeepContentStream(response);
      if (!command.isReplayable()) {
         logger.error("Cannot retry after redirect, command is not replayable: %s", command);
         return false;
      }
      if (command.incrementRedirectCount() > retryCountLimit) {
         logger.error("Cannot retry after redirect, command exceeded retry limit %d: %s", retryCountLimit, command);
         return false;
      }
      String location = response.getFirstHeaderOrNull(LOCATION);
      if (location == null) {
         logger.error("Cannot retry after redirect, no host header: %s", command);
         return false;
      }
      HttpRequest current = command.getCurrentRequest();
      URI redirect = URI.create(location);
      if (!redirect.isAbsolute()) {
         if (redirect.getPath() == null) {
            logger.error("Cannot retry after redirect, no path in location header %s", command);
            return false;
         }
         redirect = uriBuilder(current.getEndpoint()).path(redirect.getPath()).query(redirect.getQuery()).build();
      }
      if (redirect.equals(current.getEndpoint())) {
         backoffHandler.imposeBackoffExponentialDelay(command.getRedirectCount(), "redirect: " + command.toString());
      } else if (current.getFirstHeaderOrNull(HOST) != null && redirect.getHost() != null) {
         String host = redirect.getPort() > 0 ? redirect.getHost() + ":" + redirect.getPort() : redirect.getHost();
         command.setCurrentRequest(current.toBuilder().replaceHeader(HOST, host).endpoint(redirect).build());
      } else {
         command.setCurrentRequest(current.toBuilder().endpoint(redirect).build());
      }
      return true;
   }
}
