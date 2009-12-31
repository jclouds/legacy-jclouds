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

import javax.annotation.Resource;
import javax.inject.Named;

import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpConstants;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.HttpUtils;
import org.jclouds.logging.Logger;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class AtmosStorageClientErrorRetryHandler implements HttpRetryHandler {

   @Inject(optional = true)
   @Named(HttpConstants.PROPERTY_HTTP_MAX_RETRIES)
   private int retryCountLimit = 5;
   @Resource
   protected Logger logger = Logger.NULL;

   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      HttpUtils.closeClientButKeepContentStream(response);
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
         return true;
      }
      return false;
   }

}
