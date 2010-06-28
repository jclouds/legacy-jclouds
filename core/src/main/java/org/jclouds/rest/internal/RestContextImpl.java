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
package org.jclouds.rest.internal;

import java.io.IOException;
import java.net.URI;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.lifecycle.Closer;
import org.jclouds.logging.Logger;
import org.jclouds.rest.HttpAsyncClient;
import org.jclouds.rest.HttpClient;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.annotations.Provider;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
@Singleton
public class RestContextImpl<S, A> implements RestContext<S, A> {

   @Resource
   private Logger logger = Logger.NULL;
   private final A asyncApi;
   private final S syncApi;
   private final Closer closer;
   private final URI endpoint;
   private final String identity;
   private final HttpClient simpleClient;
   private final HttpAsyncClient simpleAsyncClient;
   private final String provider;
   private final String apiVersion;

   @Inject
   RestContextImpl(Closer closer, HttpClient simpleClient, HttpAsyncClient simpleAsyncClient,
            Injector injector, TypeLiteral<S> syncApi, TypeLiteral<A> asyncApi,
            @Provider URI endpoint, @Provider String provider, @Identity String identity,
            @ApiVersion String apiVersion) {
      this.simpleClient = simpleClient;
      this.simpleAsyncClient = simpleAsyncClient;
      this.asyncApi = injector.getInstance(Key.get(asyncApi));
      this.syncApi = injector.getInstance(Key.get(syncApi));
      this.closer = closer;
      this.endpoint = endpoint;
      this.identity = identity;
      this.provider = provider;
      this.apiVersion = apiVersion;
   }

   /**
    * {@inheritDoc}
    * 
    * @see Closer
    */
   @Override
   public void close() {
      try {
         closer.close();
      } catch (IOException e) {
         logger.error(e, "error closing content");
      }
   }

   @Override
   public String getPrincipal() {
      return identity;
   }

   @Override
   public A getAsyncApi() {
      return asyncApi;
   }

   @Override
   public S getApi() {
      return syncApi;
   }

   @Override
   public URI getEndpoint() {
      return endpoint;
   }

   @Override
   public HttpAsyncClient asyncHttp() {
      return this.simpleAsyncClient;
   }

   @Override
   public HttpClient http() {
      return this.simpleClient;
   }

   @Override
   public String getApiVersion() {
      return apiVersion;
   }

   @Override
   public String getProvider() {
      return provider;
   }
}
