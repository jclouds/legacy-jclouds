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
package org.jclouds.compute;

import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.jclouds.util.Throwables2.propagateAuthorizationOrOriginalException;

import java.util.Properties;

import org.jclouds.javax.annotation.Nullable;

import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;

import com.google.inject.Module;

/**
 * Helper class to instantiate {@code ComputeServiceContext} instances.
 * 
 * @author Adrian Cole
 */
public class ComputeServiceContextFactory {

   private final RestContextFactory contextFactory;

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code rest.properties}
    * 
    * @see RestContextFactory#getPropertiesFromResource
    */
   public ComputeServiceContextFactory() {
      this(new RestContextFactory());
   }

   /**
    * Finds definitions in the specified properties.
    */
   public ComputeServiceContextFactory(Properties properties) {
      this(new RestContextFactory(properties));
   }

   /**
    * 
    * Uses the supplied RestContextFactory to create {@link ComputeServiceContext}s
    */
   public ComputeServiceContextFactory(RestContextFactory restContextFactory) {
      this.contextFactory = restContextFactory;
   }

   public static <S, A> ComputeServiceContext buildContextUnwrappingExceptions(
         ComputeServiceContextBuilder<S, A> builder) {
      try {
         return builder.buildComputeServiceContext();
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String, String)
    */
   public ComputeServiceContext createContext(String provider, String identity, String credential) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(contextFactory
            .createContextBuilder(provider, identity, credential));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties)
    */
   public ComputeServiceContext createContext(String provider, Properties overrides) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(contextFactory
            .createContextBuilder(provider, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Iterable)
    */
   public ComputeServiceContext createContext(String provider, Iterable<? extends Module> modules, Properties overrides) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(contextFactory
            .createContextBuilder(provider, modules, overrides));
      return buildContextUnwrappingExceptions(builder);

   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable)
    */
   public ComputeServiceContext createContext(String provider, @Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(contextFactory
            .createContextBuilder(provider, identity, credential, modules));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable, Properties)
    */
   public ComputeServiceContext createContext(String provider, @Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules, Properties overrides) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(contextFactory
            .createContextBuilder(provider, identity, credential, modules, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(RestContextSpec)
    */
   public <S, A> ComputeServiceContext createContext(RestContextSpec<S, A> contextSpec) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class
            .cast(createContextBuilder(contextSpec));
      return buildContextUnwrappingExceptions(builder);

   }

   /**
    * @see RestContextFactory#createContextBuilder(RestContextSpec, Properties)
    */
   public <S, A> ComputeServiceContext createContext(RestContextSpec<S, A> contextSpec, Properties overrides) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(createContextBuilder(
            contextSpec, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(RestContextSpec, Iterable, Properties)
    */
   public <S, A> ComputeServiceContext createContext(RestContextSpec<S, A> contextSpec, Iterable<Module> modules,
         Properties overrides) {
      ComputeServiceContextBuilder<?, ?> builder = ComputeServiceContextBuilder.class.cast(createContextBuilder(
            contextSpec, modules, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

}