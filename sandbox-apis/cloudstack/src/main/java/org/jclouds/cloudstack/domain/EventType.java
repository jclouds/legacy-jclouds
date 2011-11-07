/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.cloudstack.domain;

import com.google.gson.annotations.SerializedName;

/**
 * @author Vijay Kiran
 */
public class EventType implements Comparable<EventType> {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String name;

      public Builder name(String name) {
         this.name = name;
         return this;
      }

      public EventType build() {
         return new EventType(name);
      }
   }

   // for deserialization
   EventType() {

   }

   @SerializedName("name")
   private String name;

   public EventType(String name) {
      this.name = name;
   }

   /**
    * @return the name/name of the OS type
    */
   public String getName() {
      return name;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
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
      EventType other = (EventType) obj;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + "]";
   }

   @Override
   public int compareTo(EventType arg0) {
      return name.compareTo(arg0.getName());
   }

}
