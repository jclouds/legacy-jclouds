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

import javax.inject.Inject;

import org.jclouds.annotations.Name;
import org.jclouds.domain.Credentials;
import org.jclouds.internal.ContextImpl;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Provider;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 * @deprecated please use {@link org.jclouds.ContextBuilder#buildApi()} as
 *             async interface will be removed in jclouds 1.7.
 */
@Deprecated
@Singleton
public class RestContextImpl<S, A> extends ContextImpl implements RestContext<S, A> {

   private final A asyncApi;
   private final S syncApi;

   @Inject
   protected RestContextImpl(@Name String name, ProviderMetadata providerMetadata,
         @Provider Supplier<Credentials> creds, Utils utils, Closer closer, Injector injector, TypeLiteral<S> syncApi,
         TypeLiteral<A> asyncApi) {
      super(name, providerMetadata, creds, utils, closer);
      checkNotNull(injector, "injector");
      this.asyncApi = injector.getInstance(Key.get(checkNotNull(asyncApi, "asyncApi")));
      this.syncApi = injector.getInstance(Key.get(checkNotNull(syncApi, "syncApi")));
   }

   @Override
   public A getAsyncApi() {
      return asyncApi;
   }

   @Override
   public S getApi() {
      return syncApi;
   }

}
