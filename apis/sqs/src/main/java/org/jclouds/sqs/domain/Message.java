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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/MessageLifecycle.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class Message {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromMessage(this);
   }

   public static class Builder {

      private String id;
      private String body;
      private String receiptHandle;
      private HashCode md5;
      private ImmutableMap.Builder<String, String> attributes = ImmutableMap.<String, String> builder();

      /**
       * @see Message#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see Message#getBody()
       */
      public Builder body(String body) {
         this.body = body;
         return this;
      }

      /**
       * @see Message#getReceiptHandle()
       */
      public Builder receiptHandle(String receiptHandle) {
         this.receiptHandle = receiptHandle;
         return this;
      }

      /**
       * @see Message#getMD5()
       */
      public Builder md5(HashCode md5) {
         this.md5 = md5;
         return this;
      }

      /**
       * @see Message#getAttributes()
       */
      public Builder attributes(Map<String, String> attributes) {
         this.attributes.putAll(checkNotNull(attributes, "attributes"));
         return this;
      }

      /**
       * @see Message#getAttributes()
       */
      public Builder addAttribute(String name, String value) {
         this.attributes.put(checkNotNull(name, "name"), checkNotNull(value, "value"));
         return this;
      }

      public Message build() {
         return new Message(id, body, receiptHandle, md5, attributes.build());
      }

      public Builder fromMessage(Message in) {
         return id(in.getId()).body(in.getBody()).receiptHandle(in.getReceiptHandle()).md5(in.getMD5())
               .attributes(in.getAttributes());
      }
   }

   private final String id;
   private final String body;
   private final String receiptHandle;
   private final HashCode md5;
   private final Map<String, String> attributes;

   private Message(String id, String body, String receiptHandle, HashCode md5, Map<String, String> attributes) {
      this.id = checkNotNull(id, "id");
      this.body = checkNotNull(body, "body of %s", id);
      this.receiptHandle = checkNotNull(receiptHandle, "receiptHandle of %s", id);
      this.md5 = checkNotNull(md5, "md5 of %s", id);
      this.attributes = ImmutableMap.copyOf(checkNotNull(attributes, "attributes of %s", id));
   }

   /**
    * The message's SQS-assigned ID.
    */
   public String getId() {
      return id;
   }

   /**
    * The message's contents (not URL encoded)
    */
   public String getBody() {
      return body;
   }

   /**
    * A string associated with a specific instance of receiving the message.
    */
   public String getReceiptHandle() {
      return receiptHandle;
   }

   /**
    * An MD5 digest of the non-URL-encoded message body string
    */
   public HashCode getMD5() {
      return md5;
   }

   /**
    * Attributes of the queue
    */
   public Map<String, String> getAttributes() {
      return attributes;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(id);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      Message that = Message.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("body", body).add("md5", md5)
            .add("receiptHandle", receiptHandle).add("attributes", attributes).toString();
   }

}
