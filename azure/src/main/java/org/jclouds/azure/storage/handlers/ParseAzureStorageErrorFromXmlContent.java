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

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.azure.storage.AzureStorageResponseException;
import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.util.AzureStorageUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpResponseException;
import org.jclouds.logging.Logger;
import org.jclouds.util.Utils;

import com.google.common.base.Throwables;

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

   public void handleError(HttpCommand command, HttpResponse response) {
      String content;
      try {
         content = response.getContent() != null ? Utils.toStringAndClose(response.getContent())
                  : null;
         if (content != null) {
            try {
               if (content.indexOf('<') >= 0) {
                  AzureStorageError error = utils.parseAzureStorageErrorFromContent(command,
                           response, content);
                  AzureStorageResponseException ex = new AzureStorageResponseException(command,
                           response, error);
                  if (error.getCode().equals("ContainerNotFound")) {
                     command.setException(new ContainerNotFoundException(ex));
                  } else {
                     command.setException(ex);
                  }
               } else {
                  command.setException(new HttpResponseException(command, response, content));
               }
            } catch (Exception he) {
               command.setException(new HttpResponseException(command, response, content));
               Throwables.propagateIfPossible(he);
            }
         } else {
            command.setException(new HttpResponseException(command, response));
         }
      } catch (Exception e) {
         command.setException(new HttpResponseException(command, response));
         Throwables.propagateIfPossible(e);
      }
   }

}