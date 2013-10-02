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
package org.jclouds.cloudservers.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

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
public class ParseCloudServersErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final Pattern RESOURCE_PATTERN = Pattern
            .compile("^/v1[^/]*/[0-9]+/([^/]+)/([0-9]+)");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         String content = parseErrorFromContentOrNull(command, response);
         exception = content != null ? new HttpResponseException(command, response, content) : exception;
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(exception.getMessage(), exception);
               break;
            case 404:
               if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
                  String path = command.getCurrentRequest().getEndpoint().getPath();
                  Matcher matcher = RESOURCE_PATTERN.matcher(path);
                  String message;
                  if (matcher.find()) {
                     message = String.format("%s %s not found", matcher.group(1), matcher.group(2));
                  } else {
                     message = path;
                  }
                  exception = new ResourceNotFoundException(message);
               }
               break;
            case 409:
               exception = new IllegalStateException(content);
               break;
            default:
               exception = new HttpResponseException(command, response, content);
               break;
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   String parseErrorFromContentOrNull(HttpCommand command, HttpResponse response) {
      if (response.getPayload() != null) {
         try {
            return Strings2.toString(response.getPayload());
         } catch (IOException e) {
            logger.warn(e, "exception reading error from response", response);
         }
      }
      return null;
   }
}
