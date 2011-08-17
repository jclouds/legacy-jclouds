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

import static com.google.common.base.Preconditions.checkNotNull;

import java.lang.reflect.Method;
import java.util.Arrays;

import javax.annotation.Nullable;

/**
 * 
 * @author Adrian Cole
 */
public class ClassMethodArgs {
   private final Method method;
   private final Object[] args;
   private final Class<?> asyncClass;

   public ClassMethodArgs(Class<?> asyncClass, Method method, @Nullable Object[] args) {
      this.asyncClass = checkNotNull(asyncClass, "asyncClass");
      this.method = checkNotNull(method, "method");
      this.args = args;
   }

   @Override
   public String toString() {
      return "[class=" + asyncClass.getSimpleName() + ", method=" + method.getName() + ", args="
            + Arrays.toString(args) + "]";
   }

   public Method getMethod() {
      return method;
   }

   public Object[] getArgs() {
      return args;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + Arrays.hashCode(args);
      result = prime * result + ((asyncClass == null) ? 0 : asyncClass.hashCode());
      result = prime * result + ((method == null) ? 0 : method.hashCode());
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
      ClassMethodArgs other = (ClassMethodArgs) obj;
      if (!Arrays.equals(args, other.args))
         return false;
      if (asyncClass == null) {
         if (other.asyncClass != null)
            return false;
      } else if (!asyncClass.equals(other.asyncClass))
         return false;
      if (method == null) {
         if (other.method != null)
            return false;
      } else if (!method.equals(other.method))
         return false;
      return true;
   }

   public Class<?> getAsyncClass() {
      return asyncClass;
   }
}