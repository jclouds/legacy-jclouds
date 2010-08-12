/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef.handlers;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.chef.functions.ParseErrorFromJsonOrReturnBody;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ChefErrorHandler implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   private final ParseErrorFromJsonOrReturnBody errorParser;

   @Inject
   ChefErrorHandler(ParseErrorFromJsonOrReturnBody errorParser) {
      this.errorParser = errorParser;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      String message = errorParser.apply(response);
      Exception exception = new HttpResponseException(command, response, message);
      try {
         message = message != null ? message : String.format("%s -> %s", command.getRequest()
                  .getRequestLine(), response.getStatusLine());
         switch (response.getStatusCode()) {
            case 401:
            case 403:
               exception = new AuthorizationException(message, exception);
               break;
            case 404:
               if (!command.getRequest().getMethod().equals("DELETE")) {
                  exception = new ResourceNotFoundException(message, exception);
               }
               break;
         }
      } finally {
         if (response.getPayload() != null)
            Closeables.closeQuietly(response.getPayload().getInput());
         command.setException(exception);
      }
   }

}