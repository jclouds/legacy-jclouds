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
package org.jclouds.tmrk.enterprisecloud.domain.resource;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.tmrk.enterprisecloud.domain.internal.ResourceCapacity;

import javax.xml.bind.annotation.XmlElement;

/**
 * <xs:complexType name="StorageResourceSummary">
 * @author Jason King
 */
public class StorageResourceSummary {

   @SuppressWarnings("unchecked")
   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return new Builder().fromStorageResourceSummary(this);
   }

   public static class Builder {
      private ResourceCapacity purchased;
      private ResourceCapacity used;

      /**
      * @see org.jclouds.tmrk.enterprisecloud.domain.resource.StorageResourceSummary#getPurchased
      */
      public Builder purchased(ResourceCapacity purchased) {
         this.purchased = purchased;
         return this;
      }

      /**
       * @see org.jclouds.tmrk.enterprisecloud.domain.resource.StorageResourceSummary#getUsed
       */
      public Builder used(ResourceCapacity used) {
         this.used = used;
         return this;
      }

      public StorageResourceSummary build() {
         return new StorageResourceSummary(purchased,used);
      }

      public Builder fromStorageResourceSummary(StorageResourceSummary in) {
         return purchased(in.getPurchased()).used(in.getUsed());
      }
   }

   @XmlElement(name = "Purchased", required = false)
   private ResourceCapacity purchased;

   @XmlElement(name = "Used", required = false)
   private ResourceCapacity used;

   protected StorageResourceSummary(@Nullable ResourceCapacity purchased, @Nullable ResourceCapacity used) {
      this.purchased = purchased;
      this.used = used;
   }

   protected StorageResourceSummary() {
      //For JAXB
   }

   public ResourceCapacity getPurchased() {
      return purchased;
   }

   public ResourceCapacity getUsed() {
      return used;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      StorageResourceSummary that = (StorageResourceSummary) o;

      if (purchased != null ? !purchased.equals(that.purchased) : that.purchased != null)
         return false;
      if (used != null ? !used.equals(that.used) : that.used != null)
         return false;

      return true;
   }

   @Override
   public int hashCode() {
      int result = purchased != null ? purchased.hashCode() : 0;
      result = 31 * result + (used != null ? used.hashCode() : 0);
      return result;
   }

   @Override
   public String toString() {
      return String.format("[%s]",string());
   }

   protected String string() {
       return "purchased="+purchased+", used="+ used;
   }
}
