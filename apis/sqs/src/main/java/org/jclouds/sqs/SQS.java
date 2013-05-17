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
package org.jclouds.sqs;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.jclouds.collect.AdvanceUntilEmptyIterable;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.sqs.domain.Message;
import org.jclouds.sqs.features.MessageApi;
import org.jclouds.sqs.options.ReceiveMessageOptions;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;

/**
 * Utilities for interacting with SQS
 * 
 * @author Adrian Cole
 */
@Beta
public class SQS {

   /**
    * Returns an iterable that lazy fetches messages until there are none left.
    * Note that this method will make multiple network calls.
    * 
    * @param api
    *           api targeted at the queue in question
    * @param messagesPerPage
    *           how many messages to receive per request (current max: 10)
    * @param options
    *           controls attributes and visibility options
    * @return an iterable that lazy fetches messages until there are none left
    */
   public static FluentIterable<Message> receiveAllAtRate(MessageApi api, int messagesPerPage,
         ReceiveMessageOptions options) {
      return AdvanceUntilEmptyIterable.create(new MoreMessages(api, messagesPerPage, options)).concat();
   }

   /**
    * returns another response of messages on {@link MoreMessages#get}
    * 
    */
   private static class MoreMessages implements Supplier<FluentIterable<Message>> {

      private static final ReceiveMessageOptions NO_OPTIONS = new ReceiveMessageOptions();
      private MessageApi api;
      private int max;
      private ReceiveMessageOptions options;

      private MoreMessages(MessageApi api, int max, @Nullable ReceiveMessageOptions options) {
         this.api = checkNotNull(api, "message api");
         checkState(max > 0, "max messages per request must be a positive number");
         this.max = max;
         this.options = Optional.fromNullable(options).or(NO_OPTIONS);
      }

      @Override
      public FluentIterable<Message> get() {
         return api.receive(max, options);
      }

   }
}
