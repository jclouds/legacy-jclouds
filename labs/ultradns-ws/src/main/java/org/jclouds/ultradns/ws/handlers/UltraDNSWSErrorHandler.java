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

   private Exception refineException(UltraDNSWSResponseException exception) {
      switch (exception.getError().getCode()) {
      case 0:
      case 1801:
      case 2401:
         return new ResourceNotFoundException(exception.getError().getDescription(), exception);
      }
      return exception;
   }

}
