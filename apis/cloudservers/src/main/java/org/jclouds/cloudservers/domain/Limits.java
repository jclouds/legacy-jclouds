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
package org.jclouds.cloudservers.domain;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Limits {

   private Set<RateLimit> rate = Sets.newLinkedHashSet();
   private Map<String, Integer> absolute = Maps.newLinkedHashMap();

   public Set<RateLimit> getRate() {
      return rate;
   }

   @Override
   public String toString() {
      return "Limits [rate=" + rate + ", absolute=" + absolute + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((absolute == null) ? 0 : absolute.hashCode());
      result = prime * result + ((rate == null) ? 0 : rate.hashCode());
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
      Limits other = (Limits) obj;
      if (absolute == null) {
         if (other.absolute != null)
            return false;
      } else if (!absolute.equals(other.absolute))
         return false;
      if (rate == null) {
         if (other.rate != null)
            return false;
      } else if (!rate.equals(other.rate))
         return false;
      return true;
   }

   public Map<String, Integer> getAbsolute() {
      return absolute;
   }

}
