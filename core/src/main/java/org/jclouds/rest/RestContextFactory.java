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
package org.jclouds.rest;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.jclouds.PropertiesBuilder;
import org.jclouds.util.Utils;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.inject.Module;

/**
 * Helper class to instantiate {@code RestContext} instances. "blobstore.properties"
 * 
 * At least one property is needed needed per context:
 * <ul>
 * <li>tag.contextbuilder=classname extends RestContextBuilder</li>
 * </ul>
 * 
 * Optional properties are as follows
 * <ul>
 * <li>tag.contextbuilder=classname extends RestContextBuilder</li>
 * <li>tag.propertiesbuilder=classname extends HttpPropertiesBuilder</li>
 * </ul>
 * Ex.
 * 
 * <pre>
 * azureblob.contextbuilder=org.jclouds.azure.storage.blob.blobstore.AzureRestContextBuilder
 * azureblob.propertiesbuilder=org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder
 * </pre>
 * 
 * @author Adrian Cole
 */
public class RestContextFactory {

   public static <S, A> ContextSpec<S, A> contextSpec(String provider, String endpoint,
            String apiVersion, String identity, String credential, Class<S> sync, Class<A> async,
            Class<PropertiesBuilder> propertiesBuilderClass,
            Class<RestContextBuilder<S, A>> contextBuilderClass) {
      return new ContextSpec<S, A>(provider, endpoint, apiVersion, identity, credential, sync,
               async, propertiesBuilderClass, contextBuilderClass);
   }

   public static <S, A> ContextSpec<S, A> contextSpec(String provider, String endpoint,
            String apiVersion, String identity, String credential, Class<S> sync, Class<A> async) {
      return new ContextSpec<S, A>(provider, endpoint, apiVersion, identity, credential, sync,
               async);
   }

   public static class ContextSpec<S, A> {
      final String provider;
      final String endpoint;
      final String apiVersion;
      final String identity;
      final String credential;
      final Class<S> sync;
      final Class<A> async;
      final Class<PropertiesBuilder> propertiesBuilderClass;
      final Class<RestContextBuilder<S, A>> contextBuilderClass;

      ContextSpec(String provider, String endpoint, String apiVersion, String identity,
               String credential, Class<S> sync, Class<A> async,
               Class<PropertiesBuilder> propertiesBuilderClass,
               Class<RestContextBuilder<S, A>> contextBuilderClass) {
         this.provider = checkNotNull(provider, "provider");
         this.endpoint = endpoint;
         this.apiVersion = apiVersion;
         this.identity = identity;
         this.credential = credential;
         this.sync = sync;
         this.async = async;
         this.propertiesBuilderClass = propertiesBuilderClass;
         this.contextBuilderClass = contextBuilderClass;
      }

      @SuppressWarnings("unchecked")
      public ContextSpec(String provider, String endpoint, String apiVersion, String identity,
               String credential, Class<S> sync, Class<A> async) {
         this(provider, endpoint, apiVersion, identity, credential, sync, async,
                  PropertiesBuilder.class, (Class) RestContextBuilder.class);
      }
   }

