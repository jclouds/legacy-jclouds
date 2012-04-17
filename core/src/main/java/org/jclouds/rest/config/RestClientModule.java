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

import java.util.Map;

import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.internal.RestContextImpl;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class RestClientModule<S, A> extends RestModule {
   protected final Class<A> asyncClientType;
   protected final Class<S> syncClientType;
   
   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType,
         Map<Class<?>, Class<?>> sync2Async) {
      super(sync2Async);
      this.asyncClientType = asyncClientType;
      this.syncClientType = syncClientType;
   }

   public RestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      this(syncClientType, asyncClientType, ImmutableMap
            .<Class<?>, Class<?>> of(syncClientType, asyncClientType));
   }
   
   @SuppressWarnings( { "unchecked", "rawtypes" })
   @Override
   protected void configure() {
      super.configure();
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

}