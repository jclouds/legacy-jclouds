/**
 *
 * Copyright (C) 2009 Global Cloud Specialists, Inc. <info@globalcloudspecialists.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.rest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

import javax.inject.Inject;

import org.jclouds.rest.internal.RestClientProxy;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

public class RestClientFactory {
   private final Injector injector;

   @Inject
   public RestClientFactory(Injector injector) {
      this.injector = injector;
   }

   @SuppressWarnings("unchecked")
   public <T> T create(Class<T> clazz) {
      return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz },
               (InvocationHandler) injector.getInstance(Key.get(TypeLiteral.get(Types
                        .newParameterizedType(RestClientProxy.class, clazz)))));
   }

}
