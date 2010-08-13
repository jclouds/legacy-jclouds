/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.chef;

import static org.jclouds.rest.RestContextFactory.createContextBuilder;
import static org.jclouds.util.Utils.propagateAuthorizationOrOriginalException;

import java.util.Properties;

import javax.annotation.Nullable;

import org.jclouds.rest.RestContextBuilder;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextFactory.ContextSpec;

import com.google.inject.Module;

/**
 * Helper class to instantiate {@code ChefContext} instances.
 * 
 * @author Adrian Cole
 */
public class ChefContextFactory {

   private final RestContextFactory contextFactory;

   /**
    * Initializes with the default properties built-in to jclouds. This is
    * typically stored in the classpath resource {@code rest.properties}
    * 
    * @see RestContextFactory#getPropertiesFromResource
    */
   public ChefContextFactory() {
      this(new RestContextFactory());
   }

   /**
    * Finds definitions in the specified properties.
    */
   public ChefContextFactory(Properties properties) {
      this(new RestContextFactory(properties));
   }

   /**
    * 
    * Uses the supplied RestContextFactory to create {@link ChefContext}s
    */
   public ChefContextFactory(RestContextFactory restContextFactory) {
      this.contextFactory = restContextFactory;
   }

   public static <S, A> ChefContext buildContextUnwrappingExceptions(RestContextBuilder<S, A> builder) {
      try {
         return (ChefContext) builder.buildContext();
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   /**
    * @see #createContext(String, String, String)
    */
   public ChefContext createContext(String identity, String credential) {
      return createContext("chef", identity, credential);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String, String)
    */
   public ChefContext createContext(String provider, String identity, String credential) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder(provider,
            identity, credential));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see #createContext(String, Properties)
    */
   public ChefContext createContext(Properties overrides) {
      return createContext("chef", overrides);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties)
    */
   public ChefContext createContext(String provider, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder(provider,
            overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see #createContext(String, Iterable, Properties)
    */
   public ChefContext createContext(Iterable<? extends Module> modules, Properties overrides) {
      return createContext("chef", modules, overrides);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Iterable, Properties)
    */
   public ChefContext createContext(String provider, Iterable<? extends Module> modules, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder(provider,
            modules, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see #createContext(String,String,String,Iterable)
    */
   public ChefContext createContext(@Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules) {
      return createContext("chef", identity, credential, modules);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String,String String,
    *      Iterable)
    */
   public ChefContext createContext(String provider, @Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder(provider,
            identity, credential, modules));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see #createContext(String,String, String, Iterable, Properties)
    */
   public ChefContext createContext(@Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules, Properties overrides) {
      return createContext("chef", identity, credential, modules, overrides);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String,String,String,
    *      Iterable, Properties)
    */
   public ChefContext createContext(String provider, @Nullable String identity, @Nullable String credential,
         Iterable<? extends Module> modules, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(contextFactory.createContextBuilder(provider,
            identity, credential, modules, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec)
    */
   public <S, A> ChefContext createContext(ContextSpec<S, A> contextSpec) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(createContextBuilder(contextSpec));
      return buildContextUnwrappingExceptions(builder);

   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Properties)
    */
   public <S, A> ChefContext createContext(ContextSpec<S, A> contextSpec, Properties overrides) {
      RestContextBuilder<?, ?> builder = RestContextBuilder.class.cast(createContextBuilder(contextSpec, overrides));
      return buildContextUnwrappingExceptions(builder);
   }

}