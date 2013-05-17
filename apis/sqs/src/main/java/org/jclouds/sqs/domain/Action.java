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

import com.google.common.base.CaseFormat;

/**
 * 
 * The action you want to allow for the specified principal.
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/acp-overview.html#PermissionTypes"
 *      />
 * @author Adrian Cole
 */
public enum Action {
   /**
    * This permission type grants the following actions to a principal on a
    * shared queue: receive messages, send messages, delete messages, change a
    * message's visibility, get a queue's attributes.
    */
   ALL,
   /**
    * This grants permission to receive messages in the queue.
    */
   RECEIVE_MESSAGE,
   /**
    * This grants permission to send messages to the queue. SendMessageBatch
    * inherits permissions associated with SendMessage.
    */
   SEND_MESSAGE,
   /**
    * This grants permission to delete messages from the queue.
    * DeleteMessageBatch inherits permissions associated with DeleteMessage.
    */
   DELETE_MESSAGE,
   /**
    * This grants permission to extend or terminate the read lock timeout of a
    * specified message. ChangeMessageVisibilityBatch inherits permissions
    * associated with ChangeMessageVisibility. For more information about
    * visibility timeout, see Visibility Timeout. For more information about
    * this permission type, see the ChangeMessageVisibility operation.
    */
   CHANGE_MESSAGE_VISIBILITY,
   /**
    * This grants permission to receive all of the queue attributes except the
    * policy, which can only be accessed by the queue's owner. For more
    * information, see the GetQueueAttributes operation.
    */
   GET_QUEUE_ATTRIBUTES,
   /**
    * This grants permission to get the url of a queue by name.
    */
   GET_QUEUE_URL;

   public String value() {
      return this == ALL ? "*" : CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name());
   }

   @Override
   public String toString() {
      return value();
   }
}
