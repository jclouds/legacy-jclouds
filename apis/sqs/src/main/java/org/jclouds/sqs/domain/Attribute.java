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
package org.jclouds.sqs.domain;

/**
 * 
 * The action you want to allow for the specified principal.
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/acp-overview.html#PermissionTypes"
 *      />
 * @author Adrian Cole
 */
public interface Attribute {

   /**
    * approximate number of visible messages in a queue.
    */
   public static final String APPROXIMATE_NUMBER_OF_MESSAGES = "ApproximateNumberOfMessages";
   /**
    * approximate number of messages that are not timed-out and not deleted.
    */
   public static final String APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE = "ApproximateNumberOfMessagesNotVisible";

   /**
    * approximate number of messages that are not visible because you have set a
    * positive delay value on the queue
    */
   public static final String APPROXIMATE_NUMBER_OF_MESSAGES_DELAYED = "ApproximateNumberOfMessagesDelayed";

   /**
    * visibility timeout for the queue.
    */
   public static final String VISIBILITY_TIMEOUT = "VisibilityTimeout";

   /**
    * time when the queue was created (epoch time in seconds).
    */
   public static final String CREATED_TIMESTAMP = "CreatedTimestamp";

   /**
    * time when the queue was last changed (epoch time in seconds).
    */
   public static final String LAST_MODIFIED_TIMESTAMP = "LastModifiedTimestamp";

   /**
    * queue's policy.
    */
   public static final String POLICY = "Policy";

   /**
    * limit of how many bytes a message can contain before Amazon SQS rejects
    * it.
    */
   public static final String MAXIMUM_MESSAGE_SIZE = "MaximumMessageSize";

   /**
    * number of seconds Amazon SQS retains a message.
    */
   public static final String MESSAGE_RETENTION_PERIOD = "MessageRetentionPeriod";

   /**
    * queue's Amazon resource name (ARN).
    */
   public static final String QUEUE_ARN = "QueueArn";

   /**
    * The time in seconds that the delivery of all messages in the queue will be
    * delayed.
    */
   public static final String DELAY_SECONDS = "DelaySeconds";
}
