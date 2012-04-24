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

import com.google.common.base.Function;


/**
 * For wrapping covariant functions for passing to non-covariant methods
 * 
 * @author danikov
 */
public class ConcreteFunction<F,T> implements Function<F, T> {
   private final Function<? super F, ? extends T> delegate;
   
   public static <F,T> ConcreteFunction<F,T> wrap(Function<? super F, ? extends T> delegate) {
      return new ConcreteFunction<F, T>(delegate);
   }

   public ConcreteFunction(Function<? super F, ? extends T> delegate) {
      this.delegate = delegate;
   }

   @Override
   public T apply(F input) {
      return delegate.apply(input);
   }

}
