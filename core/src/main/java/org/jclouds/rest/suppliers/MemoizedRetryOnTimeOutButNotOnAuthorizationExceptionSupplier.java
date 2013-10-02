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

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ForwardingObject;
import com.google.common.util.concurrent.UncheckedExecutionException;

/**
 * This will retry the supplier if it encounters a timeout exception, but not if it encounters an
 * AuthorizationException.
 * <p/>
 * A shared exception reference is used so that anyone who encounters an authorizationexception will be short-circuited.
 * This prevents accounts from being locked out.
 * 
 * <h3>details</h3>
 * http://code.google.com/p/google-guice/issues/detail?id=483 guice doesn't remember when singleton providers throw
 * exceptions. in this case, if the supplier fails with an authorization exception, it is called again for each provider
 * method that depends on it. To short-circuit this, we remember the last exception trusting that guice is
 * single-threaded.
 * 
 * Note this implementation is folded into the same class, vs being decorated as stacktraces are exceptionally long and
 * difficult to grok otherwise. We use {@link LoadingCache} to deal with concurrency issues related to the supplier.
 * 
 * @author Adrian Cole
 */
public class MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<T> extends ForwardingObject implements
      Supplier<T> {

   static class SetAndThrowAuthorizationExceptionSupplierBackedLoader<V> extends CacheLoader<String, Optional<V>> {

      private final Supplier<V> delegate;
      private final AtomicReference<AuthorizationException> authException;

      public SetAndThrowAuthorizationExceptionSupplierBackedLoader(Supplier<V> delegate,
            AtomicReference<AuthorizationException> authException) {
         this.delegate = checkNotNull(delegate, "delegate");
         this.authException = checkNotNull(authException, "authException");
      }

      @Override
      public Optional<V> load(String key) {
         if (authException.get() != null)
            throw authException.get();
         try {
            return Optional.fromNullable(delegate.get());
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
         return Objects.toStringHelper(this).add("delegate", delegate).toString();
      }

   }

   private final Supplier<T> delegate;
   private final long duration;
   private final TimeUnit unit;
   private final LoadingCache<String, Optional<T>> cache;

   public static <T> MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<T> create(
         AtomicReference<AuthorizationException> authException, Supplier<T> delegate, long duration, TimeUnit unit) {
      return new MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier<T>(authException, delegate, duration,
            unit);
   }

   MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier(AtomicReference<AuthorizationException> authException,
         Supplier<T> delegate, long duration, TimeUnit unit) {
      this.delegate = delegate;
      this.duration = duration;
      this.unit = unit;
      this.cache = CacheBuilder.newBuilder().expireAfterWrite(duration, unit)
            .build(new SetAndThrowAuthorizationExceptionSupplierBackedLoader<T>(delegate, authException));
   }

   @Override
   protected Supplier<T> delegate() {
      return delegate;
   }

   @Override
   public T get() {
      try {
         return cache.get("FOO").orNull();
      } catch (UncheckedExecutionException e) {
         throw propagate(e.getCause());
      } catch (ExecutionException e) {
         throw propagate(e.getCause());
      }
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("delegate", delegate).add("duration", duration).add("unit", unit)
            .toString();
   }

}
