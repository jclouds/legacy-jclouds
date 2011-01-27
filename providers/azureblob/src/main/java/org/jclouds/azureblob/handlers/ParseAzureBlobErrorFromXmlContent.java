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

import java.util.List;

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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

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

   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception,
            AzureStorageError error, String message) {
      switch (response.getStatusCode()) {
         case 404:
            if (!command.getCurrentRequest().getMethod().equals("DELETE")) {
               exception = new ResourceNotFoundException(message, exception);
               List<String> parts = Lists.newArrayList(Splitter.on('/').split(
                        command.getCurrentRequest().getEndpoint().getPath()));
               parts.remove("");
               if (parts.size() > 0) {
                  String container = parts.remove(0);
                  String query = command.getCurrentRequest().getEndpoint().getQuery();
                  if (query != null && query.indexOf("container") != -1) {
                     exception = new ContainerNotFoundException(container, message);
                  } else {
                     exception = new KeyNotFoundException(container, Joiner.on('/').join(parts), message);
                  }
               }
            }
            return exception;
         default:
            return super.refineException(command, response, exception, error, message);
      }
   }
}