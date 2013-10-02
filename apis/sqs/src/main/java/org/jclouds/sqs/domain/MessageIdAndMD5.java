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

import com.google.common.base.Objects;
import com.google.common.hash.HashCode;

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/MessageLifecycle.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class MessageIdAndMD5 {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromMessage(this);
   }

   public static class Builder {

      private String id;
      private HashCode md5;

      /**
       * @see MessageIdAndMD5#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see MessageIdAndMD5#getMD5()
       */
      public Builder md5(HashCode md5) {
         this.md5 = md5;
         return this;
      }

      public MessageIdAndMD5 build() {
         return new MessageIdAndMD5(id, md5);
      }

      public Builder fromMessage(MessageIdAndMD5 in) {
         return id(in.getId()).md5(in.getMD5());
      }
   }

   private final String id;
   private final HashCode md5;

   private MessageIdAndMD5(String id, HashCode md5) {
      this.id = checkNotNull(id, "id");
      this.md5 = checkNotNull(md5, "md5 of %s", id);
   }

   /**
    * The message's SQS-assigned ID.
    */
   public String getId() {
      return id;
   }

   /**
    * An MD5 digest of the non-URL-encoded message body string
    */
   public HashCode getMD5() {
      return md5;
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
      MessageIdAndMD5 that = MessageIdAndMD5.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("md5", md5).toString();
   }

}
