/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.terremark.domain;

/**
 * The compute options are the CPU and memory configurations supported by Terremark and by the guest
 * operating system of the vApp.
 * 
 * @author Adrian Cole
 * @see <a
 *      href="https://community.vcloudexpress.terremark.com/en-us/product_docs/w/wiki/6-using-the-vcloud-express-api.aspx"
 *      >Terremark documentation</a>
 */
public class ComputeOptions implements Comparable<ComputeOptions> {
   private final int processorCount;
   private final long memory;
   private final float costPerHour;

   public ComputeOptions(int processorCount, long memory, float costPerHour) {
      this.processorCount = processorCount;
      this.memory = memory;
      this.costPerHour = costPerHour;
   }

   public int getProcessorCount() {
      return processorCount;
   }

   public long getMemory() {
      return memory;
   }

   public float getCostPerHour() {
      return costPerHour;
   }

   /**
    * orders by cost
    */
   public int compareTo(ComputeOptions that) {
      if (this == that)
         return 0;
      if (this.costPerHour < that.costPerHour)
         return -1;
      return 1;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Float.floatToIntBits(costPerHour);
      result = prime * result + (int) (memory ^ (memory >>> 32));
      result = prime * result + processorCount;
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
      ComputeOptions other = (ComputeOptions) obj;
      if (Float.floatToIntBits(costPerHour) != Float.floatToIntBits(other.costPerHour))
         return false;
      if (memory != other.memory)
         return false;
      if (processorCount != other.processorCount)
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "ComputeOption [costPerHour=" + costPerHour + ", memory=" + memory
               + ", processorCount=" + processorCount + "]";
   }
}