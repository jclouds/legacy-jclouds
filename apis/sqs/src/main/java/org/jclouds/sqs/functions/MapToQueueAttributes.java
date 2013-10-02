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
package org.jclouds.sqs.functions;

import java.util.Date;
import java.util.Map;

import org.jclouds.sqs.domain.Attribute;
import org.jclouds.sqs.domain.QueueAttributes;
import org.jclouds.sqs.domain.QueueAttributes.Builder;

import com.google.common.base.Function;

/**
 * Converts a Map to a typed QueueAttributes object
 * 
 * @author Adrian Cole
 */
public class MapToQueueAttributes implements Function<Map<String, String>, QueueAttributes> {

   @Override
   public QueueAttributes apply(Map<String, String> input) {
      if (input == null)
         return null;
      Builder<?> builder = QueueAttributes.builder();
      builder.queueArn(input.get(Attribute.QUEUE_ARN));
      builder.approximateNumberOfMessages(Long.parseLong(input.get(Attribute.APPROXIMATE_NUMBER_OF_MESSAGES)));
      builder.approximateNumberOfMessagesNotVisible(Long.parseLong(input
            .get(Attribute.APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE)));
      builder.approximateNumberOfMessagesDelayed(Long.parseLong(input
            .get(Attribute.APPROXIMATE_NUMBER_OF_MESSAGES_DELAYED)));
      builder.visibilityTimeout(Integer.parseInt(input.get(Attribute.VISIBILITY_TIMEOUT)));
      builder.createdTimestamp(new Date(Long.parseLong(input.get(Attribute.CREATED_TIMESTAMP))));
      builder.lastModifiedTimestamp(new Date(Long.parseLong(input.get(Attribute.LAST_MODIFIED_TIMESTAMP))));
      builder.rawPolicy(input.get(Attribute.POLICY));
      builder.maximumMessageSize(Integer.parseInt(input.get(Attribute.MAXIMUM_MESSAGE_SIZE)));
      builder.messageRetentionPeriod(Integer.parseInt(input.get(Attribute.MESSAGE_RETENTION_PERIOD)));
      builder.delaySeconds(Integer.parseInt(input.get(Attribute.DELAY_SECONDS)));
      return builder.build();
   }
}
