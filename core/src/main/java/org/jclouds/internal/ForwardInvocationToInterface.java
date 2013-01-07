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
package org.jclouds.internal;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.jclouds.reflect.Invocation;

import com.google.common.annotations.Beta;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * internal type to {@link SyncProxy} which is likely to be removed
 * 
 * @author Adrian Cole
 */
@Beta
public final class ForwardInvocationToInterface {
   /**
    * @param interfaceType
    *           {@link #getInterfaceType()}
    */
   public static ForwardInvocationToInterface create(Invocation invocation, Class<?> interfaceType) {
      return new ForwardInvocationToInterface(invocation, interfaceType);
   }

   private final Invocation invocation;
   private final Class<?> interfaceType;

   private ForwardInvocationToInterface(Invocation invocation, Class<?> interfaceType) {
      this.invocation = checkNotNull(invocation, "invocation");
      this.interfaceType = checkNotNull(interfaceType, "interfaceType");
   }

   public Invocation getInvocation() {
      return invocation;
   }

   public Class<?> getInterfaceType() {
      return interfaceType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o)
         return true;
      if (o == null || getClass() != o.getClass())
         return false;
      ForwardInvocationToInterface that = ForwardInvocationToInterface.class.cast(o);
      return equal(this.invocation, that.invocation) && equal(this.interfaceType, that.interfaceType);
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(invocation, interfaceType);
   }

   @Override
   public String toString() {
      return string().toString();
   }

   protected ToStringHelper string() {
      return Objects.toStringHelper("").omitNullValues().add("invocation", invocation).add("interfaceType", interfaceType);
   }
}
