/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rest.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static org.jclouds.util.Throwables2.getFirstThrowableOfType;

import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Supplier;

/**
 * 
 * @author Adrian Cole
 */
public class SetAndThrowAuthorizationExceptionSupplier<T> implements Supplier<T> {
   private final Supplier<T> delegate;
   private final AtomicReference<AuthorizationException> authException;

   public SetAndThrowAuthorizationExceptionSupplier(Supplier<T> delegate,
         AtomicReference<AuthorizationException> authException) {
      this.delegate = checkNotNull(delegate, "delegate");
      this.authException = checkNotNull(authException, "authException");
   }

   public T get() {
      if (authException.get() != null)
         throw authException.get();
      try {
         return delegate.get();
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      } catch (Exception e) {
         AuthorizationException aex = getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null) {
            authException.set(aex);
            throw aex;
         }
         throw propagate(e);
      }
   }

   @Override
   public String toString() {
      return "RetryOnTimeOutButNotOnAuthorizationExceptionSupplier(" + delegate + ")";
   }

}
