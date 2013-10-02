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

import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryGetQueueAttributes.html"
 *      />
 */
public class QueueAttributes {
   public static Builder<?> builder() {
      return new ConcreteBuilder();
   }

   public Builder<?> toBuilder() {
      return new ConcreteBuilder().fromQueueAttributes(this);
   }

   public abstract static class Builder<T extends Builder<T>> {
      protected abstract T self();

      protected String queueArn;
      protected long approximateNumberOfMessages;
      protected long approximateNumberOfMessagesNotVisible;
      protected long approximateNumberOfMessagesDelayed;
      protected int visibilityTimeout;
      protected Date createdTimestamp;
      protected Date lastModifiedTimestamp;
      protected Optional<String> rawPolicy = Optional.absent();
      protected int maximumMessageSize;
      protected int messageRetentionPeriod;
      protected int delaySeconds;

      /**
       * @see QueueAttributes#getQueueArn()
       */
      public T queueArn(String queueArn) {
         this.queueArn = queueArn;
         return self();
      }

      /**
       * @see QueueAttributes#getApproximateNumberOfMessages()
       */
      public T approximateNumberOfMessages(long approximateNumberOfMessages) {
         this.approximateNumberOfMessages = approximateNumberOfMessages;
         return self();
      }

      /**
       * @see QueueAttributes#getApproximateNumberOfMessagesNotVisible()
       */
      public T approximateNumberOfMessagesNotVisible(long approximateNumberOfMessagesNotVisible) {
         this.approximateNumberOfMessagesNotVisible = approximateNumberOfMessagesNotVisible;
         return self();
      }

      /**
       * @see QueueAttributes#getApproximateNumberOfMessagesDelayed()
       */
      public T approximateNumberOfMessagesDelayed(long approximateNumberOfMessagesDelayed) {
         this.approximateNumberOfMessagesDelayed = approximateNumberOfMessagesDelayed;
         return self();
      }

      /**
       * @see QueueAttributes#getVisibilityTimeout()
       */
      public T visibilityTimeout(int visibilityTimeout) {
         this.visibilityTimeout = visibilityTimeout;
         return self();
      }

      /**
       * @see QueueAttributes#getCreatedTimestamp()
       */
      public T createdTimestamp(Date createdTimestamp) {
         this.createdTimestamp = createdTimestamp;
         return self();
      }

      /**
       * @see QueueAttributes#getLastModifiedTimestamp()
       */
      public T lastModifiedTimestamp(Date lastModifiedTimestamp) {
         this.lastModifiedTimestamp = lastModifiedTimestamp;
         return self();
      }

      /**
       * @see QueueAttributes#getRawPolicy()
       */
      public T rawPolicy(String rawPolicy) {
         this.rawPolicy = Optional.fromNullable(rawPolicy);
         return self();
      }

      /**
       * @see QueueAttributes#getMaximumMessageSize()
       */
      public T maximumMessageSize(int maximumMessageSize) {
         this.maximumMessageSize = maximumMessageSize;
         return self();
      }

      /**
       * @see QueueAttributes#getMessageRetentionPeriod()
       */
      public T messageRetentionPeriod(int messageRetentionPeriod) {
         this.messageRetentionPeriod = messageRetentionPeriod;
         return self();
      }

      /**
       * @see QueueAttributes#getDelaySeconds()
       */
      public T delaySeconds(int delaySeconds) {
         this.delaySeconds = delaySeconds;
         return self();
      }

      public QueueAttributes build() {
         return new QueueAttributes(queueArn, approximateNumberOfMessages, approximateNumberOfMessagesNotVisible,
               approximateNumberOfMessagesDelayed, visibilityTimeout, createdTimestamp, lastModifiedTimestamp,
               rawPolicy, maximumMessageSize, messageRetentionPeriod, delaySeconds);
      }

      public T fromQueueAttributes(QueueAttributes in) {
         return queueArn(in.queueArn).approximateNumberOfMessages(in.approximateNumberOfMessages)
               .approximateNumberOfMessagesNotVisible(in.approximateNumberOfMessagesNotVisible)
               .approximateNumberOfMessagesDelayed(in.approximateNumberOfMessagesDelayed)
               .visibilityTimeout(in.visibilityTimeout).createdTimestamp(in.createdTimestamp)
               .lastModifiedTimestamp(in.lastModifiedTimestamp).rawPolicy(in.rawPolicy.orNull())
               .maximumMessageSize(in.maximumMessageSize).messageRetentionPeriod(in.messageRetentionPeriod)
               .delaySeconds(in.delaySeconds);

      }
   }

