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
package org.jclouds.sqs.handlers;

import static org.jclouds.sqs.config.SQSProperties.CREATE_QUEUE_MAX_RETRIES;
import static org.jclouds.sqs.config.SQSProperties.CREATE_QUEUE_RETRY_INTERVAL;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.inject.Named;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.handlers.AWSClientErrorRetryHandler;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.handlers.BackoffLimitedRetryHandler;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.util.concurrent.Uninterruptibles;
import com.google.inject.Inject;

/**
 * 
 * @author Adrian Cole
 */
public class SQSErrorRetryHandler extends AWSClientErrorRetryHandler {

   private final long retryInterval;
   private final int maxTries;

   @Inject
   public SQSErrorRetryHandler(AWSUtils utils, BackoffLimitedRetryHandler backoffLimitedRetryHandler,
         @ClientError Set<String> retryableCodes, @Named(CREATE_QUEUE_MAX_RETRIES) int maxTries,
         @Named(CREATE_QUEUE_RETRY_INTERVAL) long retryInterval) {
      super(utils, backoffLimitedRetryHandler, retryableCodes);
      this.maxTries = maxTries;
      this.retryInterval = retryInterval;
   }

   @VisibleForTesting
   public boolean shouldRetryRequestOnError(HttpCommand command, HttpResponse response, AWSError error) {
      if ("AWS.SimpleQueueService.QueueDeletedRecently".equals(error.getCode())) {
         if (command.incrementFailureCount() - 1 < maxTries) {
            Uninterruptibles.sleepUninterruptibly(retryInterval, TimeUnit.MILLISECONDS);
            return true;
         }
         return false;
      }
      return super.shouldRetryRequestOnError(command, response, error);
   }

}
