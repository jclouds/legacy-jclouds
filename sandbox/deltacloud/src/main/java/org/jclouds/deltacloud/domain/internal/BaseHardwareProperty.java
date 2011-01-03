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

package org.jclouds.deltacloud.domain.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.deltacloud.domain.HardwareProperty;

/**
 * 
 * @author Adrian Cole
 */
public class BaseHardwareProperty implements HardwareProperty {
   private final Kind kind;
   private final String name;
   private final String unit;
   private final Object value;

   public BaseHardwareProperty(Kind kind, String name, String unit, Object value) {
      this.kind = checkNotNull(kind, "kind");
      this.name = checkNotNull(name, "name");
      this.unit = checkNotNull(unit, "unit");
      this.value = checkNotNull(value, "value");
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Kind getKind() {
      return kind;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getName() {
      return name;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String getUnit() {
      return unit;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Object getValue() {
      return value;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((kind == null) ? 0 : kind.hashCode());
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((unit == null) ? 0 : unit.hashCode());
      result = prime * result + ((value == null) ? 0 : value.hashCode());
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
      BaseHardwareProperty other = (BaseHardwareProperty) obj;
      if (kind != other.kind)
         return false;
      if (name == null) {
         if (other.name != null)
            return false;
      } else if (!name.equals(other.name))
         return false;
      if (unit == null) {
         if (other.unit != null)
            return false;
      } else if (!unit.equals(other.unit))
         return false;
      if (value == null) {
         if (other.value != null)
            return false;
      } else if (!value.equals(other.value))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[kind=" + kind + ", name=" + name + ", unit=" + unit + ", value=" + value + "]";
   }

}
