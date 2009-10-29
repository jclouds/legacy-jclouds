/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 */
package org.jclouds.http.handlers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
public class RedirectionRetryHandler implements HttpRetryHandler {
   @Inject(optional = true)
   @Named(HttpConstants.PROPERTY_HTTP_MAX_REDIRECTS)
   protected int retryCountLimit = 5;

   @Resource
   protected Logger logger = Logger.NULL;

   protected final BackoffLimitedRetryHandler backoffHandler;

   @Inject
   public RedirectionRetryHandler(BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      closeClientButKeepContentStream(response);

      String hostHeader = response.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (hostHeader != null && command.incrementRedirectCount() < retryCountLimit) {
         URI redirectionUrl = UriBuilder.fromUri(hostHeader).build();
         if (redirectionUrl.getHost().equals(command.getRequest().getEndpoint().getHost())
                  && redirectionUrl.getPort() == command.getRequest().getEndpoint().getPort()) {
            return backoffHandler.shouldRetryRequest(command, response);
         } else {
            command.setHostAndPort(redirectionUrl.getHost(), redirectionUrl.getPort());
         }
         return true;
      } else {
         return false;
      }
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    */
   @VisibleForTesting
   void closeClientButKeepContentStream(HttpResponse response) {
      if (response.getContent() != null) {
         try {
            byte[] data = IOUtils.toByteArray(response.getContent());
            response.setContent(new ByteArrayInputStream(data));
         } catch (IOException e) {
            logger.error(e, "Error consuming input");
         } finally {
            IOUtils.closeQuietly(response.getContent());
         }
      }
   }

}
