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
package org.jclouds.concurrent;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;

import java.util.concurrent.TimeoutException;

import org.jclouds.util.Throwables2;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class RetryOnTimeOutExceptionSupplier<T> implements Supplier<T> {
   private final Supplier<T> delegate;

   public RetryOnTimeOutExceptionSupplier(Supplier<T> delegate) {
      this.delegate =  checkNotNull(delegate, "delegate");
   }

   @Override
   public T get() {
      TimeoutException ex = null;
      for (int i = 0; i < 3; i++) {
         try {
            ex = null;
            return delegate.get();
         } catch (Exception e) {
            if ((ex = Throwables2.getFirstThrowableOfType(e, TimeoutException.class)) != null)
               continue;
            throw propagate(e);
         }
      }
      if (ex != null)
         propagate(ex);
      assert false;
      return null;
   }

   @Override
   public String toString() {
      return "RetryOnTimeOutExceptionSupplier(" + delegate + ")";
   }

}
