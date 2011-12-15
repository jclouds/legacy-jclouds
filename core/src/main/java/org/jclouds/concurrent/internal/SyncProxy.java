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

import static com.google.common.base.Preconditions.checkState;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;

import org.jclouds.concurrent.Timeout;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.util.Throwables2;

import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.ProvisionException;

/**
 * Generates RESTful clients from appropriately annotated interfaces.
 * 
 * @author Adrian Cole
 */
public class SyncProxy implements InvocationHandler {

   @SuppressWarnings("unchecked")
   public static <T> T proxy(Class<T> clazz, Object async,
         @Named("sync") LoadingCache<ClassMethodArgs, Object> delegateMap,
         Map<Class<?>, Class<?>> sync2Async, Map<String, Long> timeouts) throws IllegalArgumentException, SecurityException,
         NoSuchMethodException {
      return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz },
              new SyncProxy(clazz, async, delegateMap, sync2Async, timeouts));
   }

   private final Object delegate;
   private final Class<?> declaring;
   private final Map<Method, Method> methodMap;
   private final Map<Method, Method> syncMethodMap;
   private final Map<Method, Long> timeoutMap;
   private final LoadingCache<ClassMethodArgs, Object> delegateMap;
   private final Map<Class<?>, Class<?>> sync2Async;
   private static final Set<Method> objectMethods = ImmutableSet.copyOf(Object.class.getMethods());

   @Inject
   private SyncProxy(Class<?> declaring, Object async,
         @Named("sync") LoadingCache<ClassMethodArgs, Object> delegateMap, Map<Class<?>,
           Class<?>> sync2Async, final Map<String, Long> timeouts)
         throws SecurityException, NoSuchMethodException {
      this.delegateMap = delegateMap;
      this.delegate = async;
      this.declaring = declaring;
      this.sync2Async = sync2Async;
      if (!declaring.isAnnotationPresent(Timeout.class)) {
         throw new IllegalArgumentException(String.format("type %s does not specify a default @Timeout", declaring));
      }
      Timeout typeTimeout = declaring.getAnnotation(Timeout.class);
      long typeNanos = convertToNanos(typeTimeout);

      methodMap = Maps.newHashMap();
      syncMethodMap = Maps.newHashMap();
      timeoutMap = Maps.newHashMap();
      for (Method method : declaring.getMethods()) {
         if (!objectMethods.contains(method)) {
            Method delegatedMethod = delegate.getClass().getMethod(method.getName(), method.getParameterTypes());
            if (!Arrays.equals(delegatedMethod.getExceptionTypes(), method.getExceptionTypes()))
               throw new IllegalArgumentException(String.format(
                     "method %s has different typed exceptions than delegated method %s", method, delegatedMethod));
            if (delegatedMethod.getReturnType().isAssignableFrom(ListenableFuture.class)) {
               timeoutMap.put(method, getTimeout(method, typeNanos, timeouts));
               methodMap.put(method, delegatedMethod);
            } else {
               syncMethodMap.put(method, delegatedMethod);
            }
         }
      }
   }

   private Long getTimeout(Method method, long typeNanos, final Map<String,Long> timeouts) {
      Long timeout = overrideTimeout(method, timeouts);
      if (timeout == null && method.isAnnotationPresent(Timeout.class)) {
         Timeout methodTimeout = method.getAnnotation(Timeout.class);
         timeout = convertToNanos(methodTimeout);
      }
      return timeout != null ? timeout : typeNanos;

   }

   static long convertToNanos(Timeout timeout) {
      long methodNanos = TimeUnit.NANOSECONDS.convert(timeout.duration(), timeout.timeUnit());
      return methodNanos;
   }

   public Object invoke(Object o, Method method, Object[] args) throws Throwable {
      if (method.getName().equals("equals")) {
         return this.equals(o);
      } else if (method.getName().equals("hashCode")) {
         return this.hashCode();
      } else if (method.getName().equals("toString")) {
         return this.toString();
      } else if (method.isAnnotationPresent(Delegate.class)) {
         Class<?> asyncClass = sync2Async.get(method.getReturnType());
         checkState(asyncClass != null, "please configure corresponding async class for " + method.getReturnType()
               + " in your RestClientModule");
         Object returnVal = delegateMap.get(new ClassMethodArgs(asyncClass, method, args));
         return returnVal;
      } else if (syncMethodMap.containsKey(method)) {
         return syncMethodMap.get(method).invoke(delegate, args);
      } else {
         try {
            return ((ListenableFuture<?>) methodMap.get(method).invoke(delegate, args)).get(timeoutMap.get(method),
                  TimeUnit.NANOSECONDS);
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
   private Long overrideTimeout(final Method method, final Map<String, Long> timeouts) {
      if (timeouts == null) {
         return null;
      }
      final String className = declaring.getSimpleName();
      Long timeout = timeouts.get(className + "." + method.getName());
      if (timeout == null) {
         timeout = timeouts.get(className);
      }
      return timeout != null ? TimeUnit.MILLISECONDS.toNanos(timeout) : null;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null || !(obj instanceof SyncProxy))
         return false;
      SyncProxy other = (SyncProxy) obj;
      if (other == this)
         return true;
      if (other.declaring != this.declaring)
         return false;
      return super.equals(obj);
   }

   @Override
   public int hashCode() {
      return declaring.hashCode();
   }

   public String toString() {
      return "Sync Proxy for: " + delegate.getClass().getSimpleName();
   }
}