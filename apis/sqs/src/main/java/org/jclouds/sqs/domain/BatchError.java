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

/**
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/latest/APIReference/Query_QueryDeleteMessageBatch.html"
 *      >doc</a>
 * 
 * @author Adrian Cole
 */
public class BatchError {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromErrorEntry(this);
   }

   public static class Builder {

      private String id;
      private boolean senderFault;
      private String code;
      private String message;

      /**
       * @see BatchError#getId()
       */
      public Builder id(String id) {
         this.id = id;
         return this;
      }

      /**
       * @see BatchError#isSenderFault()
       */
      public Builder senderFault(boolean senderFault) {
         this.senderFault = senderFault;
         return this;
      }

      /**
       * @see BatchError#getCode()
       */
      public Builder code(String code) {
         this.code = code;
         return this;
      }

      /**
       * @see BatchError#getMessage()
       */
      public Builder message(String message) {
         this.message = message;
         return this;
      }

      public BatchError build() {
         return new BatchError(id, senderFault, code, message);
      }

      public Builder fromErrorEntry(BatchError in) {
         return id(in.getId()).senderFault(in.isSenderFault()).code(in.getCode()).message(in.getMessage());
      }
   }

   private final String id;
   private final boolean senderFault;
   private final String code;
   private final String message;

   private BatchError(String id, boolean senderFault, String code, String message) {
      this.id = checkNotNull(id, "id");
      this.senderFault = checkNotNull(senderFault, "senderFault of %s", id);
      this.code = checkNotNull(code, "code of %s", id);
      this.message = checkNotNull(message, "message of %s", id);
   }

   /**
    * The Id name that you assigned to the message.
    */
   public String getId() {
      return id;
   }

   /**
    * 
    */
   public boolean isSenderFault() {
      return senderFault;
   }

   /**
    * A short string description of the error.
    */
   public String getCode() {
      return code;
   }

   /**
    * A description of the error.
    */
   public String getMessage() {
      return message;
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
      BatchError that = BatchError.class.cast(obj);
      return Objects.equal(this.id, that.id);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("id", id).add("senderFault", senderFault)
            .add("message", message).add("code", code).toString();
   }

}
