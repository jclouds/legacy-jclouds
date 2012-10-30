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
package org.jclouds.vcloud.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Strings2;
import org.jclouds.vcloud.VCloudResponseException;
import org.jclouds.vcloud.domain.VCloudError;
import org.jclouds.vcloud.domain.VCloudError.MinorCode;
import org.jclouds.vcloud.util.VCloudUtils;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseVCloudErrorFromHttpResponse implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;
   public static final Pattern RESOURCE_PATTERN = Pattern.compile(".*/v[^/]+/([^/]+)/([0-9]+)");
   private final VCloudUtils utils;

   @Inject
   public ParseVCloudErrorFromHttpResponse(VCloudUtils utils) {
      this.utils = utils;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      HttpRequest request = command.getCurrentRequest();
      Exception exception = new HttpResponseException(command, response);
      try {
         VCloudError error = null;
         String message = null;
         if (response.getPayload() != null) {
            try {
               error = utils.parseErrorFromContent(request, response);
               if (error != null) {
                  message = error.getMessage();
                  exception = new VCloudResponseException(command, response, error);
               } else {
                  message = Strings2.toString(response.getPayload());
                  exception = message != null ? new HttpResponseException(command, response, message) : exception;
               }
            } catch (IOException e) {
            } finally {
               response.getPayload().release();
            }
         }
         message = message != null ? message : String.format("%s -> %s", request.getRequestLine(), response
                  .getStatusLine());

         switch (response.getStatusCode()) {
            case 400:
               if (error != null
                        && ((error.getMinorErrorCode() != null && error.getMinorErrorCode() == MinorCode.BUSY_ENTITY)
                        || (error.getMessage() != null && error.getMessage().indexOf("is not running") != -1)))
                  exception = new IllegalStateException(message, exception);
               else
                  exception = new IllegalArgumentException(message, exception);
               break;
            case 401:
            case 403:
               if (error != null
                        && ((error.getMinorErrorCode() != null && error.getMinorErrorCode() == MinorCode.ACCESS_TO_RESOURCE_IS_FORBIDDEN)
                        || (error.getMessage() != null && error.getMessage().indexOf("No access to entity") != -1)))
                  exception = new ResourceNotFoundException(message, exception);
               else
                  exception = new AuthorizationException(exception.getMessage(), exception);
               break;
            case 404:
               if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
                  String path = command.getCurrentRequest().getEndpoint().getPath();
                  Matcher matcher = RESOURCE_PATTERN.matcher(path);
                  if (matcher.find()) {
                     message = String.format("%s %s not found", matcher.group(1), matcher.group(2));
                  } else {
                     message = path;
                  }
                  exception = new ResourceNotFoundException(message);
               }
               break;
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }
}
