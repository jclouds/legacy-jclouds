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

import java.io.ByteArrayInputStream;

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.Constants;
import org.jclouds.azure.storage.domain.AzureStorageError;
import org.jclouds.azure.storage.util.AzureStorageUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpException;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.HttpUtils;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class AzureStorageClientErrorRetryHandler implements HttpRetryHandler {

   @Inject(optional = true)
   @Named(Constants.PROPERTY_MAX_RETRIES)
   private int retryCountLimit = 5;

   private final AzureStorageUtils utils;
   private final BackoffLimitedRetryHandler backoffHandler;

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject
   public AzureStorageClientErrorRetryHandler(BackoffLimitedRetryHandler backoffHandler,
            AzureStorageUtils utils) {
      this.backoffHandler = backoffHandler;
      this.utils = utils;
   }

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      byte[] content = HttpUtils.closeClientButKeepContentStream(response);
      command.incrementFailureCount();
      if (!command.isReplayable()) {
         logger.warn("Cannot retry after server error, command is not replayable: %1$s", command);
         return false;
      } else if (command.getFailureCount() > retryCountLimit) {
         logger.warn(
                  "Cannot retry after server error, command has exceeded retry limit %1$d: %2$s",
                  retryCountLimit, command);
         return false;
      } else if (response.getStatusCode() == 409) {
         // Content can be null in the case of HEAD requests
         if (content != null) {
            try {
               AzureStorageError error = utils.parseAzureStorageErrorFromContent(command, response,
                        new ByteArrayInputStream(content));
               if ("ContainerBeingDeleted".equals(error.getCode())) {
                  backoffHandler.imposeBackoffExponentialDelay(100L, 3, retryCountLimit, command
                           .getFailureCount(), command.toString());
                  return true;
               }
            } catch (HttpException e) {
               logger.warn(e, "error parsing response: %s", new String(content));
            }
         }
      }
      return false;
   }

}
