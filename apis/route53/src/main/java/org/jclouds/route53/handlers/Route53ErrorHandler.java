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
package org.jclouds.route53.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.xml.ErrorHandler;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.route53.InvalidChangeBatchException;
import org.jclouds.route53.xml.InvalidChangeBatchHandler;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Singleton
public class Route53ErrorHandler implements HttpErrorHandler {

   private final Factory factory;
   private final Provider<ErrorHandler> handlers;
   private final Provider<InvalidChangeBatchHandler> batchHandlers;

   @Inject
   Route53ErrorHandler(Factory factory, Provider<ErrorHandler> handlers,
         Provider<InvalidChangeBatchHandler> batchHandlers) {
      this.factory = factory;
      this.handlers = handlers;
      this.batchHandlers = batchHandlers;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data) : null;
         if (message != null) {
            exception = new HttpResponseException(command, response, message);
            if (message.indexOf("ErrorResponse") != -1) {
               AWSError error = factory.create(handlers.get()).parse(message);
               exception = refineException(new AWSResponseException(command, response, error));
            } else if (message.indexOf("InvalidChangeBatch") != -1) {
               ImmutableList<String> errors = factory.create(batchHandlers.get()).parse(message);
               exception = new InvalidChangeBatchException(errors, new HttpResponseException(command, response));
            }
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   private Exception refineException(AWSResponseException in) {
      int statusCode = in.getResponse().getStatusCode();
      String errorCode = in.getError().getCode();
      String message = in.getError().getMessage();

      if (statusCode == 403 || "RequestExpired".equals(errorCode))
         return new AuthorizationException(message, in);
      if (statusCode == 400) {
         if (ImmutableSet.of("InvalidAction", "AccessDenied").contains(errorCode))
            return new UnsupportedOperationException(message, in);
         else if ("Throttling".equals(errorCode))
            return new InsufficientResourcesException(message, in);
         else if (message.indexOf("not found") != -1)
            return new ResourceNotFoundException(message, in);
         return new IllegalArgumentException(message, in);
      }
      return in;
   }
}
