/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.slicehost.domain;

/**
 * 
 * A flavor is an available hardware configuration for a slice.
 * 
 * @author Adrian Cole
 */
public class Flavor {

   private final int id;
   private final String name;
   private final int price;
   private final int ram;

   public Flavor(int id, String name, int price, int ram) {
      this.id = id;
      this.name = name;
      this.price = price;
      this.ram = ram;
   }

   /**
    * @return id of the flavor
    */
   public int getId() {
      return id;
   }

   /**
    * @return Verbose name for the flavor, e.g. “256 slice”
    */
   public String getName() {
      return name;
   }

   /**
    * @return The price as an integer of cents. For example: 2000 equals $20.00.
    *         Note that all prices are in USD
    */
   public int getPrice() {
      return price;
   }

   /**
    * @return The amount of RAM (in Megabytes) included with the plan
    */
   public int getRam() {
      return ram;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + id;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + Float.floatToIntBits(price);
      result = prime * result + ram;
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
      Flavor other = (Flavor) obj;
      if (id != other.id)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (Float.floatToIntBits(price) != Float.floatToIntBits(other.price))
         return false;
      if (ram != other.ram)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[id=" + id + ", name=" + name + ", price=" + price + ", ram=" + ram + "]";
   }

}
