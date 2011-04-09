/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.cloudstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.ImmutableSet;
import com.google.gson.annotations.SerializedName;

/**
 * 
 * @author Adrian Cole
 */
public class NetworkService {
   // internal only to match json type
   private static class Capability {

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

   }

   private String name;
   @SerializedName("capability")
   private Set<? extends NetworkService.Capability> capabilities = ImmutableSet.of();

   NetworkService() {

   }

   public NetworkService(String name) {
      this(name, ImmutableMap.<String, String> of());
   }

   public NetworkService(String name, Map<String, String> capabilities) {
      this.name = checkNotNull(name, "name");
      ImmutableSet.Builder<Capability> internal = ImmutableSet.<Capability> builder();
      for (Entry<String, String> capabililty : checkNotNull(capabilities, "capabilities").entrySet())
         internal.add(new Capability(capabililty.getKey(), capabililty.getValue()));
      this.capabilities = internal.build();
   }

   public String getName() {
      return name;
   }

   public Map<String, String> getCapabilities() {
      Builder<String, String> returnVal = ImmutableMap.<String, String> builder();
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
}