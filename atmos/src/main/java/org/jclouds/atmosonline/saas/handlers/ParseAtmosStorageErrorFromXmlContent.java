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
package org.jclouds.atmosonline.saas.handlers;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.atmosonline.saas.AtmosStorageResponseException;
import org.jclouds.atmosonline.saas.domain.AtmosStorageError;
import org.jclouds.atmosonline.saas.util.AtmosStorageUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyAlreadyExistsException;
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
 * @see AtmosStorageError
 * @author Adrian Cole
 * 
 */
public class ParseAtmosStorageErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AtmosStorageUtils utils;

   @Inject
   public ParseAtmosStorageErrorFromXmlContent(AtmosStorageUtils utils) {
      this.utils = utils;
   }

   public static final Pattern CONTAINER_PATH = Pattern.compile("^/rest/namespace/?([^/]+)[/]?$");
   public static final Pattern CONTAINER_KEY_PATH = Pattern
            .compile("^/rest/namespace/?([^/]+)/(.*)");

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
                        AtmosStorageError error = utils.parseAtmosStorageErrorFromContent(command,
                                 response, content);
                        if (error.getCode() == 1016) {
                           File file = new File(command.getRequest().getEndpoint().getPath());
                           exception = new KeyAlreadyExistsException(file.getParentFile()
                                    .getAbsolutePath(), file.getName());
                        } else {
                           exception = new AtmosStorageResponseException(command, response, error);
                        }
                     }
                  } catch (IOException e) {
                     logger.warn(e, "exception reading error from response", response);
                     exception = new HttpResponseException(command, response);
                  }
               }
         }
      } finally {
         Closeables.closeQuietly(response.getContent());
         command.setException(exception);
      }
   }
}