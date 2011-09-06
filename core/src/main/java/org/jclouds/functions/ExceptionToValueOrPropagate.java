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
package org.jclouds.functions;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.Nullable;

import org.jclouds.util.Throwables2;

import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class ExceptionToValueOrPropagate<E extends Exception, T> implements Function<Exception, T> {

   private final Class<E> matchingClass;
   private final T value;

   public ExceptionToValueOrPropagate(Class<E> matchingClass, @Nullable T value) {
      this.matchingClass = checkNotNull(matchingClass, "matchingClass");
      this.value = value;
   }

   @SuppressWarnings("unchecked")
   @Override
   public T apply(Exception from) {
      checkNotNull(from, "exception");
      List<Throwable> throwables = Throwables.getCausalChain(from);
      Iterable<E> matchingThrowables = Iterables.filter(throwables, matchingClass);
      if (Iterables.size(matchingThrowables) >= 1)
         return value;
      return (T) Throwables2.propagateOrNull(from);
   }

}
