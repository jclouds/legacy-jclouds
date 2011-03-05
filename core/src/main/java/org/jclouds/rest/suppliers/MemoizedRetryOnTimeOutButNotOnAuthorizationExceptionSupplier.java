/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.rest.suppliers;

import static com.google.common.base.Suppliers.memoizeWithExpiration;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.concurrent.RetryOnTimeOutExceptionSupplier;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Supplier;

/**
 * This will retry the supplier if it encounters a timeout exception, but not if it encounters an
 * AuthorizationException.
 * <p/>
 * A shared exception reference is used so that anyone who encounters an authorizationexception will
 * be short-circuited. This prevents accounts from being locked out.
 * 
 * <h3>details</h3>
 * http://code.google.com/p/google-guice/issues/detail?id=483 guice doesn't remember when singleton
 * providers throw exceptions. in this case, if the supplier fails with an authorization exception,
 * it is called again for each provider method that depends on it. To short-circuit this, we
 * remember the last exception trusting that guice is single-threaded.
 * 
 * @author Adrian Cole
 */
public class MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<T> implements Supplier<T> {
   private final Supplier<T> delegate;
   private final long seconds;

   public MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(
         AtomicReference<AuthorizationException> authException, long seconds, Supplier<T> delegate) {
      this.delegate = memoizeWithExpiration(new RetryOnTimeOutExceptionSupplier<T>(
            new SetAndThrowAuthorizationExceptionSupplier<T>(delegate, authException)), seconds, TimeUnit.SECONDS);
      this.seconds = seconds;
   }

   @Override
   public T get() {
      return delegate.get();
   }

   @Override
   public String toString() {
      return "memoizeWithExpiration(" + delegate + ", seconds=" + seconds + ")";
   }
}