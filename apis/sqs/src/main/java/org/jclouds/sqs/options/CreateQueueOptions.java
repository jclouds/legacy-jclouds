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

import java.util.Map;
import java.util.Map.Entry;

import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;

/**
 * Options used to receive a message from a queue.
 * 
 * @see <a
 *      href="http://docs.amazonwebservices.com/AWSSimpleQueueService/2011-10-01/APIReference/Query_QueryCreateQueue.html"
 *      >docs</a>
 * 
 * @author Adrian Cole
 */
public class CreateQueueOptions extends BaseHttpRequestOptions implements Cloneable {

   private ImmutableMap.Builder<String, String> attributes = ImmutableMap.<String, String> builder();

   /**
    * The duration (in seconds) that the received messages are hidden from
    * subsequent retrieve requests after being retrieved by a CreateQueue
    * request.
    * 
    * @param visibilityTimeout
    *           Constraints: 0 to 43200 (maximum 12 hours)
    * 
    *           Default: The visibility timeout for the queue
    */
   public CreateQueueOptions visibilityTimeout(int visibilityTimeout) {
      return attribute("VisibilityTimeout", visibilityTimeout + "");
   }

   /**
    */
   public CreateQueueOptions attributes(Map<String, String> attributes) {
      this.attributes = ImmutableMap.<String, String> builder().putAll(attributes);
      return this;
   }

   /**
    * @see #attributes
    */
   public CreateQueueOptions attribute(String name, String value) {
      this.attributes.put(name, value);
      return this;
   }

   public static class Builder {

      /**
       * @see CreateQueueOptions#visibilityTimeout
       */
      public static CreateQueueOptions visibilityTimeout(Integer visibilityTimeout) {
         return new CreateQueueOptions().visibilityTimeout(visibilityTimeout);
      }

      /**
       * @see CreateQueueOptions#attribute
       */
      public static CreateQueueOptions attribute(String name, String value) {
         return new CreateQueueOptions().attribute(name, value);
      }

      /**
       * @see CreateQueueOptions#attributes
       */
      public static CreateQueueOptions attributes(Map<String, String> attributes) {
         return new CreateQueueOptions().attributes(attributes);
      }
   }

   @Override
   public Multimap<String, String> buildFormParameters() {
      Multimap<String, String> params = super.buildFormParameters();
      ImmutableMap<String, String> attributes = this.attributes.build();
      if (attributes.size() > 0) {
         int nameIndex = 1;
         for (Entry<String, String> attribute : attributes.entrySet()) {
            params.put("Attribute." + nameIndex + ".Name", attribute.getKey());
            params.put("Attribute." + nameIndex + ".Value", attribute.getValue());
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
      return Objects.hashCode(attributes.build());
   }

   @Override
   public CreateQueueOptions clone() {
      return new CreateQueueOptions().attributes(attributes.build());
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
      CreateQueueOptions other = CreateQueueOptions.class.cast(obj);
      return Objects.equal(this.attributes.build(), other.attributes.build());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      ImmutableMap<String, String> attributes = this.attributes.build();
      return Objects.toStringHelper(this).omitNullValues().add("attributes", attributes.size() > 0 ? attributes : null)
            .toString();
   }
}
