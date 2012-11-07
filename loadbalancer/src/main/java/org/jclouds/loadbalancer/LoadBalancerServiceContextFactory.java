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
package org.jclouds.loadbalancer;

import java.util.NoSuchElementException;
import java.util.Properties;

import org.jclouds.ContextBuilder;
import org.jclouds.apis.Apis;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * This class is deprecated in favor of {@link ContextBuilder}. Please view
 * sample usage below:
 * 
 * <pre>
 * context = ContextBuilder.newBuilder("aws-elb")
 *                         .credentials(apikey, secret)
 *                         .buildView(LoadBalancerServiceContext.class);
 * </pre>
 * 
 * @see ContextBuilder
 * @deprecated  As of version 1.5, replaced by {@link ContextBuilder}
 * @author Adrian Cole
 */
@Deprecated
public class LoadBalancerServiceContextFactory {

   /**
    * for porting old code to {@link ContextBuilder}
    */
   public LoadBalancerServiceContextFactory() {
   }

   /**
    * for porting old code to {@link ContextBuilder}
    */
   public LoadBalancerServiceContextFactory(Properties properties) {
   }

   /**
    * @see #createContext(String, String,String, Iterable, Properties)
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, String identity, String credential) {
      return createContext(providerOrApi, identity, credential, ImmutableSet.<Module> of(), new Properties());
   }

   /**
    * @see #createContext(String, String, String, Iterable, Properties)
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, Properties overrides) {
      return createContext(providerOrApi, null, null, ImmutableSet.<Module> of(), overrides);
   }

   /**
    * @see #createContext(String, String,String, Iterable, Properties)
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, Iterable<? extends Module> wiring,
         Properties overrides) {
      return createContext(providerOrApi, null, null, wiring, overrides);
   }

   /**
    * @see #createContext(String, String,String, Iterable, Properties)
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, @Nullable String identity,
         @Nullable String credential, Properties overrides) {
      return createContext(providerOrApi, identity, credential, ImmutableSet.<Module> of(), overrides);
   }

   /**
    * @see createContext(String, String,String, Iterable, Properties)
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, @Nullable String identity,
         @Nullable String credential, Iterable<? extends Module> wiring) {
      return createContext(providerOrApi, identity, credential, wiring, new Properties());
   }

   /**
    *  for porting old code to {@link ContextBuilder}
    * 
    * @param providerOrApi
    * @param identity
    *           nullable, if credentials are present in the overrides
    * @param credential
    *           nullable, if credentials are present in the overrides
    * @param wiring
    *           Configuration you'd like to pass to the context. Ex.
    *           ImmutableSet.<Module>of(new ExecutorServiceModule(myexecutor))
    * @param overrides
    *           properties to override defaults with.
    * @return initialized context ready for use
    */
   public LoadBalancerServiceContext createContext(String providerOrApi, @Nullable String identity,
         @Nullable String credential, Iterable<? extends Module> wiring, Properties overrides) {
      ContextBuilder builder = null;
      try {
         ProviderMetadata pm = Providers.withId(providerOrApi);
         builder = ContextBuilder.newBuilder(pm);
      } catch (NoSuchElementException e) {
         builder = ContextBuilder.newBuilder(Apis.withId(providerOrApi));
      }
      builder.modules(wiring);
      builder.overrides(overrides);
      if (identity != null)
         builder.credentials(identity, credential);
      return builder.build(LoadBalancerServiceContext.class);
   }

}
