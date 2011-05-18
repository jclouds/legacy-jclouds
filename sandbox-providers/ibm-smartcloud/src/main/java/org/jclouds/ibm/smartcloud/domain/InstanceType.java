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
package org.jclouds.ibm.smartcloud.domain;

import com.google.common.collect.ComparisonChain;

/**
 * 
 * 
 * @author Adrian Cole
 */
public class InstanceType implements Comparable<InstanceType> {

   protected String label;
   protected Price price;
   protected String id;

   public InstanceType(String label, Price price, String id) {
      super();
      this.label = label;
      this.price = price;
      this.id = id;
   }

   InstanceType() {
      super();
   }

   public String getLabel() {
      return label;
   }

   public Price getPrice() {
      return price;
   }

   public String getId() {
      return id;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((id == null) ? 0 : id.hashCode());
      result = prime * result + ((label == null) ? 0 : label.hashCode());
      result = prime * result + ((price == null) ? 0 : price.hashCode());
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
      InstanceType other = (InstanceType) obj;
      if (id == null) {
         if (other.id != null)
            return false;
      } else if (!id.equals(other.id))
         return false;
      if (label == null) {
         if (other.label != null)
            return false;
      } else if (!label.equals(other.label))
         return false;
      if (price == null) {
         if (other.price != null)
            return false;
      } else if (!price.equals(other.price))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", label=" + label + ", price=" + price + "]";
   }

   @Override
   public int compareTo(InstanceType o) {
      return ComparisonChain.start().compare(this.getPrice().getRate(), o.getPrice().getRate()).result();
   }
}
