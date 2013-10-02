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
package org.jclouds.reflect;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.all;
import static org.jclouds.reflect.Reflection2.method;
import static org.jclouds.reflect.Reflection2.typeToken;
import static org.jclouds.util.Throwables2.propagateIfPossible;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;
import com.google.common.reflect.TypeToken;

/**
 * Static utilities relating to functional Java reflection.
 * 
 * @since 1.6
 */
@Beta
public final class FunctionalReflection {
   /**
    * Returns a proxy instance that implements {@code enclosingType} by dispatching method invocations to
    * {@code invocationFunction}. The class loader of {@code enclosingType} will be used to define the proxy class.
    * <p>
    * Usage example:
    * 
    * <pre>
    * httpAdapter = new Function&lt;Invocation, Result&gt;() {
    *    public Result apply(Invocation in) {
    *       try {
    *          HttpRequest request = parseRequest(in);
    *          HttpResponse response = invoke(request);
    *          return Result.success(parseJson(response));
    *       } catch (Exception e) {
    *          return Result.failure(e);
    *       }
    *    }
    * };
    * 
    * client = FunctionalReflection.newProxy(Client.class, httpAdapter);
    * </pre>
    * 
    * @param invocationFunction
    *           returns a result or a top-level exception, or result
    * @throws IllegalArgumentException
    *            if {@code enclosingType} does not specify the type of a Java interface
    * @see com.google.common.reflect.AbstractInvocationHandler#invoke(Object, Method, Object[])
    * @see com.google.common.reflect.Reflection#newProxy(Class, java.lang.reflect.InvocationHandler)
    */
   public static <T> T newProxy(TypeToken<T> enclosingType, Function<Invocation, Object> invocationFunction) {
      checkNotNull(enclosingType, "enclosingType");
      checkNotNull(invocationFunction, "invocationFunction");
      return newProxy(enclosingType.getRawType(), new FunctionalInvocationHandler<T>(enclosingType, invocationFunction));
   }

   public static <T> T newProxy(Class<T> enclosingType, Function<Invocation, Object> invocationFunction) {
      checkNotNull(invocationFunction, "invocationFunction");
      return newProxy(enclosingType, new FunctionalInvocationHandler<T>(typeToken(enclosingType), invocationFunction));
   }

   @SuppressWarnings("unchecked")
   private static <T> T newProxy(Class<? super T> enclosingType, FunctionalInvocationHandler<T> invocationHandler) {
      checkNotNull(enclosingType, "enclosingType");
      checkArgument(enclosingType.isInterface(), "%s is not an interface", enclosingType);
      return (T) Proxy.newProxyInstance(enclosingType.getClassLoader(), new Class<?>[] { enclosingType },
            invocationHandler);
   }

   private static final class FunctionalInvocationHandler<T> extends
         com.google.common.reflect.AbstractInvocationHandler {
      private final TypeToken<T> enclosingType;
      private final Function<Invocation, Object> invocationFunction;

      private FunctionalInvocationHandler(TypeToken<T> enclosingType, Function<Invocation, Object> invocationFunction) {
         this.enclosingType = enclosingType;
         this.invocationFunction = invocationFunction;
      }

      @Override
      protected Object handleInvocation(Object proxy, Method invoked, Object[] argv) throws Throwable {
         List<Object> args = Arrays.asList(argv);
         if (all(args, notNull()))
            args = ImmutableList.copyOf(args);
         else
            args = Collections.unmodifiableList(args);
         Invokable<T, Object> invokable = method(enclosingType, invoked);
         Invocation invocation = Invocation.create(invokable, args);
         try {
            return invocationFunction.apply(invocation);
         } catch (RuntimeException e) {
            propagateIfPossible(e, invocation.getInvokable().getExceptionTypes());
            throw propagate(e);
         }
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         FunctionalInvocationHandler<?> that = FunctionalInvocationHandler.class.cast(o);
         return equal(this.enclosingType, that.enclosingType)
               && equal(this.invocationFunction, that.invocationFunction);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(enclosingType, invocationFunction);
      }

      @Override
      public String toString() {
         return invocationFunction.toString();
      }
   }
}
