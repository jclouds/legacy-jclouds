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

package org.jclouds.atmosonline.saas.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;

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
import org.jclouds.util.Strings2;

/**
 * This will parse and set an appropriate exception on the command object.
 * 
 * @see AtmosStorageError
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseAtmosStorageErrorFromXmlContent implements HttpErrorHandler {
   @Resource
   protected Logger logger = Logger.NULL;

   private final AtmosStorageUtils utils;

   @Inject
   public ParseAtmosStorageErrorFromXmlContent(AtmosStorageUtils utils) {
      this.utils = utils;
   }

   public static final Pattern DIRECTORY_PATH = Pattern.compile("^/rest/namespace/?([^/]+)/$");
   public static final Pattern DIRECTORY_KEY_PATH = Pattern.compile("^/rest/namespace/?([^/]+)/(.*)");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      try {
         AtmosStorageError error = null;
         if (response.getPayload() != null) {
            try {
               String content = Strings2.toStringAndClose(response.getPayload().getInput());
               if (content != null && content.indexOf('<') >= 0) {
                  error = utils.parseAtmosStorageErrorFromContent(command, response, Strings2.toInputStream(content));
               } else {
                  exception = content != null ? new HttpResponseException(command, response, content) : exception;
               }
            } catch (IOException e) {
               logger.warn(e, "exception reading error from response", response);
            }
         }
         if (error != null && error.getCode() == 1016) {
            File file = new File(command.getCurrentRequest().getEndpoint().getPath());
            exception = new KeyAlreadyExistsException(file.getParentFile().getAbsolutePath(), file.getName());
         } else {
            switch (response.getStatusCode()) {
            case 401:
               exception = new AuthorizationException(exception.getMessage(), exception);
               break;
            case 404:
               if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
                  String message = error != null ? error.getMessage() : String.format("%s -> %s", command.getCurrentRequest()
                        .getRequestLine(), response.getStatusLine());
                  String path = command.getCurrentRequest().getEndpoint().getPath();
                  Matcher matcher = DIRECTORY_PATH.matcher(path);
                  if (matcher.find()) {
                     exception = new ContainerNotFoundException(matcher.group(1), message);
                  } else {
                     matcher = DIRECTORY_KEY_PATH.matcher(path);
                     if (matcher.find()) {
                        exception = new KeyNotFoundException(matcher.group(1), matcher.group(2), message);
                     }
                  }
               }
               break;
            default:
               exception = error != null ? new AtmosStorageResponseException(command, response, error)
                     : new HttpResponseException(command, response);

            }
         }
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

}