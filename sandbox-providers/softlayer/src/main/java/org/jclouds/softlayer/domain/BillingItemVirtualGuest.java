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
import static com.google.common.base.Strings.emptyToNull;

/**
 * 
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Billing_Item_Virtual_Guest"
 *      />
 */
public class BillingItemVirtualGuest implements Comparable<BillingItemVirtualGuest> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private long id = -1;

      public Builder id(long id) {
         this.id = id;
         return this;
      }

      public static Builder fromBillingItemVirtualGuest(BillingItemVirtualGuest in) {
         return BillingItemVirtualGuest.builder().id(in.getId());
      }
   }

   private long id = -1;

   // for deserializer
   BillingItemVirtualGuest() {

   }

   public BillingItemVirtualGuest(long id) {
      this.id = id;
   }

   @Override
   public int compareTo(BillingItemVirtualGuest arg0) {
      return new Long(id).compareTo(arg0.getId());
   }

   /**
    * @return The unique identifier for this billing item.
    */
   public long getId() {
      return id;
   }

   public Builder toBuilder() {
      return Builder.fromBillingItemVirtualGuest(this);
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (id ^ (id >>> 32));
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
      BillingItemVirtualGuest other = (BillingItemVirtualGuest) obj;
      if (id != other.id)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + "]";
   }
}
