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

package org.jclouds.azureblob.handlers;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.handlers.ParseAzureStorageErrorFromXmlContent;
import org.jclouds.azure.storage.util.AzureStorageUtils;
import org.jclouds.blobstore.ContainerNotFoundException;
import org.jclouds.blobstore.KeyNotFoundException;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseAzureBlobErrorFromXmlContent extends ParseAzureStorageErrorFromXmlContent {

   @Inject
   ParseAzureBlobErrorFromXmlContent(AzureStorageUtils utils) {
      super(utils);
   }

   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception, AzureStorageError error,
         String message) {
      switch (response.getStatusCode()) {
      case 404:
         if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
            exception = new ResourceNotFoundException(message, exception);
            String container = command.getCurrentRequest().getEndpoint().getHost();
            String key = command.getCurrentRequest().getEndpoint().getPath();
            if (key == null || key.equals("/"))
               exception = new ContainerNotFoundException(container, message);
            else
               exception = new KeyNotFoundException(container, key, message);
         }
         return exception;
      default:
         return super.refineException(command, response, exception, error, message);
      }
   }
}