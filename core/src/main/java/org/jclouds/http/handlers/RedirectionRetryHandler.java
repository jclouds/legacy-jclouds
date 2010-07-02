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
package org.jclouds.http.handlers;

import static org.jclouds.http.HttpUtils.changePathTo;
import static org.jclouds.http.HttpUtils.changeSchemeHostAndPortTo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;
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
   protected RedirectionRetryHandler(Provider<UriBuilder> uriBuilderProvider,
            BackoffLimitedRetryHandler backoffHandler) {
      this.backoffHandler = backoffHandler;
      this.uriBuilderProvider = uriBuilderProvider;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      closeClientButKeepContentStream(response);

      String hostHeader = response.getFirstHeaderOrNull(HttpHeaders.LOCATION);
      if (hostHeader != null && command.incrementRedirectCount() < retryCountLimit) {
         URI redirectionUrl = uriBuilderProvider.get().uri(URI.create(hostHeader)).build();
         if (redirectionUrl.getScheme().equals(command.getRequest().getEndpoint().getScheme())
                  && redirectionUrl.getHost().equals(command.getRequest().getEndpoint().getHost())
                  && redirectionUrl.getPort() == command.getRequest().getEndpoint().getPort()) {
            if (!redirectionUrl.getPath().equals(command.getRequest().getEndpoint().getPath())) {
               changePathTo(command.getRequest(), redirectionUrl.getPath(), uriBuilderProvider
                        .get());
            } else {
               return backoffHandler.shouldRetryRequest(command, response);
            }
         } else {
            changeSchemeHostAndPortTo(command.getRequest(), redirectionUrl.getScheme(),
                     redirectionUrl.getHost(), redirectionUrl.getPort(), uriBuilderProvider.get());
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
            byte[] data = ByteStreams.toByteArray(response.getContent());
            response.setContent(new ByteArrayInputStream(data));
         } catch (IOException e) {
            logger.error(e, "Error consuming input");
         } finally {
            Closeables.closeQuietly(response.getContent());
         }
      }
   }

}
