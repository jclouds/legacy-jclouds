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
package org.jclouds.s3.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.Uris.uriBuilder;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.HttpHeaders;

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
public class S3RedirectionRetryHandler extends RedirectionRetryHandler {
   private final AWSUtils utils;

   @Inject
   public S3RedirectionRetryHandler(BackoffLimitedRetryHandler backoffHandler, AWSUtils utils) {
      super(backoffHandler);
      this.utils = utils;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getFirstHeaderOrNull(HttpHeaders.LOCATION) == null
            && (response.getStatusCode() == 301 || response.getStatusCode() == 307)) {
         command.incrementRedirectCount();
         closeClientButKeepContentStream(response);
         AWSError error = utils.parseAWSErrorFromContent(command.getCurrentRequest(), response);
         String host = error.getDetails().get("Endpoint");
         if (host != null) {
            if (host.equals(command.getCurrentRequest().getEndpoint().getHost())) {
               // must be an amazon error related to
               // http://developer.amazonwebservices.com/connect/thread.jspa?messageID=72287&#72287
               return backoffHandler.shouldRetryRequest(command, response);
            } else {
               URI newHost = uriBuilder(command.getCurrentRequest().getEndpoint()).host(host).build();
               command.setCurrentRequest(command.getCurrentRequest().toBuilder().endpoint(newHost).build());
            }
            return true;
         } else {
            return false;
         }
      } else {
         return super.shouldRetryRequest(command, response);
      }
   }
}
