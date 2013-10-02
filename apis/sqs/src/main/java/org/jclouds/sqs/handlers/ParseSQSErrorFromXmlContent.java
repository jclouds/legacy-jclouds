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

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.aws.domain.AWSError;
import org.jclouds.aws.handlers.ParseAWSErrorFromXmlContent;
import org.jclouds.aws.util.AWSUtils;
import org.jclouds.http.HttpCommand;
import org.jclouds.http.HttpResponse;
import org.jclouds.rest.ResourceNotFoundException;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 * 
 */
@Singleton
public class ParseSQSErrorFromXmlContent extends ParseAWSErrorFromXmlContent {
   protected Set<String> resourceNotFoundCodes = ImmutableSet.of("AWS.SimpleQueueService.NonExistentQueue");
   protected Set<String> illegalStateCodes = ImmutableSet.of("AWS.SimpleQueueService.QueueDeletedRecently",
         "AWS.SimpleQueueService.QueueNameExists");
   protected Set<String> illegalArgumentCodes = ImmutableSet.of("InvalidAttributeName", "ReadCountOutOfRange",
         "InvalidMessageContents", "MessageTooLong");

   @Inject
   public ParseSQSErrorFromXmlContent(AWSUtils utils) {
      super(utils);
   }

   @Override
   protected Exception refineException(HttpCommand command, HttpResponse response, Exception exception, AWSError error,
         String message) {
      String errorCode = (error != null && error.getCode() != null) ? error.getCode() : null;
      if (resourceNotFoundCodes.contains(errorCode))
         exception = new ResourceNotFoundException(message, exception);
      else if (illegalStateCodes.contains(errorCode))
         exception = new IllegalStateException(message, exception);
      else if (illegalArgumentCodes.contains(errorCode))
         exception = new IllegalArgumentException(message, exception);
      else
         exception = super.refineException(command, response, exception, error, message);
      return exception;
   }
}