   private static class ConcreteBuilder extends Builder<ConcreteBuilder> {
      @Override
      protected ConcreteBuilder self() {
         return this;
      }
   }

   protected final long approximateNumberOfMessages;
   protected final long approximateNumberOfMessagesNotVisible;
   protected final int visibilityTimeout;
   protected final Date createdTimestamp;
   protected final Date lastModifiedTimestamp;
   protected final long approximateNumberOfMessagesDelayed;
   protected final Optional<String> rawPolicy;
   protected final int maximumMessageSize;
   protected final int messageRetentionPeriod;
   protected final String queueArn;
   protected int delaySeconds;

   protected QueueAttributes(String queueArn, long approximateNumberOfMessages,
         long approximateNumberOfMessagesNotVisible, long approximateNumberOfMessagesDelayed, int visibilityTimeout,
         Date createdTimestamp, Date lastModifiedTimestamp, Optional<String> rawPolicy, int maximumMessageSize,
         int messageRetentionPeriod, int delaySeconds) {
      this.queueArn = checkNotNull(queueArn, "queueArn");
      this.approximateNumberOfMessages = approximateNumberOfMessages;
      this.approximateNumberOfMessagesNotVisible = approximateNumberOfMessagesNotVisible;
      this.approximateNumberOfMessagesDelayed = approximateNumberOfMessagesDelayed;
      this.visibilityTimeout = visibilityTimeout;
      this.createdTimestamp = checkNotNull(createdTimestamp, "createdTimestamp of %s", queueArn);
      this.lastModifiedTimestamp = checkNotNull(lastModifiedTimestamp, "lastModifiedTimestamp of %s", queueArn);
      this.rawPolicy = checkNotNull(rawPolicy, "rawPolicy of %s", queueArn);
      this.maximumMessageSize = maximumMessageSize;
      this.messageRetentionPeriod = messageRetentionPeriod;
      this.delaySeconds = delaySeconds;
   }

   /**
    * @see Attribute#QUEUE_ARN
    */
   public String getQueueArn() {
      return queueArn;
   }

   /**
    * @see Attribute#APPROXIMATE_NUMBER_OF_MESSAGES
    */
   public long getApproximateNumberOfMessages() {
      return approximateNumberOfMessages;
   }

   /**
    * @see Attribute#APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE
    */
   public long getApproximateNumberOfMessagesNotVisible() {
      return approximateNumberOfMessagesNotVisible;
   }

   /**
    * @see Attribute#APPROXIMATE_NUMBER_OF_MESSAGES_DELAYED
    */
   public long getApproximateNumberOfMessagesDelayed() {
      return approximateNumberOfMessagesDelayed;
   }

   /**
    * @see Attribute#VISIBILITY_TIMEOUT
    */
   public int getVisibilityTimeout() {
      return visibilityTimeout;
   }

   /**
    * @see Attribute#CREATED_TIMESTAMP
    */
   public Date getCreatedTimestamp() {
      return createdTimestamp;
   }

   /**
    * @see Attribute#LAST_MODIFIED_TIMESTAMP
    */
   public Date getLastModifiedTimestamp() {
      return lastModifiedTimestamp;
   }

   /**
    * Note this is in raw Json
    * 
    * @see Attribute#POLICY
    */
   public Optional<String> getRawPolicy() {
      return rawPolicy;
   }

   /**
    * @see Attribute#MAXIMUM_MESSAGE_SIZE
    */
   public int getMaximumMessageSize() {
      return maximumMessageSize;
   }

   /**
    * @see Attribute#MESSAGE_RETENTION_PERIOD
    */
   public int getMessageRetentionPeriod() {
      return messageRetentionPeriod;
   }

   /**
    * @see Attribute#DELAY_SECONDS
    */
   public int getDelaySeconds() {
      return delaySeconds;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(queueArn);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      QueueAttributes other = (QueueAttributes) obj;
      return Objects.equal(this.queueArn, other.queueArn);

   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("queueArn", queueArn)
            .add("approximateNumberOfMessages", approximateNumberOfMessages)
            .add("approximateNumberOfMessagesNotVisible", approximateNumberOfMessagesNotVisible)
            .add("approximateNumberOfMessagesDelayed", approximateNumberOfMessagesDelayed)
            .add("visibilityTimeout", visibilityTimeout).add("createdTimestamp", createdTimestamp)
            .add("lastModifiedTimestamp", lastModifiedTimestamp).add("rawPolicy", rawPolicy.orNull())
            .add("maximumMessageSize", maximumMessageSize).add("messageRetentionPeriod", messageRetentionPeriod)
            .add("delaySeconds", delaySeconds).toString();
   }

}
