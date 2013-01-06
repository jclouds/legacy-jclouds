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

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.annotations.Beta;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.reflect.Invokable;

/**
 * Like {@link com.google.common.reflect.AbstractInvocationHandler}, except you process {@link Invokable} and
 * {@link List} as opposed to {@link Method} and arg arrays.
 * 
 * @author Adrian Cole
 * @since 1.6
 */
@Beta
public abstract class AbstractInvocationHandler extends com.google.common.reflect.AbstractInvocationHandler {

   /**
    * @param args
    *           note that this can contain nulls, as method arguments can be null.
    * @see com.google.common.reflect.AbstractInvocationHandler#invoke(Object, Method, Object[])
    */
   protected abstract Object handleInvocation(Object proxy, Invokable<?, ?> method, List<Object> args) throws Throwable;

   @Override
   protected Object handleInvocation(Object proxy, Method method, Object[] argv) throws Throwable {
      List<Object> args = Arrays.asList(argv);
      if (Iterables.all(args, Predicates.notNull()))
         args = ImmutableList.copyOf(args);
      else
         args = Collections.unmodifiableList(args);
      return handleInvocation(proxy, Invokable.from(method), args);
   }
}