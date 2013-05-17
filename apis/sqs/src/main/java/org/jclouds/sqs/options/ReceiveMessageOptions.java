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
package org.jclouds.sqs.options;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * Options used to receive a message from a queue.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryReceiveMessage.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class ReceiveMessageOptions extends BaseHttpRequestOptions implements Cloneable {

   private Integer visibilityTimeout;
   private ImmutableSet.Builder<String> attributes = ImmutableSet.<String> builder();

   /**
    * The duration (in seconds) that the received messages are hidden from
    * subsequent retrieve requests after being retrieved by a ReceiveMessage
    * request.
    * 
    * @param visibilityTimeout
    *           Constraints: 0 to 43200 (maximum 12 hours)
    * 
    *           Default: The visibility timeout for the queue
    */
   public ReceiveMessageOptions visibilityTimeout(Integer visibilityTimeout) {
      this.visibilityTimeout = visibilityTimeout;
      return this;
   }

   /**
    * The attribute you want to get.
    * 
    * All - returns all values.
    * 
    * SenderId - returns the AWS account number (or the IP address, if anonymous
    * access is allowed) of the sender.
    * 
    * SentTimestamp - returns the time when the message was sent (epoch time in
    * milliseconds).
    * 
    * ApproximateReceiveCount - returns the number of times a message has been
    * received but not deleted.
    * 
    * ApproximateFirstReceiveTimestamp - returns the time when the message was
    * first received (epoch time in milliseconds).
    */
   public ReceiveMessageOptions attributes(Iterable<String> attributes) {
      this.attributes = ImmutableSet.<String> builder().addAll(attributes);
      return this;
   }

   /**
    * @see #attributes
    */
   public ReceiveMessageOptions attribute(String attribute) {
      this.attributes.add(attribute);
      return this;
   }

   public static class Builder {

      /**
       * @see ReceiveMessageOptions#visibilityTimeout
       */
      public static ReceiveMessageOptions visibilityTimeout(Integer visibilityTimeout) {
         return new ReceiveMessageOptions().visibilityTimeout(visibilityTimeout);
      }

      /**
       * @see ReceiveMessageOptions#attribute
       */
      public static ReceiveMessageOptions attribute(String attribute) {
         return new ReceiveMessageOptions().attribute(attribute);
      }

      /**
       * @see ReceiveMessageOptions#attributes
       */
      public static ReceiveMessageOptions attributes(Iterable<String> attributes) {
         return new ReceiveMessageOptions().attributes(attributes);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (visibilityTimeout != null)
         params.put("VisibilityTimeout", visibilityTimeout.toString());
      ImmutableSet<String> attributes = this.attributes.build();
      if (attributes.size() > 0) {
         int nameIndex = 1;
         for (String attribute : attributes) {
            params.put("AttributeName." + nameIndex, attribute);
            nameIndex++;
         }
      }
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(visibilityTimeout, attributes.build());
   }

   @Override
   public ReceiveMessageOptions clone() {
      return new ReceiveMessageOptions().visibilityTimeout(visibilityTimeout).attributes(attributes.build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      ReceiveMessageOptions other = ReceiveMessageOptions.class.cast(obj);
      return Objects.equal(this.visibilityTimeout, other.visibilityTimeout)
            && Objects.equal(this.attributes.build(), other.attributes.build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      ImmutableSet<String> attributes = this.attributes.build();
      return Objects.toStringHelper(this).omitNullValues().add("visibilityTimeout", visibilityTimeout)
            .add("attributes", attributes.size() > 0 ? attributes : null).toString();
   }
}
