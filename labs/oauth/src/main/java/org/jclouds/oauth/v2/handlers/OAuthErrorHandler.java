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
package org.jclouds.oauth.v2.handlers;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;

import javax.inject.Singleton;

import static javax.ws.rs.core.Response.Status;
import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

/**
 * This will parse and set an appropriate exception on the command object.
 *
 * @author David Alves
 */
@Singleton
public class OAuthErrorHandler implements HttpErrorHandler {
   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);
      String message = data != null ? new String(data) : null;

      Exception exception = message != null ? new HttpResponseException(command, response, message)
              : new HttpResponseException(command, response);
      message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
              response.getStatusLine());
      Status status = Status.fromStatusCode(response.getStatusCode());
      switch (status) {
         case BAD_REQUEST:
            break;
         case UNAUTHORIZED:
         case FORBIDDEN:
            exception = new AuthorizationException(message, exception);
            break;
         case NOT_FOUND:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
            }
            break;
         case CONFLICT:
            exception = new IllegalStateException(message, exception);
            break;
      }
      command.setException(exception);
   }
}
