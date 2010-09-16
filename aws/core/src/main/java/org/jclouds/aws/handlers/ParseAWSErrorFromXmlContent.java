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

package org.jclouds.aws.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Utils;

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
   ParseAWSErrorFromXmlContent(AWSUtils utils) {
      this.utils = utils;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      HttpRequest request = command.getRequest();
      Exception exception = new HttpResponseException(command, response);
      try {
         AWSError error = null;
         String message = null;
         if (response.getPayload() != null) {
            if (response.getPayload().getContentType() != null
                     && (response.getPayload().getContentType().indexOf("xml") != -1 || response.getPayload()
                              .getContentType().indexOf("unknown") != -1)) {
               error = utils.parseAWSErrorFromContent(request, response);
               if (error != null) {
                  message = error.getMessage();
                  exception = new AWSResponseException(command, response, error);
               }
            } else {
               try {
                  message = Utils.toStringAndClose(response.getPayload().getInput());
               } catch (IOException e) {
               }
            }
         }
         message = message != null ? message : String.format("%s -> %s", request.getRequestLine(), response
                  .getStatusLine());
         switch (response.getStatusCode()) {
            case 400:
               if (error != null && error.getCode() != null
                        && (error.getCode().endsWith(".NotFound") || error.getCode().endsWith(".Unknown")))
                  exception = new ResourceNotFoundException(message, exception);
               else if ((error != null && error.getCode() != null && (error.getCode().equals("IncorrectState") || error
                        .getCode().equals("InvalidGroup.Duplicate")))
                        || (message != null && message.indexOf("already exists") != -1))
                  exception = new IllegalStateException(message, exception);
               else if (error != null && error.getCode() != null && error.getCode().equals("AuthFailure"))
                  exception = new AuthorizationException(command.getRequest(), message);
               else if (message != null && message.indexOf("Failed to bind the following fields") != -1)// Nova
                  exception = new IllegalArgumentException(message, exception);
               break;
            case 401:
            case 403:
               exception = new AuthorizationException(command.getRequest(), message);
               break;
            case 404:
               if (!command.getRequest().getMethod().equals("DELETE")) {
                  String container = request.getEndpoint().getHost();
                  String key = request.getEndpoint().getPath();
                  if (key == null || key.equals("/"))
                     exception = new ContainerNotFoundException(container, message);
                  else
                     exception = new KeyNotFoundException(container, key, message);
               }
               break;
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

}