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

package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 
 * @author Adrian Cole
 */
public class RangeHardwareProperty extends ParameterizedHardwareProperty {

   private final Number first;
   private final Number last;

   public RangeHardwareProperty(String name, String unit, Number value, HardwareParameter param, Number first,
         Number last) {
      super(Kind.FIXED, name, unit, value, param);
      this.first = checkNotNull(first, "first");
      this.last = checkNotNull(last, "last");
   }

   /**
    * 
    * @return minimum value
    */
   public Number getFirst() {
      return first;
   }

   /**
    * 
    * @return maximum value
    */
   public Number getLast() {
      return last;
   }

   @Override
   public String toString() {
      return "[kind=" + getKind() + ", name=" + getName() + ", unit=" + getUnit() + ", value=" + getValue()
            + ", param=" + getParam() + ", first=" + first + ", last=" + last + "]";
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((first == null) ? 0 : first.hashCode());
      result = prime * result + ((last == null) ? 0 : last.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (!super.equals(obj))
         return false;
      if (getClass() != obj.getClass())
         return false;
      RangeHardwareProperty other = (RangeHardwareProperty) obj;
      if (first == null) {
         if (other.first != null)
            return false;
      } else if (!first.equals(other.first))
         return false;
      if (last == null) {
         if (other.last != null)
            return false;
      } else if (!last.equals(other.last))
         return false;
      return true;
   }
}
