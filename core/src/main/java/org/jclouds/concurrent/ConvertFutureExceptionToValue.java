/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.concurrent;

import javax.annotation.Nullable;

import org.jclouds.functions.ExceptionToValueOrPropagate;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Matches on a particular exception and converts the response to null.
 * 
 * @author Adrian Cole
 */
public class ConvertFutureExceptionToValue<T> extends FutureExceptionParser<T> {

   public <E extends Exception> ConvertFutureExceptionToValue(ListenableFuture<T> delegate,
            Class<E> exceptionClass, @Nullable T value) {
      super(delegate, new ExceptionToValueOrPropagate<E, T>(exceptionClass, value));
   }

}
