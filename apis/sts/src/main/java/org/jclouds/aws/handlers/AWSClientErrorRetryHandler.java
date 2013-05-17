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
package org.jclouds.aws.handlers;

import static org.jclouds.http.HttpUtils.closeClientButKeepContentStream;

import java.util.Set;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.HttpRetryHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;

import com.google.inject.Inject;

/**
 * Handles Retryable responses with error codes in the 4xx range
 * 
 * @author Adrian Cole
 */
public class AWSClientErrorRetryHandler implements HttpRetryHandler {

   private final AWSUtils utils;
   private final BackoffLimitedRetryHandler backoffLimitedRetryHandler;
   private final Set<String> retryableCodes;

   @Inject
   public AWSClientErrorRetryHandler(AWSUtils utils, BackoffLimitedRetryHandler backoffLimitedRetryHandler,
         @ClientError Set<String> retryableCodes) {
      this.utils = utils;
      this.backoffLimitedRetryHandler = backoffLimitedRetryHandler;
      this.retryableCodes = retryableCodes;
   }

   @Override
   public boolean shouldRetryRequest(HttpCommand command, HttpResponse response) {
      if (response.getStatusCode() == 400 || response.getStatusCode() == 403 || response.getStatusCode() == 409) {
         // Content can be null in the case of HEAD requests
         if (response.getPayload() != null) {
            closeClientButKeepContentStream(response);
            AWSError error = utils.parseAWSErrorFromContent(command.getCurrentRequest(), response);
            if (error != null) {
               return shouldRetryRequestOnError(command, response, error);
            }
         }
      }
      return false;
   }

   protected boolean shouldRetryRequestOnError(HttpCommand command, HttpResponse response, AWSError error) {
      if (retryableCodes.contains(error.getCode()))
         return backoffLimitedRetryHandler.shouldRetryRequest(command, response);
      return false;
   }

}
