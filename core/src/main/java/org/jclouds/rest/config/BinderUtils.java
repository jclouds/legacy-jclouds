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
package org.jclouds.rest.config;

import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Binder;
import com.google.inject.TypeLiteral;

/**
 * 
 * @author Adrian Cole
 */
public class BinderUtils {

   /**
    * adds an explicit binding for {@code async} by parsing its annotations. Then. adds an explicit binding for an
    * interface which synchronously blocks on similar calls to an {@code async} type.
    * 
    * @param <S>
    *           sync interface that blocks
    * @param <A>
    *           async type where all methods have same args as {@code sync}, but returns {@link ListenableFuture}
    * @param binder
    *           guice binder
    * @param sync
    *           type interface that blocks
    * @param async
    *           type type that returns {@link ListenableFuture}
    */
   public static <S, A> void bindHttpApi(Binder binder, Class<S> sync, Class<A> async) {
      bindClass(binder, sync);
      bindClass(binder, async);
      bindAsyncHttpApiProvider(binder, async);
      bindHttpApiProvider(binder, sync, async);
   }
   
   @SuppressWarnings("unchecked")
   private static <T> void bindAsyncHttpApiProvider(Binder binder, Class<T> async) {
      TypeToken<AsyncHttpApiProvider<T>> token = new TypeToken<AsyncHttpApiProvider<T>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<T>() {
      }, async);
      binder.bind(async).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }
   
   @SuppressWarnings("unchecked")
   private static <S, A> void bindHttpApiProvider(Binder binder, Class<S> sync, Class<A> async) {
      TypeToken<HttpApiProvider<S, A>> token = new TypeToken<HttpApiProvider<S, A>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<S>() {
      }, sync).where(new TypeParameter<A>() {
      }, async);
      binder.bind(sync).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }
   /**
    * adds an explicit binding for an interface which synchronously blocks on similar calls to an {@code async} type.
    * 
    * @param <S>
    *           sync interface that blocks
    * @param <A>
    *           async type where all methods have same args as {@code sync}, but returns {@link ListenableFuture}
    * @param binder
    *           guice binder
    * @param sync
    *           type interface that blocks
    * @param async
    *           type type that returns {@link ListenableFuture}
    */
   public static <S, A> void bindBlockingApi(Binder binder, Class<S> sync, Class<A> async) {
      bindClass(binder, sync);
      bindClass(binder, async);
      bindCallGetOnFutures(binder, sync, async);
   }

   @SuppressWarnings("unchecked")
   private static <S, A> void bindCallGetOnFutures(Binder binder, Class<S> sync, Class<A> async) {
      TypeToken<CallGetOnFuturesProvider<S, A>> token = new TypeToken<CallGetOnFuturesProvider<S, A>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<S>() {
      }, sync).where(new TypeParameter<A>() {
      }, async);
      binder.bind(sync).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }

   @SuppressWarnings("unchecked")
   private static <K> void bindClass(Binder binder, Class<K> sync) {
      binder.bind(TypeLiteral.class.cast(TypeLiteral.get(new TypeToken<Class<K>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<K>() {
      }, sync).getType()))).toInstance(sync);
   }
}
