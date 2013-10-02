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
package org.jclouds.softlayer.domain;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;

import java.beans.ConstructorProperties;

import org.jclouds.javax.annotation.Nullable;

import com.google.common.base.Objects;

/**
 * A region is made up of a keyname and a description of that region.
 * A region keyname can be used as part of an order.
 * Check the SoftLayer_Product_Order service for more details.
 *
 * @author Jason King
 * @see <a href= "http://sldn.softlayer.com/reference/datatypes/SoftLayer_Location_Region"
 *      />
 */
public class Region implements Comparable<Region> {
   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      private String keyname;
      private String description;

      public Builder keyname(String keyname) {
         this.keyname = keyname;
         return this;
      }

      public Builder description(String description) {
         this.description = description;
         return this;
      }

      public Region build() {
         return new Region(0, keyname, description);
      }

      public static Builder fromAddress(Region in) {
         return Region.builder().keyname(in.getKeyname())
               .description(in.getDescription());
      }
   }

   /* An integer representing the order in which this element is displayed */
   private final int sortOrder;
   private final String keyname;
   private final String description;

   @ConstructorProperties({"sortOrder", "keyname", "description"})
   public Region(int sortOrder, String keyname, String description) {
      this.sortOrder = sortOrder;
      this.keyname = checkNotNull(emptyToNull(keyname), "keyname cannot be null or empty:" + keyname);
      this.description = description;
   }

   @Override
   public int compareTo(Region arg0) {
      return Integer.valueOf(sortOrder).compareTo(arg0.sortOrder);
   }

   /**
    * @return A unique key name for a region. Provided for easy debugging. This is to be sent in with an order.
    */
   public String getKeyname() {
      return keyname;
   }

   /**
    * @return A short description of a region's name. This description is seen on the order forms.
    */
   @Nullable
   public String getDescription() {
      return description;
   }

   public Builder toBuilder() {
      return Builder.fromAddress(this);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      Region region = (Region) o;
      return Objects.equal(keyname, region.keyname)
            && Objects.equal(description, region.description);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(keyname, description);
   }

   @Override
   public String toString() {
      return "[keyname=" + keyname + ", description=" + description + "]";
   }


}
