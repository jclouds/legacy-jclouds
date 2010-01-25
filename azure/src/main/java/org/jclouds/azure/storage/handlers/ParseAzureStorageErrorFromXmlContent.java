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
package org.jclouds.azure.storage.handlers;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.util.AzureStorageUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.util.Utils;

import com.google.common.io.Closeables;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AzureStorageError
 * @author Adrian Cole
 * 
 */
public class ParseAzureStorageErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AzureStorageUtils utils;

   @Inject
   public ParseAzureStorageErrorFromXmlContent(AzureStorageUtils utils) {
      this.utils = utils;
   }

   public static final Pattern CONTAINER_PATH = Pattern.compile("^[/]?([^/]+)");
   public static final Pattern CONTAINER_KEY_PATH = Pattern.compile("^[/]?([^/]+)/(.*)");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(command.getRequest().getRequestLine());
               break;
            case 404:
               if (!command.getRequest().getMethod().equals("DELETE")) {
                  String path = command.getRequest().getEndpoint().getPath();
                  Matcher matcher = CONTAINER_PATH.matcher(path);
                  if (matcher.find()) {
                     exception = new ContainerNotFoundException(matcher.group(1));
                  } else {
                     matcher = CONTAINER_KEY_PATH.matcher(path);
                     if (matcher.find()) {
                        exception = new KeyNotFoundException(matcher.group(1), matcher.group(2));
                     }
                  }
               }
               break;
            default:
               if (response.getContent() != null) {
                  try {
                     String content = Utils.toStringAndClose(response.getContent());
                     if (content.indexOf('<') >= 0) {
                        AzureStorageError error = utils.parseAzureStorageErrorFromContent(command,
                                 response, Utils.toInputStream(content));
                        exception = new AzureStorageResponseException(command, response, error);

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