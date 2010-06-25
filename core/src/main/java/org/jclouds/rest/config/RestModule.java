/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.rest.config;

import java.util.concurrent.ConcurrentMap;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.ws.rs.core.UriBuilder;

import org.jboss.resteasy.specimpl.UriBuilderImpl;
import org.jclouds.http.HttpResponse;
import org.jclouds.http.TransformingHttpCommand;
import org.jclouds.http.TransformingHttpCommandExecutorService;
import org.jclouds.http.TransformingHttpCommandImpl;
import org.jclouds.http.functions.config.ParserModule;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.internal.AsyncRestClientProxy;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.MapMaker;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
public class RestModule extends AbstractModule {

   @Override
   protected void configure() {
      install(new ParserModule());
      bind(UriBuilder.class).to(UriBuilderImpl.class);
      bind(AsyncRestClientProxy.Factory.class).to(Factory.class).in(
            Scopes.SINGLETON);
      BinderUtils.bindAsyncClient(binder(), HttpAsyncClient.class);
      BinderUtils.bindClient(binder(), HttpClient.class, HttpAsyncClient.class,
            ImmutableMap.<Class<?>, Class<?>> of(HttpClient.class,
                  HttpAsyncClient.class));
   }

   @Provides
   @Singleton
   @Named("async")
   ConcurrentMap<ClassMethodArgs, Object> provideAsyncDelegateMap(
         CreateAsyncClientForCaller createAsyncClientForCaller) {
      return new MapMaker().makeComputingMap(createAsyncClientForCaller);
   }

   static class CreateAsyncClientForCaller implements
         Function<ClassMethodArgs, Object> {
      private final Injector injector;
      private final AsyncRestClientProxy.Factory factory;

      @Inject
      CreateAsyncClientForCaller(Injector injector,
            AsyncRestClientProxy.Factory factory) {
         this.injector = injector;
         this.factory = factory;
      }

      @SuppressWarnings("unchecked")
      @Override
      public Object apply(ClassMethodArgs from) {
         Class clazz = from.getAsyncClass();
         TypeLiteral typeLiteral = TypeLiteral.get(clazz);
         RestAnnotationProcessor util = (RestAnnotationProcessor) injector
               .getInstance(Key.get(TypeLiteral.get(Types.newParameterizedType(
                     RestAnnotationProcessor.class, clazz))));
         util.setCaller(from);

         ConcurrentMap<ClassMethodArgs, Object> delegateMap = injector
               .getInstance(Key.get(
                     new TypeLiteral<ConcurrentMap<ClassMethodArgs, Object>>() {
                     }, Names.named("async")));
         AsyncRestClientProxy proxy = new AsyncRestClientProxy(injector,
               factory, util, typeLiteral, delegateMap);
         injector.injectMembers(proxy);
         return AsyncClientFactory.create(clazz, proxy);
      }
   }

   private static class Factory implements AsyncRestClientProxy.Factory {
      @Inject
      private TransformingHttpCommandExecutorService executorService;
      @Inject
      private Provider<UriBuilder> uriBuilderProvider;

      @SuppressWarnings("unchecked")
      public TransformingHttpCommand<?> create(GeneratedHttpRequest<?> request,
            Function<HttpResponse, ?> transformer) {
         return new TransformingHttpCommandImpl(uriBuilderProvider,
               executorService, request, transformer);
      }

   }

}