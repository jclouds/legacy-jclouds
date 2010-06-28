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
package org.jclouds.blobstore;

import static org.jclouds.rest.RestContextFactory.createContextBuilder;

import java.util.Properties;

import javax.annotation.Nullable;

import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;

import com.google.inject.Module;

/**
 * Helper class to instantiate {@code BlobStoreContext} instances.
 * 
 * @author Adrian Cole
 */
public class BlobStoreContextFactory {

   private final RestContextFactory contextFactory;

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code rest.properties}
    * 
    * @see RestContextFactory#getPropertiesFromResource
    */
   public BlobStoreContextFactory() {
      this(new RestContextFactory());
   }

   /**
    * Finds definitions in the specified properties.
    */
   public BlobStoreContextFactory(Properties properties) {
      this(new RestContextFactory(properties));
   }

   /**
    * 
    * Uses the supplied RestContextFactory to create {@link BlobStoreContext}s
    */
   public BlobStoreContextFactory(RestContextFactory restContextFactory) {
      this.contextFactory = restContextFactory;
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String, String)
    */
   public BlobStoreContext createContext(String provider, String identity, String credential) {
      return BlobStoreContextBuilder.class.cast(
               contextFactory.createContextBuilder(provider, identity, credential))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties)
    */
   public BlobStoreContext createContext(String provider, Properties overrides) {
      return BlobStoreContextBuilder.class.cast(
               contextFactory.createContextBuilder(provider, overrides)).buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Iterable)
    */
   public BlobStoreContext createContext(String provider, Iterable<? extends Module> modules,
            Properties overrides) {
      return BlobStoreContextBuilder.class.cast(
               contextFactory.createContextBuilder(provider, modules, overrides))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable)
    */
   public BlobStoreContext createContext(String provider, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> modules) {
      return BlobStoreContextBuilder.class.cast(
               contextFactory.createContextBuilder(provider, identity, credential, modules))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable, Properties)
    */
   public BlobStoreContext createContext(String providerName, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> modules, Properties overrides) {
      return BlobStoreContextBuilder.class.cast(
               contextFactory.createContextBuilder(providerName, identity, credential, modules,
                        overrides)).buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec)
    */
   public <S, A> BlobStoreContext createContext(ContextSpec<S, A> contextSpec) {
      return BlobStoreContextBuilder.class.cast(createContextBuilder(contextSpec))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable)
    */
   public <S, A> BlobStoreContext createContext(ContextSpec<S, A> contextSpec,
            Iterable<? extends Module> modules) {
      return BlobStoreContextBuilder.class.cast(createContextBuilder(contextSpec, modules))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Properties)
    */
   public <S, A> BlobStoreContext createContext(ContextSpec<S, A> contextSpec, Properties overrides) {
      return BlobStoreContextBuilder.class.cast(createContextBuilder(contextSpec, overrides))
               .buildBlobStoreContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable, Properties)
    */
   public <S, A> BlobStoreContext createContext(ContextSpec<S, A> contextSpec,
            Iterable<? extends Module> modules, Properties overrides) {
      return BlobStoreContextBuilder.class.cast(
               createContextBuilder(contextSpec, modules, overrides)).buildBlobStoreContext();
   }
}