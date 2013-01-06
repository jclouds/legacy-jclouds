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
package org.jclouds.concurrent.internal;

import static com.google.common.base.Optional.fromNullable;
import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.internal.ClassInvokerArgs;
import org.jclouds.internal.ClassInvokerArgsAndReturnVal;
import org.jclouds.logging.Logger;
import org.jclouds.reflect.AbstractInvocationHandler;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.util.Optionals2;
import org.jclouds.util.Throwables2;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Throwables;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ProvisionException;
import com.google.inject.assistedinject.Assisted;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * 
 * @author Adrian Cole
 */
public final class SyncProxy extends AbstractInvocationHandler {

   public static interface Factory {
      /**
       * @param declaring
       *           type of the interface where all methods match those of {@code async} except the return values are
       *           dereferenced
       * @param async
       *           object whose interface matched {@code declaring} except all methods return {@link ListenableFuture}
       * @return blocking invocation handler
       */
      SyncProxy create(Class<?> declaring, Object async);
   }

   @Resource
   private Logger logger = Logger.NULL;
   
   private final Function<ClassInvokerArgsAndReturnVal, Optional<Object>> optionalConverter;
   private final Object delegate;
   private final Class<?> declaring;
   private final Map<Invokable<?, ?>, Invokable<Object, ListenableFuture<?>>> methodMap;
   private final Map<Invokable<?, ?>, Invokable<Object, ?>> syncMethodMap;
   private final Map<Invokable<?, ?>, Optional<Long>> timeoutMap;
   private final LoadingCache<ClassInvokerArgs, Object> delegateMap;
   private final Map<Class<?>, Class<?>> sync2Async;
   private static final Set<Method> objectMethods = ImmutableSet.copyOf(Object.class.getMethods());

   @SuppressWarnings("unchecked")
   @Inject
   @VisibleForTesting
   SyncProxy(Function<ClassInvokerArgsAndReturnVal, Optional<Object>> optionalConverter,
         @Named("sync") LoadingCache<ClassInvokerArgs, Object> delegateMap, Map<Class<?>, Class<?>> sync2Async,
         @Named("TIMEOUTS") Map<String, Long> timeouts, @Assisted Class<?> declaring, @Assisted Object async)
         throws SecurityException, NoSuchMethodException {
      this.optionalConverter = optionalConverter;
      this.delegateMap = delegateMap;
      this.delegate = async;
      this.declaring = declaring;
      this.sync2Async = ImmutableMap.copyOf(sync2Async);

      ImmutableMap.Builder<Invokable<?, ?>, Invokable<Object, ListenableFuture<?>>> methodMapBuilder = ImmutableMap.builder();
      ImmutableMap.Builder<Invokable<?, ?>, Invokable<Object, ?>> syncMethodMapBuilder = ImmutableMap.builder();

      for (Method method : declaring.getMethods()) {
         if (!objectMethods.contains(method)) {
            Method delegatedMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (!Arrays.equals(delegatedMethod.getExceptionTypes(), method.getExceptionTypes()))
               throw new IllegalArgumentException(String.format(
                     "method %s has different typed exceptions than delegated method %s", method, delegatedMethod));
            if (delegatedMethod.getReturnType().isAssignableFrom(ListenableFuture.class)) {
               methodMapBuilder.put(Invokable.from(method), Invokable.class.cast(Invokable.from(delegatedMethod)));
            } else {
               syncMethodMapBuilder.put(Invokable.from(method), Invokable.class.cast(Invokable.from(delegatedMethod)));
            }
         }
      }
      methodMap = methodMapBuilder.build();
      syncMethodMap = syncMethodMapBuilder.build();

      ImmutableMap.Builder<Invokable<?, ?>, Optional<Long>> timeoutMapBuilder = ImmutableMap.builder();
      for (Invokable<?, ?> method : methodMap.keySet()) {
         timeoutMapBuilder.put(method, timeoutInNanos(method, timeouts));
      }
      timeoutMap = timeoutMapBuilder.build();
   }

   public Class<?> getDeclaring() {
      return declaring;
   }
   
   @Override
   protected Object handleInvocation(Object proxy, Invokable<?, ?> method, List<Object> args) throws Throwable {
      if (method.isAnnotationPresent(Delegate.class)) {
         Class<?> syncClass = Optionals2.returnTypeOrTypeOfOptional(method);
         // get the return type of the asynchronous class associated with this client
         // ex. FloatingIPClient is associated with FloatingIPAsyncClient
         Class<?> asyncClass = sync2Async.get(syncClass);
         checkState(asyncClass != null, "please configure corresponding async class for " + syncClass
               + " in your RestClientModule");
         // pass any parameters necessary to get a relevant instance of that async class
         // ex. getClientForRegion("north") might return an instance whose endpoint is
         // different that "south"
         ClassInvokerArgs cma = ClassInvokerArgs.builder().clazz(asyncClass).invoker(method).args(args).build();
         Object returnVal = delegateMap.get(cma);
         if (Optionals2.isReturnTypeOptional(method)){
            ClassInvokerArgsAndReturnVal cmar = ClassInvokerArgsAndReturnVal.builder().fromClassInvokerArgs(cma)
                  .returnVal(returnVal).build();
            return optionalConverter.apply(cmar);
         }
         return returnVal;
      } else if (syncMethodMap.containsKey(method)) {
         try {
            return syncMethodMap.get(method).invoke(delegate, args.toArray());
         } catch (InvocationTargetException e) {
            throw Throwables.propagate(e.getCause());
         }
      } else {
         try {
            Optional<Long> timeoutNanos = timeoutMap.get(method);
            Invokable<Object, ListenableFuture<?>> asyncMethod = methodMap.get(method);
            String name = asyncMethod.getDeclaringClass().getSimpleName() + "." + asyncMethod.getName();
            ListenableFuture<?> future = asyncMethod.invoke(delegate, args.toArray());
            if (timeoutNanos.isPresent()) {
               logger.debug(">> blocking on %s for %s", name, timeoutNanos);
               return future.get(timeoutNanos.get(), TimeUnit.NANOSECONDS);
            }
            logger.debug(">> blocking on %s", name);
            return future.get();
         } catch (ProvisionException e) {
            throw Throwables2.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(method.getExceptionTypes(), e);
         } catch (ExecutionException e) {
            throw Throwables2.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(method.getExceptionTypes(), e);
         } catch (Exception e) {
            throw Throwables2.returnFirstExceptionIfInListOrThrowStandardExceptionOrCause(method.getExceptionTypes(), e);
         }
      }
   }

   // override timeout by values configured in properties(in ms)
   private Optional<Long> timeoutInNanos(Invokable<?, ?> method, Map<String, Long> timeouts) {
      String className = declaring.getSimpleName();
      Optional<Long> timeoutMillis = fromNullable(timeouts.get(className + "." + method.getName()))
                                 .or(fromNullable(timeouts.get(className)))
                                 .or(fromNullable(timeouts.get("default")));
      if (timeoutMillis.isPresent())
         return Optional.of(TimeUnit.MILLISECONDS.toNanos(timeoutMillis.get()));
      return Optional.absent();
   }
   
   @Override
   public String toString() {
      return "blocking invocation handler for: " + delegate.getClass().getSimpleName();
   }
}
