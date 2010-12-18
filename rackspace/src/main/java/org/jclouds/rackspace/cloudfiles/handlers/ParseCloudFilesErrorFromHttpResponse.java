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

package org.jclouds.rackspace.cloudfiles.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Utils;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
public class ParseCloudFilesErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final String PREFIX = "^/v[0-9][^/]*/[a-zA-Z]+_[^/]+/";
   public static final Pattern CONTAINER_PATH = Pattern.compile(PREFIX + "([^/]+)$");
   public static final Pattern CONTAINER_KEY_PATH = Pattern.compile(PREFIX + "([^/]+)/(.*)");

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
            if (!command.getRequest().getMethod().equals("DELETE")) {
               String path = command.getRequest().getEndpoint().getPath();
               Matcher matcher = CONTAINER_PATH.matcher(path);
               if (matcher.find()) {
                  exception = new ContainerNotFoundException(matcher.group(1), content);
               } else {
                  matcher = CONTAINER_KEY_PATH.matcher(path);
                  if (matcher.find()) {
                     exception = new KeyNotFoundException(matcher.group(1), matcher.group(2), content);
                  }
               }
            }
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
            return Utils.toStringAndClose(response.getPayload().getInput());
         } catch (IOException e) {
            logger.warn(e, "exception reading error from response", response);
         }
      }
      return null;
   }
}
