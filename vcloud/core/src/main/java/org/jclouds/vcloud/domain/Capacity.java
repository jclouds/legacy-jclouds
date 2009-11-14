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
package org.jclouds.vcloud.domain;


/**
 * @author Adrian Cole
 */
public class Capacity {

   private final String units;
   private final int allocated;
   private final int used;

   public Capacity(String units, int allocated, int used) {
      this.units = units;
      this.allocated = allocated;
      this.used = used;
   }

   public String getUnits() {
      return units;
   }

   public int getAllocated() {
      return allocated;
   }

   public int getUsed() {
      return used;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + allocated;
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
      if (units == null) {
         if (other.units != null)
            return false;
      } else if (!units.equals(other.units))
         return false;
      if (used != other.used)
         return false;
      return true;
   }
}