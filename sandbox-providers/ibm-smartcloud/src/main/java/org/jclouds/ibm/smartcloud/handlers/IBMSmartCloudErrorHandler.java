/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.ibm.smartcloud.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class IBMSmartCloudErrorHandler implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         // it is important to always read fully and close streams
         String message = parseMessage(response);
         exception = message != null ? new HttpResponseException(command, response, message) : exception;
         message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
               response.getStatusLine());
         switch (response.getStatusCode()) {
         case 401:
            exception = new AuthorizationException(exception.getMessage(), exception);
            break;
         case 402:
         case 403:
            exception = new AuthorizationException(message, exception);
            break;
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
            }
            break;
         case 406:
         case 409:
         case 412:
            exception = new IllegalStateException(message, exception);
            break;
         case 500:
            if (message != null && message.indexOf("There is already a BlockStorageDevice object with name") != 1)
               exception = new IllegalStateException(message, exception);
            break;
         }
      } catch (IOException e) {
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   public String parseMessage(HttpResponse response) throws IOException {
      if (response.getPayload() == null)
         return null;
      return Strings2.toStringAndClose(response.getPayload().getInput());
   }
}
