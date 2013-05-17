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
package org.jclouds.azure.storage.handlers;

import static org.jclouds.http.HttpUtils.releasePayload;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.util.AzureStorageUtils;
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

   public static final Pattern CONTAINER_PATH = Pattern.compile("^[/]?([^/]+)$");
   public static final Pattern CONTAINER_KEY_PATH = Pattern.compile("^[/]?([^/]+)/(.*)$");

   public void handleError(HttpCommand command, HttpResponse response) {
      Exception exception = new HttpResponseException(command, response);
      String message = null;
      AzureStorageError error = null;
      try {
         if (response.getPayload() != null) {
            String contentType = response.getPayload().getContentMetadata().getContentType();
            if (contentType != null && (contentType.indexOf("xml") != -1 || contentType.indexOf("unknown") != -1)
                     && !Long.valueOf(0).equals(response.getPayload().getContentMetadata().getContentLength())) {
               try {
                  error = utils.parseAzureStorageErrorFromContent(command, response, response.getPayload().getInput());
                  if (error != null) {
                     message = error.getMessage();
                     exception = new AzureStorageResponseException(command, response, error);
                  }
               } catch (RuntimeException e) {
                  try {
                     message = Strings2.toString(response.getPayload());
                     exception = new HttpResponseException(command, response, message);
                  } catch (IOException e1) {
                  }
               }
            } else {
               try {
                  message = Strings2.toString(response.getPayload());
                  exception = new HttpResponseException(command, response, message);
               } catch (IOException e) {
               }
            }
         }
         message = message != null ? message : String.format("%s -> %s", command.getCurrentRequest().getRequestLine(),
                  response.getStatusLine());
         exception = refineException(command, response, exception, error, message);
      } finally {
         releasePayload(response);
         command.setException(exception);
      }
   }

   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception,
            AzureStorageError error, String message) {
      switch (response.getStatusCode()) {
         case 401:
            exception = new AuthorizationException(message, exception);
            break;
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
            }
            break;
         case 411:
            exception = new IllegalArgumentException(message);
            break;
      }
      return exception;
   }

}
