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
package org.jclouds.aws.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;
import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.InsufficientResourcesException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;

import com.google.common.annotations.VisibleForTesting;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AWSError
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseAWSErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   @VisibleForTesting
   final AWSUtils utils;

   @Inject
   public ParseAWSErrorFromXmlContent(AWSUtils utils) {
      this.utils = utils;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         AWSError error = null;
         // it is important to always read fully and close streams
         byte[] data = closeClientButKeepContentStream(response);
         String message = data != null ? new String(data) : null;
         if (response.getPayload() != null) {
            String contentType = response.getPayload().getContentMetadata().getContentType();
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("unknown") != -1)) {
               error = utils.parseAWSErrorFromContent(command.getCurrentRequest(), response);
               if (error != null) {
                  message = error.getMessage();
                  exception = new AWSResponseException(command, response, error);
               } else {
                  exception = new HttpResponseException(command, response, message);
               }
            } else {
               try {
                  message = Strings2.toString(response.getPayload());
                  exception = new HttpResponseException(command, response, message);
               } catch (IOException e) {
               }
            }
         }
         message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
                  response.getStatusLine());
         exception = refineException(command, response, exception, error, message);
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception, AWSError error,
            String message) {
      String errorCode = (error != null && error.getCode() != null) ? error.getCode() : null;
      switch (response.getStatusCode()) {
         case 400:
            if ("UnsupportedOperation".equals(errorCode))
               exception = new UnsupportedOperationException(message, exception);
            else if ("AddressLimitExceeded".equals(errorCode))
               exception = new InsufficientResourcesException(message, exception);
            else if (errorCode != null && (errorCode.indexOf("NotFound") != -1 || errorCode.endsWith(".Unknown")))
               exception = new ResourceNotFoundException(message, exception);
            else if ("IncorrectState".equals(errorCode)
                     || (errorCode != null && (error.getCode().endsWith(".Duplicate") | error.getCode().endsWith(
                              ".InUse")))
                     || (message != null && (message.indexOf("already exists") != -1 || message.indexOf("is in use") != -1)))
               exception = new IllegalStateException(message, exception);
            else if (errorCode != null && errorCode.indexOf("AuthFailure") != -1)
               exception = new AuthorizationException(message, exception);
            else if (message != null
                     && (message.indexOf("Invalid id") != -1 || message.indexOf("Failed to bind") != -1))
               exception = new IllegalArgumentException(message, exception);
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
         case 409:
            exception = new IllegalStateException(message, exception);
      }
      return exception;
   }

}
