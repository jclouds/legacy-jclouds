/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.handlers;

import static javax.ws.rs.core.Response.Status.fromStatusCode;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.functions.ParseErrors;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import com.abiquo.model.transport.error.ErrorsDto;
import com.google.common.io.Closeables;

/**
 * Parse Abiquo API errors and set the appropriate exception.
 * 
 * @author Ignasi Barrera
 */
@Singleton
public class AbiquoErrorHandler implements HttpErrorHandler {
   /** The error parser. */
   private ParseErrors errorParser;

   @Inject
   AbiquoErrorHandler(final ParseErrors errorParser) {
      super();
      this.errorParser = errorParser;
   }

   @Override
   public void handleError(final HttpCommand command, final HttpResponse response) {
      Exception exception = null;
      String defaultMessage = String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
            response.getStatusLine());

      try {
         switch (response.getStatusCode()) {
            case 401:
            case 403:
               // Authorization exceptions do not return an errors DTO, so we
               // encapsulate a
               // generic exception
               exception = new AuthorizationException(defaultMessage, new HttpResponseException(command, response,
                     defaultMessage));
               break;
            case 404:
               exception = new ResourceNotFoundException(defaultMessage, getExceptionToPropagate(command, response,
                     defaultMessage));
               break;
            case 301:
               // Moved resources in Abiquo should be handled with the
               // ReturnMovedResource
               // exception parser to return the moved entity.
               exception = new HttpResponseException(command, response, defaultMessage);
               break;
            default:
               exception = getExceptionToPropagate(command, response, defaultMessage);
               break;
         }
      } finally {
         Closeables.closeQuietly(response.getPayload());
         command.setException(exception);
      }
   }

   private Exception getExceptionToPropagate(final HttpCommand command, final HttpResponse response,
         final String defaultMessage) {
      Exception exception = null;

      if (hasPayload(response)) {
         try {
            ErrorsDto errors = errorParser.apply(response);
            exception = new AbiquoException(fromStatusCode(response.getStatusCode()), errors);
         } catch (Exception ex) {
            // If it is not an Abiquo Exception (can not be unmarshalled),
            // propagate a standard
            // HttpResponseException
            exception = new HttpResponseException(command, response, defaultMessage);
         }
      } else {
         // If it is not an Abiquo Exception (there is not an errors xml in the
         // payload)
         // propagate a standard HttpResponseException
         exception = new HttpResponseException(command, response, defaultMessage);
      }

      return exception;
   }

   private static boolean hasPayload(final HttpResponse response) {
      return response.getPayload() != null && response.getPayload().getContentMetadata() != null
            && response.getPayload().getContentMetadata().getContentLength() != null
            && response.getPayload().getContentMetadata().getContentLength() > 0L;
   }
}
