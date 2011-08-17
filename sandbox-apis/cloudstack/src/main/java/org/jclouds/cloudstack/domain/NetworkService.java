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

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkService implements Comparable<NetworkService> {
   // internal only to match json type
   private static class Capability implements Comparable<Capability> {

      private String name;
      private String value;

      private Capability() {

      }

      private Capability(String name, String value) {
         this.name = name;
         this.value = value;
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((name == null) ? 0 : name.hashCode());
         result = prime * result + ((value == null) ? 0 : value.hashCode());
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
         NetworkService.Capability other = (NetworkService.Capability) obj;
         if (name == null) {
            if (other.name != null)
               return false;
         } else if (!name.equals(other.name))
            return false;
         if (value == null) {
            if (other.value != null)
               return false;
         } else if (!value.equals(other.value))
            return false;
         return true;
      }

      @Override
      public String toString() {
         return "[name=" + name + ", value=" + value + "]";
      }

      @Override
      public int compareTo(Capability o) {
         return name.compareTo(o.name);
      }

   }

   private String name;
   @SerializedName("capability")
   // so tests and serialization comes out expected
   private SortedSet<? extends NetworkService.Capability> capabilities = ImmutableSortedSet.of();

   NetworkService() {

   }

   public NetworkService(String name) {
      this(name, ImmutableMap.<String, String> of());
   }

   public NetworkService(String name, Map<String, String> capabilities) {
      this.name = checkNotNull(name, "name");
      ImmutableSortedSet.Builder<Capability> internal = ImmutableSortedSet.<Capability> naturalOrder();
      for (Entry<String, String> capabililty : checkNotNull(capabilities, "capabilities").entrySet())
         internal.add(new Capability(capabililty.getKey(), capabililty.getValue()));
      this.capabilities = internal.build();
   }

   public String getName() {
      return name;
   }

   public Map<String, String> getCapabilities() {
      // so tests and serialization comes out expected
      Builder<String, String> returnVal = ImmutableSortedMap.<String, String> naturalOrder();
      for (Capability capability : capabilities) {
         returnVal.put(capability.name, capability.value);
      }
      return returnVal.build();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((capabilities == null) ? 0 : capabilities.hashCode());
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
      NetworkService other = (NetworkService) obj;
      if (capabilities == null) {
         if (other.capabilities != null)
            return false;
      } else if (!capabilities.equals(other.capabilities))
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[name=" + name + ", capabilities=" + capabilities + "]";
   }

   @Override
   public int compareTo(NetworkService o) {
      return name.compareTo(o.getName());
   }
}