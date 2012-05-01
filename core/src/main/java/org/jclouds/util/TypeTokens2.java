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

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.TypeVariable;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author Adrian Cole
 */
public class TypeTokens2 {
   /**
    * @throws IllegalStateException if the type is an instanceof {@link TypeVariable}
    */
   public static <T> TypeToken<T> checkBound(TypeToken<T> type) throws IllegalStateException {
      checkState(!(type.getType() instanceof TypeVariable<?>),
               "unbound type variable: %s, use ctor that explicitly assigns this", type);
      return type;
   }
}
