/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import java.io.IOException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.aws.AWSResponseException;
import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ResourceNotFoundException;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AWSError
 * @author Adrian Cole
 * 
 */
public class ParseAWSErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AWSUtils utils;

   @Inject
   public ParseAWSErrorFromXmlContent(AWSUtils utils) {
      this.utils = utils;
   }

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = null;
      try {
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(command.getRequest().getRequestLine());
               break;
            case 404:
               String container = command.getRequest().getEndpoint().getHost();
               String key = command.getRequest().getEndpoint().getPath();
               if (key == null || key.equals("/"))
                  exception = new ContainerNotFoundException(container);
               else
                  exception = new KeyNotFoundException(container, key);
               break;
            default:
               if (response.getContent() != null) {
                  try {
                     String content = Utils.toStringAndClose(response.getContent());
                     if (content.indexOf('<') >= 0) {
                        AWSError error = utils.parseAWSErrorFromContent(command, response, content);
                        exception = new AWSResponseException(command, response, error);
                        if (error.getCode().indexOf("NotFound") >= 0)
                           exception = new ResourceNotFoundException(exception);
                     } else {
                        exception = new HttpResponseException(command, response, content);
                     }
                  } catch (IOException e) {
                     logger.warn(e, "exception reading error from response", response);
                  }
               }
         }
      } finally {
         Closeables.closeQuietly(response.getContent());
         command.setException(exception);
      }
   }
}