   private final static Properties NO_PROPERTIES = new Properties();
   private final Properties properties;

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code rest.properties}
    * 
    * @see RestContextFactory#getPropertiesFromResource
    */
   public RestContextFactory() {
      this("/rest.properties");
   }

   /**
    * Initializes with the default properties built-in to jclouds. This is typically stored in the
    * classpath resource {@code filename}
    * 
    * @param filename
    *           name of the properties file to initialize from
    * @throws IOException
    *            if the default properties file cannot be loaded
    * @see #getPropertiesFromResource
    */
   public RestContextFactory(String filename) {
      this(getPropertiesFromResource(filename));
   }

   /**
    * Loads the default properties that define the {@code RestContext} objects. <h3>properties file
    * format</h3>
    * 
    * Two properties are needed per context:
    * <ul>
    * <li>tag.contextbuilder=classname extends RestContextBuilder</li>
    * <li>tag.propertiesbuilder=classname extends HttpPropertiesBuilder</li>
    * </ul>
    * Ex.
    * 
    * <pre>
    * azureblob.contextbuilder=org.jclouds.azure.storage.blob.blobstore.AzureRestContextBuilder
    * azureblob.propertiesbuilder=org.jclouds.azure.storage.blob.AzureBlobPropertiesBuilder
    * </pre>
    * 
    * @param filename
    *           name of file to load from in the resource path
    * @return properties object with these items loaded for each tag
    */
   public static Properties getPropertiesFromResource(String filename) {
      Properties properties = new Properties();
      try {
         properties.load(RestContextFactory.class.getResourceAsStream(filename));
      } catch (IOException e) {
         Throwables.propagate(e);
      }
      properties.putAll(System.getProperties());
      return properties;
   }

   /**
    * Initializes the {@code RestContext} definitions from the specified properties.
    */
   @Inject
   public RestContextFactory(Properties properties) {
      this.properties = properties;
   }

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, String identity,
            String credential) {
      return createContextBuilder(provider, identity, credential, ImmutableSet.<Module> of(),
               NO_PROPERTIES);
   }

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, Properties overrides) {
      return createContextBuilder(provider, null, null, ImmutableSet.<Module> of(), overrides);
   }

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider,
            Iterable<? extends Module> modules, Properties overrides) {
      return createContextBuilder(provider, null, null, modules, overrides);
   }

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider,
            @Nullable String identity, @Nullable String credential,
            Iterable<? extends Module> modules) {
      return createContextBuilder(provider, identity, credential, modules, new Properties());
   }

   /**
    * Creates a new remote context.
    * 
    * @param provider
    * @param identity
    *           nullable, if credentials are present in the overrides
    * @param credential
    *           nullable, if credentials are present in the overrides
    * @param modules
    *           Configuration you'd like to pass to the context. Ex. ImmutableSet.<Module>of(new
    *           ExecutorServiceModule(myexecutor))
    * @param overrides
    *           properties to override defaults with.
    * @return initialized context ready for use
    */
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String providerName,
            @Nullable String identity, @Nullable String credential,
            Iterable<? extends Module> modules, Properties _overrides) {
      checkNotNull(modules, "modules");
      ContextSpec<S, A> contextSpec = createContextSpec(providerName, identity, credential,
               _overrides);
      return createContextBuilder(contextSpec, modules, _overrides);
   }

   @SuppressWarnings("unchecked")
   public <A, S> ContextSpec<S, A> createContextSpec(String providerName, String identity,
            String credential, Properties _overrides) {
      checkNotNull(providerName, "providerName");
      checkNotNull(_overrides, "overrides");

      Properties props = new Properties();
      props.putAll(this.properties);
      props.putAll(_overrides);

      String endpoint = props.getProperty(providerName + ".endpoint", null);
      String apiVersion = props.getProperty(providerName + ".apiversion", null);
      identity = props.getProperty(providerName + ".identity", identity);
      credential = props.getProperty(providerName + ".credential", credential);
      String syncClassName = props.getProperty(providerName + ".sync", null);
      String asyncClassName = props.getProperty(providerName + ".async", null);

      Class<RestContextBuilder<S, A>> contextBuilderClass;
      Class<PropertiesBuilder> propertiesBuilderClass;
      Class<S> sync;
      Class<A> async;
      try {
         contextBuilderClass = Utils.resolveContextBuilderClass(providerName, props);
         propertiesBuilderClass = Utils.resolvePropertiesBuilderClass(providerName, props);
         sync = (Class<S>) (syncClassName != null ? Class.forName(syncClassName) : null);
         async = (Class<A>) (syncClassName != null ? Class.forName(asyncClassName) : null);
      } catch (Exception e) {
         Throwables.propagate(e);
         assert false : "exception should have propogated " + e;
         return null;
      }

      ContextSpec<S, A> contextSpec = new ContextSpec<S, A>(providerName, endpoint, apiVersion,
               identity, credential, sync, async, propertiesBuilderClass, contextBuilderClass);
      return contextSpec;
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(ContextSpec<S, A> contextSpec) {
      return createContextBuilder(contextSpec, new Properties());
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(
            ContextSpec<S, A> contextSpec, Properties overrides) {
      return createContextBuilder(contextSpec, ImmutableSet.<Module> of(), overrides);
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(
            ContextSpec<S, A> contextSpec, Iterable<? extends Module> modules) {
      return createContextBuilder(contextSpec, modules, new Properties());
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(
            ContextSpec<S, A> contextSpec, Iterable<? extends Module> modules, Properties overrides) {
      try {

         PropertiesBuilder builder = contextSpec.propertiesBuilderClass.getConstructor(
                  Properties.class).newInstance(overrides);

         builder.provider(contextSpec.provider);
         if (contextSpec.apiVersion != null)
            builder.apiVersion(contextSpec.apiVersion);
         if (contextSpec.identity != null)
            builder.credentials(contextSpec.identity, contextSpec.credential);
         if (contextSpec.endpoint != null)
            builder.endpoint(contextSpec.endpoint);

         RestContextBuilder<S, A> contextBuilder = Utils.initContextBuilder(
                  contextSpec.contextBuilderClass, contextSpec.sync, contextSpec.async, builder
                           .build());

         contextBuilder.withModules(Iterables.toArray(modules, Module.class));

         return contextBuilder;
      } catch (Exception e) {
         AuthorizationException aex = Utils
                  .getFirstThrowableOfType(e, AuthorizationException.class);
         if (aex != null)
            throw aex;
         Throwables.propagate(e);
         assert false : "exception should have propogated " + e;
         return null;
      }
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String, String)
    */
   @SuppressWarnings("unchecked")
   public <S, A> RestContext<S, A> createContext(String provider, String identity,
            String credential) {
      return (RestContext<S, A>) createContextBuilder(provider, identity, credential)
               .buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties)
    */
   @SuppressWarnings("unchecked")
   public <S, A> RestContext<S, A> createContext(String provider, Properties overrides) {
      return (RestContext<S, A>) createContextBuilder(provider, overrides).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Iterable)
    */
   @SuppressWarnings("unchecked")
   public <S, A> RestContext<S, A> createContext(String provider,
            Iterable<? extends Module> modules, Properties overrides) {
      return (RestContext<S, A>) createContextBuilder(provider, modules, overrides).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable)
    */
   @SuppressWarnings("unchecked")
   public <S, A> RestContext<S, A> createContext(String provider, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> modules) {
      return (RestContext<S, A>) createContextBuilder(provider, identity, credential, modules)
               .buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable, Properties)
    */
   @SuppressWarnings("unchecked")
   public <S, A> RestContext<S, A> createContext(String providerName, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> modules, Properties overrides) {
      return (RestContext<S, A>) createContextBuilder(providerName, identity, credential, modules,
               overrides).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable, Properties)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec,
            Iterable<? extends Module> modules, Properties overrides) {
      return createContextBuilder(contextSpec, modules, overrides).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec) {
      return createContextBuilder(contextSpec).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec,
            Iterable<? extends Module> modules) {
      return createContextBuilder(contextSpec, modules).buildContext();
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Properties)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec,
            Properties overrides) {
      return createContextBuilder(contextSpec, overrides).buildContext();
   }
}
