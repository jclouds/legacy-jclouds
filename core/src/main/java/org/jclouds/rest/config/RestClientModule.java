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

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.RequiresHttp;
import org.jclouds.internal.ClassMethodArgs;
import org.jclouds.location.config.LocationModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.functions.ImplicitOptionalConverter;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
@RequiresHttp
public class RestClientModule<S, A> extends AbstractModule {
   public final static TypeLiteral<Supplier<URI>> URI_SUPPLIER_TYPE = new TypeLiteral<Supplier<URI>>() {
   };
   
   protected final Class<A> asyncClientType;
   protected final Class<S> syncClientType;
   protected final Map<Class<?>, Class<?>> sync2Async;
   protected final AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
         Map<Class<?>, Class<?>> sync2Async) {
      this.asyncClientType = asyncClientType;
      this.syncClientType = syncClientType;
      this.sync2Async = sync2Async;
   }

   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      this(syncClientType, asyncClientType, ImmutableMap
            .<Class<?>, Class<?>> of(syncClientType, asyncClientType));
   }

   protected void installLocations() {
      install(new LocationModule());
   }
   
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      bind(new TypeLiteral<Function<Object, Optional<Object>>>(){}).to(ImplicitOptionalConverter.class);
      // this will help short circuit scenarios that can otherwise lock out users
      bind(new TypeLiteral<AtomicReference<AuthorizationException>>(){}).toInstance(authException);
      // Ensures the restcontext can be looked up without generic types.
      bind(new TypeLiteral<RestContext>() {
      }).to(
            (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(
                  RestContextImpl.class, syncClientType, asyncClientType))).in(
            Scopes.SINGLETON);
      bind(TypeLiteral.get(Types.newParameterizedType(RestContext.class, syncClientType, asyncClientType))).to(
               (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class, syncClientType,
                        asyncClientType))).in(Scopes.SINGLETON);
      bindAsyncClient();
      bindClient();
      bindErrorHandlers();
      bindRetryHandlers();
      installLocations();
   }

   /**
    * overrides this to change the default retry handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpRetryHandler.class).annotatedWith(Redirection.class).to(
    *       AWSRedirectionRetryHandler.class);
    * bind(HttpRetryHandler.class).annotatedWith(ClientError.class).to(
    *       AWSClientErrorRetryHandler.class);
    * </pre>
    * 
    */
   protected void bindRetryHandlers() {
   }

   /**
    * overrides this to change the default error handlers for the http engine
    * 
    * ex.
    * 
    * <pre>
    * bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(
    *       ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(
    *       ParseAWSErrorFromXmlContent.class);
    * bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(
    *       ParseAWSErrorFromXmlContent.class);
    * </pre>
    * 
    * 
    */
   protected void bindErrorHandlers() {
   }

   protected void bindAsyncClient() {
      BinderUtils.bindAsyncClient(binder(), asyncClientType);
   }

   protected void bindClient() {
      BinderUtils.bindClient(binder(), syncClientType, asyncClientType,
            sync2Async);
   }


   @Provides
   @Singleton
   @Named("sync")
   LoadingCache<ClassMethodArgs, Object> provideSyncDelegateMap(
         CreateClientForCaller createClientForCaller) {
      createClientForCaller.sync2Async = sync2Async;
      return CacheBuilder.newBuilder().build(createClientForCaller);
   }

}