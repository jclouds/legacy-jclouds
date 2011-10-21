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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The power state class provides a common set of values for which a guest's power state will be presented in the SoftLayer API. 
 * @author Jason King
    * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Virtual_Guest_Power_State"
 *      />
 */
public class PowerState implements Comparable<PowerState> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private VirtualGuest.State keyName;

      public Builder keyName(VirtualGuest.State keyName) {
         this.keyName = keyName;
         return this;
      }

      public PowerState build() {
         return new PowerState(keyName);
      }

      public static Builder fromAddress(PowerState in) {
         return PowerState.builder().keyName(in.getKeyName());
      }
   }

   private VirtualGuest.State keyName;

   // for deserializer
   PowerState() {

   }

   public PowerState(VirtualGuest.State keyName) {
      this.keyName = checkNotNull(keyName,"keyName cannot be null or empty:"+keyName);
   }

   @Override
   public int compareTo(PowerState arg0) {
      return keyName.compareTo(arg0.keyName);
   }

   /**
    * Maps onto {@code VirtualGuest.State}
    * @return The key name of a power state.
    *
    */
   public VirtualGuest.State getKeyName() {
      return keyName;
   }

   public Builder toBuilder() {
      return Builder.fromAddress(this);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      PowerState that = (PowerState) o;
      if (keyName != null ? !keyName.equals(that.keyName) : that.keyName != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      return keyName != null ? keyName.hashCode() : 0;
   }

   @Override
   public String toString() {
      return "[keyName=" + keyName + "]";
   }
   
   
}
