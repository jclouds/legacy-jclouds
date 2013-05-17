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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.inject.Inject;

import org.jclouds.reflect.Invocation;
import org.jclouds.reflect.InvocationSuccess;
import org.jclouds.rest.config.SetCaller;

import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

/**
 * @param <S>
 *           The enclosing type of the interface that a dynamic proxy like this
 *           implements
 * @param <F>
 *           The function that implements this dynamic proxy
 * 
 * @deprecated please use {@link DelegatesToInvocationFunction} as
 *             async interface will be removed in jclouds 1.7.
 */
@Deprecated
@Beta
public final class DelegatesToPotentiallySyncToAsyncInvocationFunction<S, F extends Function<Invocation, Object>> extends
      DelegatesToInvocationFunction<S, F> {
   private final Map<Class<?>, Class<?>> syncToAsync;

   @Inject
   DelegatesToPotentiallySyncToAsyncInvocationFunction(Injector injector, SetCaller setCaller, Class<S> ownerType,
         Function<InvocationSuccess, Optional<Object>> optionalConverter, F methodInvoker,
         Map<Class<?>, Class<?>> syncToAsync) {
      super(injector, setCaller, ownerType, optionalConverter, methodInvoker);
      this.syncToAsync = checkNotNull(syncToAsync, "syncToAsync");
   }

   protected Key<?> methodInvokerFor(Class<?> returnType) {
      if (methodInvoker.getClass().getTypeParameters().length == 2) {
         if (syncToAsync.containsValue(returnType))
            return Key.get(Types.newParameterizedType(methodInvoker.getClass(), returnType, returnType));
         return Key.get(Types.newParameterizedType(
               methodInvoker.getClass(),
               returnType,
               checkNotNull(syncToAsync.get(returnType), "need async type of %s for %s", returnType,
                     methodInvoker.getClass())));
      }
      return super.methodInvokerFor(returnType);
   }
}
