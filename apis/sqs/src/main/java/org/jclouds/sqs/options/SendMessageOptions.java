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
import com.google.common.collect.Multimap;

/**
 * Options used to send a message.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QuerySendMessage.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class SendMessageOptions extends BaseHttpRequestOptions implements Cloneable {

   private Integer delaySeconds;

   /**
    * The number of seconds to delay a specific message. Messages with a
    * positive DelaySeconds value become available for processing after the
    * delay time is finished. If you don't specify a value, the default value
    * for the queue applies.
    * 
    * @param delaySeconds
    *           from 0 to 900 (15 minutes). If this parameter is not used, the
    *           default value for the queue applies.
    */
   public SendMessageOptions delaySeconds(Integer delaySeconds) {
      this.delaySeconds = delaySeconds;
      return this;
   }

   public static class Builder {

      /**
       * @see SendMessageOptions#delaySeconds
       */
      public static SendMessageOptions delaySeconds(Integer delaySeconds) {
         return new SendMessageOptions().delaySeconds(delaySeconds);
      }

   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      if (delaySeconds != null)
         params.put("DelaySeconds", delaySeconds.toString());
      return params;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      return Objects.hashCode(delaySeconds);
   }

   @Override
   public SendMessageOptions clone() {
      return new SendMessageOptions().delaySeconds(delaySeconds);
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
      SendMessageOptions other = SendMessageOptions.class.cast(obj);
      return Objects.equal(this.delaySeconds, other.delaySeconds);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return Objects.toStringHelper(this).omitNullValues().add("delaySeconds", delaySeconds).toString();
   }
}
