/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
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
 */
package org.jclouds.ultradns.ws.handlers;
import static org.jclouds.ultradns.ws.handlers.UltraDNSWSErrorHandler.ErrorCodes.*;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.ultradns.ws.UltraDNSWSError;
import org.jclouds.ultradns.ws.UltraDNSWSExceptions.ResourceAlreadyExistsException;
import org.jclouds.ultradns.ws.UltraDNSWSResponseException;
import org.jclouds.ultradns.ws.xml.UltraWSExceptionHandler;

/**
 * @author Adrian Cole
 */
@Singleton
public class UltraDNSWSErrorHandler implements HttpErrorHandler {

   private final Factory factory;
   private final Provider<UltraWSExceptionHandler> handlers;

   @Inject
   UltraDNSWSErrorHandler(Factory factory, Provider<UltraWSExceptionHandler> handlers) {
      this.factory = factory;
      this.handlers = handlers;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data) : null;
         if (message != null) {
            exception = new HttpResponseException(command, response, message);
            String contentType = response.getPayload().getContentMetadata().getContentType();
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("unknown") != -1)) {
               UltraDNSWSError error = factory.create(handlers.get()).parse(message);
               if (error != null) {
                  exception = refineException(new UltraDNSWSResponseException(command, response, error));
               }
            }
         } else {
            exception = new HttpResponseException(command, response);
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   /**
    * there are 51002 potential codes. This defines the ones we are handling.
    */
   static final class ErrorCodes {
      static final int UNKNOWN = 0;
      /**
       * Zone does not exist in the system.
       */
      static final int ZONE_NOT_FOUND = 1801;
      /**
       * Zone already exists in the system.
       */
      static final int ZONE_ALREADY_EXISTS = 1802;
      /**
       * No resource record with GUID found in the system.
       */
      static final int RESOURCE_RECORD_NOT_FOUND = 2103;
      /**
       * Resource record exists with the same name and type.
       */
      static final int RESOURCE_RECORD_ALREADY_EXISTS = 2111;
      /**
       * Account not found in the system.
       */
      static final int ACCOUNT_NOT_FOUND = 2401;
      /**
       * Pool does not exist in the system.
       */
      static final int POOL_NOT_FOUND = 2911;
      /**
       * Pool already created for the given rrGUID.
       */
      static final int POOL_ALREADY_EXISTS = 2912;
      /**
       * Pool Record does not exist.
       */
      static final int POOL_RECORD_NOT_FOUND = 3101;
   }

   private Exception refineException(UltraDNSWSResponseException exception) {
      String message = exception.getError().getDescription().or(exception.getMessage());
      switch (exception.getError().getCode()) {
      case UNKNOWN:
         if (!exception.getError().getDescription().isPresent())
            return exception;
         if (exception.getError().getDescription().get().indexOf("Cannot find") == -1)
            return exception;
      case ZONE_NOT_FOUND:
      case RESOURCE_RECORD_NOT_FOUND:
      case ACCOUNT_NOT_FOUND:
      case POOL_NOT_FOUND:
      case POOL_RECORD_NOT_FOUND:
         return new ResourceNotFoundException(message, exception);
      case ZONE_ALREADY_EXISTS:
      case RESOURCE_RECORD_ALREADY_EXISTS:
      case POOL_ALREADY_EXISTS:
         return new ResourceAlreadyExistsException(message, exception);
      }
      return exception;
   }

}
