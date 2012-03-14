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
package org.jclouds.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;

import com.google.common.base.Optional;

/**
 * 
 * @author Adrian Cole
 */
public class Optionals2 {

   public static Class<?> returnTypeOrTypeOfOptional(Method method) {
      boolean optional = isReturnTypeOptional(method);
      Class<?> syncClass;
      if (optional) {
         ParameterizedType futureType = ParameterizedType.class.cast(method.getGenericReturnType());
         // TODO: error checking in case this is a type, not a class.
         syncClass = Class.class.cast(futureType.getActualTypeArguments()[0]);
      } else {
         syncClass = method.getReturnType();
      }
      return syncClass;
   }

   public static boolean isReturnTypeOptional(Method method) {
      boolean optional = method.getReturnType().isAssignableFrom(Optional.class);
      return optional;
   }

}
