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
package org.jclouds.rest.config;

import java.lang.reflect.Proxy;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.reflect.Invocation;
import org.jclouds.rest.internal.DelegatesToInvocationFunction;

import com.google.common.base.Function;
import com.google.inject.Provider;

/**
 * 
 * @author Adrian Cole
 */
@Singleton
public class AnnotatedHttpApiProvider<A> implements Provider<A> {
   private final Class<A> annotatedApiType;
   private final DelegatesToInvocationFunction<A, Function<Invocation, Object>> httpInvoker;

   @Inject
   private AnnotatedHttpApiProvider(DelegatesToInvocationFunction<A, Function<Invocation, Object>> httpInvoker,
         Class<A> annotatedApiType) {
      this.httpInvoker = httpInvoker;
      this.annotatedApiType = annotatedApiType;
   }

   @SuppressWarnings("unchecked")
   @Override
   public A get() {
      return (A) Proxy.newProxyInstance(annotatedApiType.getClassLoader(), new Class<?>[] { annotatedApiType }, httpInvoker);
   }
}
