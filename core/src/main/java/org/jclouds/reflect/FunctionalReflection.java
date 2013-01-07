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
package org.jclouds.reflect;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.all;
import static org.jclouds.util.Throwables2.propagateIfPossible;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jclouds.reflect.Invocation.Result;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Static utilities relating to functional Java reflection.
 * 
 * @since 1.6
 */
@Beta
public final class FunctionalReflection {
   /**
    * Returns a proxy instance that implements {@code interfaceType} by dispatching method invocations to
    * {@code invocationFunction}. The class loader of {@code interfaceType} will be used to define the proxy class.
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
    *            if {@code interfaceType} does not specify the type of a Java interface
    * @see com.google.common.reflect.AbstractInvocationHandler#invoke(Object, Method, Object[])
    * @see com.google.common.reflect.Reflection#newProxy(Class, java.lang.reflect.InvocationHandler)
    */
   public static <T> T newProxy(Class<T> interfaceType, Function<Invocation, Result> invocationFunction) {
      checkNotNull(interfaceType, "interfaceType");
      checkNotNull(invocationFunction, "invocationFunction");
      checkArgument(interfaceType.isInterface(), "%s is not an interface", interfaceType);
      Object object = Proxy.newProxyInstance(interfaceType.getClassLoader(), new Class<?>[] { interfaceType },
            new FunctionalInvocationHandler<T>(interfaceType, invocationFunction));
      return interfaceType.cast(object);
   }

   private static final class FunctionalInvocationHandler<T> extends
         com.google.common.reflect.AbstractInvocationHandler {
      private final Class<T> interfaceType;
      private final Function<Invocation, Result> invocationFunction;

      private FunctionalInvocationHandler(Class<T> interfaceType, Function<Invocation, Result> invocationFunction) {
         this.interfaceType = interfaceType;
         this.invocationFunction = invocationFunction;
      }

      @Override
      protected Object handleInvocation(Object proxy, Method invoked, Object[] argv) throws Throwable {
         List<Object> args = Arrays.asList(argv);
         if (all(args, notNull()))
            args = ImmutableList.copyOf(args);
         else
            args = Collections.unmodifiableList(args);
         Invokable<?, ?> invokable = Invokable.class.cast(Invokable.from(invoked));
         // not yet support the proxy arg
         Invocation invocation = Invocation.create(interfaceType, invokable, args);
         Result result;
         try {
            result = invocationFunction.apply(invocation);
         } catch (RuntimeException e) {
            result = Result.fail(e);
         }
         if (result.getThrowable().isPresent()) {
            propagateIfPossible(result.getThrowable().get(), invocation.getInvokable().getExceptionTypes());
            throw propagate(result.getThrowable().get());
         }
         return result.getResult().orNull();
      }

      @Override
      public boolean equals(Object o) {
         if (this == o)
            return true;
         if (o == null || getClass() != o.getClass())
            return false;
         FunctionalInvocationHandler<?> that = FunctionalInvocationHandler.class.cast(o);
         return equal(this.interfaceType, that.interfaceType)
               && equal(this.invocationFunction, that.invocationFunction);
      }

      @Override
      public int hashCode() {
         return Objects.hashCode(interfaceType, invocationFunction);
      }

      @Override
      public String toString() {
         return invocationFunction.toString();
      }
   }
}