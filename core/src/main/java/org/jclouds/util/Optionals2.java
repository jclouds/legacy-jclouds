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
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;

import com.google.common.base.Optional;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class Optionals2 {
   public static Class<?> returnTypeOrTypeOfOptional(Invokable<?, ?> method) {
      TypeToken<?> type = method.getReturnType();
      return returnTypeOrTypeOfOptional(type.getRawType(), type.getType());
   }

   public static Class<?> returnTypeOrTypeOfOptional(Method method) {
      Class<?> syncClass = method.getReturnType();
      Type genericType = method.getGenericReturnType();
      return returnTypeOrTypeOfOptional(syncClass, genericType);
   }

   private static Class<?> returnTypeOrTypeOfOptional(Class<?> syncClass, Type genericType) {
      if (syncClass.isAssignableFrom(Optional.class)) {
         ParameterizedType futureType = ParameterizedType.class.cast(genericType);
         // TODO: error checking in case this is a type, not a class.
         Type t = futureType.getActualTypeArguments()[0];
         if (t instanceof WildcardType) {
            t = ((WildcardType) t).getUpperBounds()[0];
         }
         syncClass = Class.class.cast(t);
      } else {
      }
      return syncClass;
   }

   public static boolean isReturnTypeOptional(Method method) {
      return method.getReturnType().isAssignableFrom(Optional.class);
   }

}
