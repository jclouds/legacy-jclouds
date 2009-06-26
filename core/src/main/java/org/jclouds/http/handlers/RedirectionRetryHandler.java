/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
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

import static com.google.common.base.Preconditions.checkState;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;

import org.apache.commons.io.IOUtils;
import org.jclouds.http.HttpFutureCommand;
import org.jclouds.http.HttpHeaders;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.logging.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.name.Named;

/**
 * Handles Retryable responses with error codes in the 3xx range
 * 
 * @author Adrian Cole
 */
public class RedirectionRetryHandler implements HttpRetryHandler {
   private final int retryCountLimit;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public RedirectionRetryHandler(@Named("jclouds.http.max-redirects") int retryCountLimit) {
      this.retryCountLimit = retryCountLimit;
   }

   public boolean shouldRetryRequest(HttpFutureCommand<?> command, HttpResponse response) {
      closeConnectionButKeepContentStream(response);

      command.incrementRedirectCount();

      String hostHeader = response.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (hostHeader != null && command.getRedirectCount() < retryCountLimit) {
         URI endPoint = parseEndPoint(hostHeader);
         command.getRequest().setEndPoint(endPoint);
         return true;
      } else {
         return false;
      }
   }

   /**
    * Content stream may need to be read. However, we should always close the http stream.
    */
   @VisibleForTesting
   void closeConnectionButKeepContentStream(HttpResponse response) {
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

   private URI parseEndPoint(String hostHeader) {
      URI redirectURI = URI.create(hostHeader);
      String scheme = redirectURI.getScheme();

      checkState(redirectURI.getScheme().startsWith("http"), String.format(
               "header %s didn't parse an http scheme: [%s]", hostHeader, scheme));
      int port = redirectURI.getPort() > 0 ? redirectURI.getPort() : redirectURI.getScheme()
               .equals("https") ? 443 : 80;
      String host = redirectURI.getHost();
      checkState(!host.matches("[/]"), String.format(
               "header %s didn't parse an http host correctly: [%s]", hostHeader, host));
      URI endPoint = URI.create(String.format("%s://%s:%d", scheme, host, port));
      return endPoint;
   }
}
