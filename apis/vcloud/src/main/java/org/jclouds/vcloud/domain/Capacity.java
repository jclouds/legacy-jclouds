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
package org.jclouds.vcloud.domain;

/**
 * reports storage resource consumption in a vDC.
 * 
 * @author Adrian Cole
 */
public class Capacity {

   private final String units;
   private final long allocated;
   private final long limit;
   private final int used;
   private final long overhead;

   public Capacity(String units, long allocated, long limit, int used, long overhead) {
      this.units = units;
      this.limit = limit;
      this.allocated = allocated;
      this.used = used;
      this.overhead = overhead;
   }

   public String getUnits() {
      return units;
   }

   public long getAllocated() {
      return allocated;
   }

   public long getLimit() {
      return limit;
   }

   /**
    * percentage of the allocation in use.
    */
   public int getUsed() {
      return used;
   }

   /**
    * number of Units allocated to vShield Manager virtual machines provisioned from this vDC.
    */
   public long getOverhead() {
      return overhead;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (int) (allocated ^ (allocated >>> 32));
      result = prime * result + (int) (limit ^ (limit >>> 32));
      result = prime * result + (int) (overhead ^ (overhead >>> 32));
      result = prime * result + ((units == null) ? 0 : units.hashCode());
      result = prime * result + used;
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
      Capacity other = (Capacity) obj;
      if (allocated != other.allocated)
         return false;
      if (limit != other.limit)
         return false;
      if (overhead != other.overhead)
         return false;
      if (units == null) {
         if (other.units != null)
            return false;
      } else if (!units.equals(other.units))
         return false;
      if (used != other.used)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[allocated=" + allocated + ", limit=" + limit + ", overhead=" + overhead + ", units=" + units + ", used="
               + used + "]";
   }
}
