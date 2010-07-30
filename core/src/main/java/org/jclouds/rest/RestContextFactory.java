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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.size;
import static com.google.common.collect.Iterables.transform;
import static java.util.Collections.EMPTY_LIST;
import static org.jclouds.util.Utils.initContextBuilder;
import static org.jclouds.util.Utils.modulesForProviderInProperties;
import static org.jclouds.util.Utils.propagateAuthorizationOrOriginalException;
import static org.jclouds.util.Utils.resolveContextBuilderClass;
import static org.jclouds.util.Utils.resolvePropertiesBuilderClass;
import static org.jclouds.util.Utils.toStringAndClose;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.jclouds.PropertiesBuilder;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Files;
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

   public static <S, A> ContextSpec<S, A> contextSpec(String provider, String endpoint, String apiVersion,
            String identity, String credential, Class<S> sync, Class<A> async,
            Class<PropertiesBuilder> propertiesBuilderClass, Class<RestContextBuilder<S, A>> contextBuilderClass,
            Iterable<Module> modules) {
      return new ContextSpec<S, A>(provider, endpoint, apiVersion, identity, credential, sync, async,
               propertiesBuilderClass, contextBuilderClass, modules);
   }

   public static <S, A> ContextSpec<S, A> contextSpec(String provider, String endpoint, String apiVersion,
            String identity, String credential, Class<S> sync, Class<A> async) {
      return new ContextSpec<S, A>(provider, endpoint, apiVersion, identity, credential, sync, async);
   }

   @SuppressWarnings("unchecked")
   public static <S, A> ContextSpec<S, A> contextSpec(String provider, String endpoint, String apiVersion,
            String identity, String credential, Class<S> sync, Class<A> async, Iterable<Module> modules) {
      return new ContextSpec<S, A>(provider, endpoint, apiVersion, identity, credential, sync, async,
               PropertiesBuilder.class, (Class) RestContextBuilder.class, modules);
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
      final Iterable<Module> modules;

      ContextSpec(String provider, String endpoint, String apiVersion, String identity, String credential,
               Class<S> sync, Class<A> async, Class<PropertiesBuilder> propertiesBuilderClass,
               Class<RestContextBuilder<S, A>> contextBuilderClass, Iterable<Module> modules) {
         this.provider = checkNotNull(provider, "provider");
         this.endpoint = endpoint;
         this.apiVersion = apiVersion;
         this.identity = identity;
         this.credential = credential;
         this.sync = sync;
         this.async = async;
         checkArgument(RestContextBuilder.class.isAssignableFrom(contextBuilderClass), contextBuilderClass.getName()
                  + " is not a subclass of " + RestContextBuilder.class.getName());
         checkArgument(PropertiesBuilder.class.isAssignableFrom(propertiesBuilderClass), propertiesBuilderClass
                  .getName()
                  + " is not a subclass of " + PropertiesBuilder.class.getName());
         this.propertiesBuilderClass = propertiesBuilderClass;
         this.contextBuilderClass = contextBuilderClass;
         this.modules = modules;
      }

      @SuppressWarnings("unchecked")
      public ContextSpec(String provider, String endpoint, String apiVersion, String identity, String credential,
               Class<S> sync, Class<A> async) {
         this(provider, endpoint, apiVersion, identity, credential, sync, async, PropertiesBuilder.class,
                  (Class) RestContextBuilder.class, EMPTY_LIST);
      }

      @Override
      public int hashCode() {
         final int prime = 31;
         int result = 1;
         result = prime * result + ((apiVersion == null) ? 0 : apiVersion.hashCode());
         result = prime * result + ((async == null) ? 0 : async.hashCode());
         result = prime * result + ((contextBuilderClass == null) ? 0 : contextBuilderClass.hashCode());
         result = prime * result + ((credential == null) ? 0 : credential.hashCode());
         result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
         result = prime * result + ((identity == null) ? 0 : identity.hashCode());
         result = prime * result + ((propertiesBuilderClass == null) ? 0 : propertiesBuilderClass.hashCode());
         result = prime * result + ((provider == null) ? 0 : provider.hashCode());
         result = prime * result + ((sync == null) ? 0 : sync.hashCode());
         return result;
      }

      @Override
      public String toString() {
         return "[provider=" + provider + ", endpoint=" + endpoint + ", apiVersion=" + apiVersion + ", identity="
                  + identity + "]";
      }

      @Override
      public boolean equals(Object obj) {
         if (this == obj)
            return true;
         if (obj == null)
            return false;
         if (getClass() != obj.getClass())
            return false;
         ContextSpec<?, ?> other = (ContextSpec<?, ?>) obj;
         if (apiVersion == null) {
            if (other.apiVersion != null)
               return false;
         } else if (!apiVersion.equals(other.apiVersion))
            return false;
         if (async == null) {
            if (other.async != null)
               return false;
         } else if (!async.equals(other.async))
            return false;
         if (contextBuilderClass == null) {
            if (other.contextBuilderClass != null)
               return false;
         } else if (!contextBuilderClass.equals(other.contextBuilderClass))
            return false;
         if (credential == null) {
            if (other.credential != null)
               return false;
         } else if (!credential.equals(other.credential))
            return false;
         if (endpoint == null) {
            if (other.endpoint != null)
               return false;
         } else if (!endpoint.equals(other.endpoint))
            return false;
         if (identity == null) {
            if (other.identity != null)
               return false;
         } else if (!identity.equals(other.identity))
            return false;
         if (propertiesBuilderClass == null) {
            if (other.propertiesBuilderClass != null)
               return false;
         } else if (!propertiesBuilderClass.equals(other.propertiesBuilderClass))
            return false;
         if (provider == null) {
            if (other.provider != null)
               return false;
         } else if (!provider.equals(other.provider))
            return false;
         if (sync == null) {
            if (other.sync != null)
               return false;
         } else if (!sync.equals(other.sync))
            return false;
         return true;
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
         propagate(e);
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

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, String identity, String credential) {
      return createContextBuilder(provider, identity, credential, ImmutableSet.<Module> of(), NO_PROPERTIES);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties, Iterable<? extends Module>,
    *      Properties)
    */
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, Properties overrides) {
      return createContextBuilder(provider, null, null, ImmutableSet.<Module> of(), overrides);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties, Iterable<? extends Module>,
    *      Properties)
    */
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, Iterable<Module> modules) {
      return createContextBuilder(provider, null, null, modules, NO_PROPERTIES);
   }

   /**
    * 
    * Identity will be found by searching {@code jclouds.identity} failing that {@code
    * provider.identity} where provider corresponds to the parameter. Same pattern is used for
    * credential ({@code jclouds.credential} failing that {@code provider.credential}).
    * 
    * @param <S>
    *           Type of the provider specific client
    * @param <A>
    *           Type of the provide specific async client (same as above, yet all methods return
    *           {@code Future} results)
    * @param provider
    *           name of the provider (ex. s3, bluelock, etc.)
    * @param wiring
    *           defines how objects are bound to interfaces, pass in here to override this, or
    *           specify service implementations.
    * @param overrides
    *           properties to pass to the context.
    */
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, Iterable<? extends Module> wiring,
            Properties overrides) {
      return createContextBuilder(provider, null, null, wiring, overrides);
   }

   @SuppressWarnings("unchecked")
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, @Nullable String identity,
            @Nullable String credential, Properties properties) {
      return createContextBuilder(provider, identity, credential, EMPTY_LIST, properties);
   }

   public <S, A> RestContextBuilder<S, A> createContextBuilder(String provider, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> wiring) {
      return createContextBuilder(provider, identity, credential, wiring, NO_PROPERTIES);
   }

   /**
    * Creates a new remote context.
    * 
    * @param provider
    * @param identity
    *           nullable, if credentials are present in the overrides
    * @param credential
    *           nullable, if credentials are present in the overrides
    * @param wiring
    *           Configuration you'd like to pass to the context. Ex. ImmutableSet.<Module>of(new
    *           ExecutorServiceModule(myexecutor))
    * @param overrides
    *           properties to override defaults with.
    * @return initialized context ready for use
    */
   public <S, A> RestContextBuilder<S, A> createContextBuilder(String providerName, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> wiring, Properties _overrides) {
      checkNotNull(wiring, "wiring");
      ContextSpec<S, A> contextSpec = createContextSpec(providerName, identity, credential, wiring, _overrides);
      return createContextBuilder(contextSpec, _overrides);
   }

   public static Properties toProperties(ContextSpec<?, ?> contextSpec) {
      checkNotNull(contextSpec, "contextSpec");

      Properties props = new Properties();

      props.setProperty(contextSpec.provider + ".endpoint", contextSpec.endpoint);
      props.setProperty(contextSpec.provider + ".apiversion", contextSpec.apiVersion);
      props.setProperty(contextSpec.provider + ".identity", contextSpec.identity);
      if (contextSpec.credential != null)
         props.setProperty(contextSpec.provider + ".credential", contextSpec.credential);
      if (contextSpec.sync != null) {
         props.setProperty(contextSpec.provider + ".sync", contextSpec.sync.getName());
         props.setProperty(contextSpec.provider + ".async", checkNotNull(contextSpec.async, "contextSpec.async")
                  .getName());
      } else {
         props.setProperty(contextSpec.provider + ".contextbuilder", checkNotNull(contextSpec.contextBuilderClass,
                  "contextSpec.contextBuilderClass").getName());

         props.setProperty(contextSpec.provider + ".propertiesbuilder", checkNotNull(
                  contextSpec.propertiesBuilderClass, "contextSpec.propertiesBuilderClass").getName());
      }
      if (size(contextSpec.modules) > 0) {
         props.setProperty(contextSpec.provider + ".modules", Joiner.on(',').join(
                  transform(contextSpec.modules, new Function<Module, String>() {

                     @Override
                     public String apply(Module from) {
                        return from.getClass().getName();
                     }

                  })));
      }
      return props;
   }

   @SuppressWarnings("unchecked")
   public <S, A> ContextSpec<S, A> createContextSpec(String providerName, String identity, String credential,
            Properties _overrides) {
      return createContextSpec(providerName, identity, credential, EMPTY_LIST, _overrides);
   }

   @SuppressWarnings("unchecked")
   public <S, A> ContextSpec<S, A> createContextSpec(String providerName, String identity, String credential,
            Iterable<? extends Module> wiring, Properties _overrides) {
      checkNotNull(providerName, "providerName");
      checkNotNull(_overrides, "overrides");

      Properties props = new Properties();
      props.putAll(this.properties);
      props.putAll(_overrides);

      String endpoint = props.getProperty(providerName + ".endpoint", null);
      String apiVersion = props.getProperty(providerName + ".apiversion", null);
      identity = props.getProperty(providerName + ".identity", props.getProperty("jclouds.identity", identity));
      credential = loadCredentialOrDefault(props, providerName + ".credential", loadCredentialOrDefault(props,
               "jclouds.credential", credential));
      String syncClassName = props.getProperty(providerName + ".sync", null);
      String asyncClassName = props.getProperty(providerName + ".async", null);
      Iterable<Module> modules = concat(modulesForProviderInProperties(providerName, props), wiring);

      Class<RestContextBuilder<S, A>> contextBuilderClass;
      Class<PropertiesBuilder> propertiesBuilderClass;
      Class<S> sync;
      Class<A> async;
      try {
         contextBuilderClass = resolveContextBuilderClass(providerName, props);
         propertiesBuilderClass = resolvePropertiesBuilderClass(providerName, props);
         sync = (Class<S>) (syncClassName != null ? Class.forName(syncClassName) : null);
         async = (Class<A>) (syncClassName != null ? Class.forName(asyncClassName) : null);
      } catch (Exception e) {
         propagate(e);
         assert false : "exception should have propogated " + e;
         return null;
      }
      ContextSpec<S, A> contextSpec = new ContextSpec<S, A>(providerName, endpoint, apiVersion, identity, credential,
               sync, async, propertiesBuilderClass, contextBuilderClass, modules);
      return contextSpec;
   }

   static String loadCredentialOrDefault(Properties properties, String property, String credential) {
      if (properties.containsKey(property))
         return properties.getProperty(property);
      else if (properties.containsKey(property + ".resource"))
         try {
            return toStringAndClose(RestContextFactory.class.getResourceAsStream(properties.getProperty(property
                     + ".resource")));
         } catch (IOException e) {
            throw new RuntimeException("error reading resource: " + properties.getProperty(property + ".resource"));
         }
      else if (properties.containsKey(property + ".file"))
         try {
            return Files.toString(new File(properties.getProperty(property + ".file")), Charsets.UTF_8);
         } catch (IOException e) {
            throw new RuntimeException("error reading file: " + properties.getProperty(property + ".file"));
         }
      else
         return credential;
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(ContextSpec<S, A> contextSpec) {
      return createContextBuilder(contextSpec, NO_PROPERTIES);
   }

   @SuppressWarnings("unchecked")
   public static <S, A> RestContextBuilder<S, A> createContextBuilder(ContextSpec<S, A> contextSpec,
            Properties overrides) {
      return createContextBuilder(contextSpec, EMPTY_LIST, overrides);
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(ContextSpec<S, A> contextSpec,
            Iterable<Module> modules) {
      return createContextBuilder(contextSpec, modules, NO_PROPERTIES);
   }

   public static <S, A> RestContextBuilder<S, A> createContextBuilder(ContextSpec<S, A> contextSpec,
            Iterable<Module> modules, Properties overrides) {
      try {
         PropertiesBuilder builder = contextSpec.propertiesBuilderClass.getConstructor(Properties.class).newInstance(
                  overrides);

         builder.provider(contextSpec.provider);
         if (contextSpec.apiVersion != null)
            builder.apiVersion(contextSpec.apiVersion);
         if (contextSpec.identity != null)
            builder.credentials(contextSpec.identity, contextSpec.credential);
         if (contextSpec.endpoint != null)
            builder.endpoint(contextSpec.endpoint);

         RestContextBuilder<S, A> contextBuilder = initContextBuilder(contextSpec.contextBuilderClass,
                  contextSpec.sync, contextSpec.async, builder.build());

         contextBuilder.withModules(concat(modules, contextSpec.modules));

         return contextBuilder;
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String, String)
    */
   public <S, A> RestContext<S, A> createContext(String provider, String identity, String credential) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, identity, credential);
      return buildContextUnwrappingExceptions(builder);
   }

   public static <S, A> RestContext<S, A> buildContextUnwrappingExceptions(RestContextBuilder<S, A> builder) {
      try {
         return builder.buildContext();
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Properties)
    */
   public <S, A> RestContext<S, A> createContext(String provider, Properties overrides) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, overrides);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, Iterable)
    */
   public <S, A> RestContext<S, A> createContext(String provider, Iterable<? extends Module> wiring,
            Properties overrides) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, wiring, overrides);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Properties)
    */
   public <S, A> RestContext<S, A> createContext(String provider, @Nullable String identity,
            @Nullable String credential, Properties properties) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, identity, credential, properties);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable)
    */
   public <S, A> RestContext<S, A> createContext(String provider, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> wiring) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, identity, credential, wiring);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(String, String,String, Iterable, Properties)
    */
   public <S, A> RestContext<S, A> createContext(String provider, @Nullable String identity,
            @Nullable String credential, Iterable<? extends Module> wiring, Properties overrides) {
      RestContextBuilder<S, A> builder = createContextBuilder(provider, identity, credential, wiring, overrides);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec) {
      RestContextBuilder<S, A> builder = createContextBuilder(contextSpec);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Properties)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec, Properties overrides) {
      RestContextBuilder<S, A> builder = createContextBuilder(contextSpec, overrides);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec, Iterable<Module> modules) {
      RestContextBuilder<S, A> builder = createContextBuilder(contextSpec, modules);
      return buildContextUnwrappingExceptions(builder);
   }

   /**
    * @see RestContextFactory#createContextBuilder(ContextSpec, Iterable, Properties)
    */
   public static <S, A> RestContext<S, A> createContext(ContextSpec<S, A> contextSpec, Iterable<Module> modules,
            Properties overrides) {
      RestContextBuilder<S, A> builder = createContextBuilder(contextSpec, modules, overrides);
      return buildContextUnwrappingExceptions(builder);
   }
}
