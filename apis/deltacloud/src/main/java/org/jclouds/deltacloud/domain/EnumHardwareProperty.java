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

import java.util.Set;

import com.google.common.collect.ImmutableSet;

/**
 * 
 * @author Adrian Cole
 */
public class EnumHardwareProperty extends ParameterizedHardwareProperty {

   private final Set<Object> availableValues;

   public EnumHardwareProperty(String name, String unit, Object value, HardwareParameter param,
         Set<Object> availableValues) {
      super(Kind.FIXED, name, unit, value, param);
      this.availableValues = ImmutableSet.copyOf(checkNotNull(availableValues, "availableValues"));
   }

   /**
    * 
    * @return a set of available values
    */
   public Set<Object> getAvailableValues() {
      return availableValues;
   }

   @Override
   public String toString() {
      return "[kind=" + getKind() + ", name=" + getName() + ", unit=" + getUnit() + ", value=" + getValue()
            + ", param=" + getParam() + ", availableValues=" + availableValues + "]";
   }
}
