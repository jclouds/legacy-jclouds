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
package org.jclouds.openstack.swift.v1.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Singleton;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.rest.AuthorizationException;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
// TODO: is there error spec someplace? let's type errors, etc.
@Singleton
public class SwiftErrorHandler implements HttpErrorHandler {
   public static final String PREFIX = "^/v[0-9][^/]*/[a-zA-Z]+_[^/]+/";
   public static final Pattern CONTAINER_PATH = Pattern.compile(PREFIX + "([^/]+)$");
   public static final Pattern CONTAINER_KEY_PATH = Pattern.compile(PREFIX + "([^/]+)/(.*)");

   public void handleError(HttpCommand command, HttpResponse response) {
      // it is important to always read fully and close streams
      byte[] data = closeClientButKeepContentStream(response);
      String message = data != null ? new String(data) : null;

      Exception exception = message != null ? new HttpResponseException(command, response, message)
               : new HttpResponseException(command, response);
      message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
               response.getStatusLine());
      switch (response.getStatusCode()) {
         case 401:
            exception = new AuthorizationException(exception.getMessage(), exception);
            break;
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               String path = command.getCurrentRequest().getEndpoint().getPath();
               Matcher matcher = CONTAINER_PATH.matcher(path);
               Exception oldException = exception;
               if (matcher.find()) {
                  exception = new ContainerNotFoundException(matcher.group(1), message);
                  exception.initCause(oldException);
               } else {
                  matcher = CONTAINER_KEY_PATH.matcher(path);
                  if (matcher.find()) {
                     exception = new KeyNotFoundException(matcher.group(1), matcher.group(2), message);
                     exception.initCause(oldException);
                  }
               }
            }
            break;
      }
      command.setException(exception);
   }
}
