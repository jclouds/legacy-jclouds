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
    * adds an explicit binding for {@code async} by parsing its annotations.
    * 
    * @param <S>
    *           sync interface that blocks
    * @param <A>
    *           api type with http annotations
    * @param binder
    *           guice binder
    * @param api
    *           type with http annotations
    */
   public static <S, A> void bindHttpApi(Binder binder, Class<A> api) {
      bindClass(binder, api);
      bindAnnotatedHttpApiProvider(binder, api);
   }
   
   @SuppressWarnings("unchecked")
   private static <T> void bindAnnotatedHttpApiProvider(Binder binder, Class<T> annotated) {
      TypeToken<AnnotatedHttpApiProvider<T>> token = new TypeToken<AnnotatedHttpApiProvider<T>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<T>() {
      }, annotated);
      binder.bind(annotated).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }

   /**
    * adds an explicit binding for {@code async} by parsing its annotations. Then, adds an explicit binding for an
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
    * 
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   public static <S, A> void bindMappedHttpApi(Binder binder, Class<S> sync, Class<A> async) {
      bindClass(binder, sync);
      bindClass(binder, async);
      bindAnnotatedMappedHttpApiProvider(binder, async);
      bindHttpApiProvider(binder, sync, async);
   }

   /**
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   private static <T> void bindAnnotatedMappedHttpApiProvider(Binder binder, Class<T> annotated) {
      TypeToken<AnnotatedMappedHttpApiProvider<T>> token = new TypeToken<AnnotatedMappedHttpApiProvider<T>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<T>() {
      }, annotated);
      binder.bind(annotated).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }

   /**
    * 
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   @SuppressWarnings("unchecked")
   private static <S, A> void bindHttpApiProvider(Binder binder, Class<S> sync, Class<A> async) {
      TypeToken<MappedHttpApiProvider<S, A>> token = new TypeToken<MappedHttpApiProvider<S, A>>() {
         private static final long serialVersionUID = 1L;
      }.where(new TypeParameter<S>() {
      }, sync).where(new TypeParameter<A>() {
      }, async);
      binder.bind(sync).toProvider(TypeLiteral.class.cast(TypeLiteral.get(token.getType())));
   }
   
   /**
    * adds an explicit binding for an interface which synchronously blocks on
    * similar calls to an {@code async} type.
    * 
    * @param <S>
    *           sync interface that blocks
    * @param <A>
    *           async type where all methods have same args as {@code sync}, but
    *           returns {@link ListenableFuture}
    * @param binder
    *           guice binder
    * @param sync
    *           type interface that blocks
    * @param async
    *           type type that returns {@link ListenableFuture}
    * 
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
   public static <S, A> void bindMappedApi(Binder binder, Class<S> sync, Class<A> async) {
      bindClass(binder, sync);
      bindClass(binder, async);
      bindCallGetOnFutures(binder, sync, async);
   }

   /**
    * @deprecated will be removed in jclouds 1.7, as async interfaces are no
    *             longer supported.
    */
   @Deprecated
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
