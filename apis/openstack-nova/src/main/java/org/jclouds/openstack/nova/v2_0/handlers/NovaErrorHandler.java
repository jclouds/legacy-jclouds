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
package org.jclouds.openstack.nova.v2_0.handlers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.in;
import static com.google.common.base.Strings.emptyToNull;
import static com.google.common.collect.Maps.filterKeys;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.util.Set;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.date.DateCodecFactory;
import org.jclouds.fallbacks.HeaderToRetryAfterException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.nova.v2_0.functions.OverLimitParser;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.rest.RetryAfterException;

import com.google.common.base.Optional;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole, Steve Loughran
 * 
 */
// TODO: is there error spec someplace? let's type errors, etc.
@Singleton
public class NovaErrorHandler implements HttpErrorHandler {

   @Resource
   protected Logger logger = Logger.NULL;
   protected final HeaderToRetryAfterException retryAfterParser;
   protected final OverLimitParser overLimitParser;

   protected NovaErrorHandler(HeaderToRetryAfterException retryAfterParser, OverLimitParser overLimitParser) {
      this.retryAfterParser = checkNotNull(retryAfterParser, "retryAfterParser");
      this.overLimitParser = checkNotNull(overLimitParser, "overLimitParser");
   }

   /**
    * in current format, retryAt has a value of {@code 2012-11-14T21:51:28UTC}, which is an ISO-8601 seconds (not milliseconds) format.
    */
   @Inject
   public NovaErrorHandler(DateCodecFactory factory, OverLimitParser overLimitParser) {
      this(HeaderToRetryAfterException.create(Ticker.systemTicker(), factory.iso8601Seconds()), overLimitParser);
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);
      String content = data != null ? emptyToNull(new String(data)) : null;

      Exception exception = content != null ? new HttpResponseException(command, response, content)
            : new HttpResponseException(command, response);
      String requestLine = command.getCurrentRequest().getRequestLine();
      String message = content != null ? content : String.format("%s -> %s", requestLine, response.getStatusLine());
      switch (response.getStatusCode()) {
         case 400:
            if (message.indexOf("quota exceeded") != -1)
               exception = new InsufficientResourcesException(message, exception);
            else if (message.indexOf("has no fixed_ips") != -1)
               exception = new IllegalStateException(message, exception);
            else if (message.indexOf("already exists") != -1)
               exception = new IllegalStateException(message, exception);
            break;
         case 401:
         case 403:
            exception = new AuthorizationException(message, exception);
            break;
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
            }
            break;
         case 413:
            if (content == null) {
               exception = new InsufficientResourcesException(message, exception);
               break;
            }
            exception = parseAndBuildRetryException(content, message, exception);
      }
      command.setException(exception);
   }

   /**
    * Build an exception from the response. If it contains the JSON payload then
    * that is parsed to create a {@link RetryAfterException}, otherwise a
    * {@link InsufficientResourcesException} is returned
    * 
    */
   private Exception parseAndBuildRetryException(String json, String message, Exception exception) {
      Set<String> retryFields = ImmutableSet.of("retryAfter", "retryAt");
      for (String value : filterKeys(overLimitParser.apply(json), in(retryFields)).values()) {
         Optional<RetryAfterException> retryException = retryAfterParser.tryCreateRetryAfterException(exception, value);
         if (retryException.isPresent())
            return retryException.get();
      }
      return new InsufficientResourcesException(message, exception);
   }

}
