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
package org.jclouds.deltacloud.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.deltacloud.domain.internal.BaseHardwareProperty;

/**
 * 
 * @author Adrian Cole
 */
public class ParameterizedHardwareProperty extends BaseHardwareProperty {

   private final HardwareParameter param;

   public ParameterizedHardwareProperty(Kind kind, String name, String unit, Object value, HardwareParameter param) {
      super(kind, name, unit, value);
      this.param = checkNotNull(param, "param");
   }

   /**
    * 
    * @return how to associate a non-default value with a request against an instance.
    */
   public HardwareParameter getParam() {
      return param;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((param == null) ? 0 : param.hashCode());
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
      ParameterizedHardwareProperty other = (ParameterizedHardwareProperty) obj;
      if (param == null) {
         if (other.param != null)
            return false;
      } else if (!param.equals(other.param))
         return false;
      return true;
   }

   @Override
   public String toString() {
      return "[kind=" + getKind() + ", name=" + getName() + ", unit=" + getUnit() + ", value=" + getValue()
            + ", param=" + getParam() + "]";
   }
}
