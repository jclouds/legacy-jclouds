/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.azurequeue.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

/**
 * 
 * @author Adrian Cole
 * 
 */
public class QueueMessage {
   private final String messageId;
   private final Date insertionTime;
   private final Date expirationTime;
   private final int dequeueCount;
   private final String popReceipt;
   private final Date timeNextVisible;
   private final String messageText;

   public QueueMessage(String messageId, Date insertionTime, Date expirationTime, int dequeueCount,
            String popReceipt, Date timeNextVisible, String messageText) {
      this.messageId = checkNotNull(messageId, "messageId");
      this.insertionTime = checkNotNull(insertionTime, "insertionTime");
      this.expirationTime = checkNotNull(expirationTime, "expirationTime");
      this.dequeueCount = dequeueCount;
      checkArgument(dequeueCount >= 0, "dequeueCount not set");
      this.popReceipt = checkNotNull(popReceipt, "popReceipt");
      this.timeNextVisible = checkNotNull(timeNextVisible, "timeNextVisible");
      this.messageText = checkNotNull(messageText, "messageText");
   }

   /**
    * The MessageID element is a GUID value that identifies the message in the queue. This value is
    * assigned to the message by the Queue service and is opaque to the client. This value may be
    * used together with the value of the PopReceipt element to delete a message from the queue
    * after it has been retrieved with the Get Messages operation.
    * 
    * 
    */
   public String getMessageId() {
      return messageId;
   }

   public Date getInsertionTime() {
      return insertionTime;
   }

   public Date getExpirationTime() {
      return expirationTime;
   }

   /**
    * DequeueCount element has a value of 1 the first time the message is dequeued. This value is
    * incremented each time the message is subsequently dequeued.
    * */
   public int getDequeueCount() {
      return dequeueCount;
   }

   /**
    * The value of PopReceipt is opaque to the client; its only purpose is to ensure that a message
    * may be deleted with the Delete Message operation.
    */
   public String getPopReceipt() {
      return popReceipt;
   }

   public Date getTimeNextVisible() {
      return timeNextVisible;
   }

   public String getMessageText() {
      return messageText;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + dequeueCount;
      result = prime * result + ((expirationTime == null) ? 0 : expirationTime.hashCode());
      result = prime * result + ((insertionTime == null) ? 0 : insertionTime.hashCode());
      result = prime * result + ((messageId == null) ? 0 : messageId.hashCode());
      result = prime * result + ((messageText == null) ? 0 : messageText.hashCode());
      result = prime * result + ((popReceipt == null) ? 0 : popReceipt.hashCode());
      result = prime * result + ((timeNextVisible == null) ? 0 : timeNextVisible.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      QueueMessage other = (QueueMessage) obj;
      if (dequeueCount != other.dequeueCount)
         return false;
      if (expirationTime == null) {
         if (other.expirationTime != null)
            return false;
      } else if (!expirationTime.equals(other.expirationTime))
         return false;
      if (insertionTime == null) {
         if (other.insertionTime != null)
            return false;
      } else if (!insertionTime.equals(other.insertionTime))
         return false;
      if (messageId == null) {
         if (other.messageId != null)
            return false;
      } else if (!messageId.equals(other.messageId))
         return false;
      if (messageText == null) {
         if (other.messageText != null)
            return false;
      } else if (!messageText.equals(other.messageText))
         return false;
      if (popReceipt == null) {
         if (other.popReceipt != null)
            return false;
      } else if (!popReceipt.equals(other.popReceipt))
         return false;
      if (timeNextVisible == null) {
         if (other.timeNextVisible != null)
            return false;
      } else if (!timeNextVisible.equals(other.timeNextVisible))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "QueueMessage [dequeueCount=" + dequeueCount + ", expirationTime=" + expirationTime
               + ", insertionTime=" + insertionTime + ", messageId=" + messageId + ", messageText="
               + messageText + ", popReceipt=" + popReceipt + ", timeNextVisible="
               + timeNextVisible + "]";
   }

}
