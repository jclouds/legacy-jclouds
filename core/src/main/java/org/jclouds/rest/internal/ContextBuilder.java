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
package org.jclouds.rest.internal;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.any;
import static com.google.inject.Scopes.SINGLETON;
import static com.google.inject.util.Types.newParameterizedType;
import static org.jclouds.Constants.PROPERTY_API;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_PROVIDER;
import static org.jclouds.util.Throwables2.propagateAuthorizationOrOriginalException;

import java.io.Closeable;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.events.config.ConfiguresEventBus;
import org.jclouds.events.config.EventBusModule;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.lifecycle.config.LifeCycleModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.rest.AnonymousProviderMetadata;
import org.jclouds.rest.ConfiguresCredentialStore;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.ConfiguresRestContext;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.BindPropertiesToAnnotations;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.config.RestModule;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link RestContext} or {@link Injector} instances based on the most
 * commonly requested arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or
 * Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default
 * {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be
 * installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see RestContext
 */
public class ContextBuilder<S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> {

   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    */
   public static ContextBuilder<?, ?, ?, ?> newBuilder(String providerOrApi) throws NoSuchElementException {
      try {
         try {
            return ContextBuilder.newBuilder(Providers.withId(providerOrApi));
         } catch (NoSuchElementException e) {
            return ContextBuilder.newBuilder(Apis.withId(providerOrApi));
         }
      } catch (NoSuchElementException e) {
         Builder<String, String> builder = ImmutableMultimap.<String, String> builder();
         builder.putAll("providers", Iterables.transform(Providers.all(), Providers.idFunction()));
         builder.putAll("apis", Iterables.transform(Apis.all(), Apis.idFunction()));
         throw new NoSuchElementException(String.format("key [%s] not in the list of providers or apis: %s",
               providerOrApi, builder.build()));
      }
   }

   @SuppressWarnings("unchecked")
   public static <S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> ContextBuilder<S, A, C, M> newBuilder(
         ApiMetadata<S, A, C, M> apiMetadata) {
      try {
         for (Constructor<?> ctor : apiMetadata.getContextBuilder().getRawType().getConstructors()) {
            if (ctor.getParameterTypes().length == 1
                  && ctor.getParameterTypes()[0].isAssignableFrom(apiMetadata.getClass()))
               return (ContextBuilder<S, A, C, M>) ctor.newInstance(apiMetadata);
         }
         throw new IllegalArgumentException(String.format("class %s has no constructor that accepts %s", apiMetadata
               .getContextBuilder().getRawType().getSimpleName(), apiMetadata.getClass().getSimpleName()));
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   @SuppressWarnings("unchecked")
   public static <S, A, C extends Closeable, M extends ApiMetadata<S, A, C, M>> ContextBuilder<S, A, C, M> newBuilder(
         ProviderMetadata<S, A, C, M> providerMetadata) {
      try {
         Class<?> contextBuilderClass = providerMetadata.getApiMetadata().getContextBuilder().getRawType();
         return (ContextBuilder<S, A, C, M>) contextBuilderClass.getConstructor(ProviderMetadata.class).newInstance(
               providerMetadata);
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   protected ProviderMetadata<S, A, C, M> providerMetadata;
   protected String endpoint;
   protected String identity;
   protected String credential;
   protected M apiMetadata;
   protected String apiVersion;
   protected String buildVersion;
   protected Properties overrides = new Properties();;
   protected List<Module> modules = new ArrayList<Module>(3);

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("providerMetadata", providerMetadata).add("apiMetadata", apiMetadata)
            .toString();
   }
   
   public ContextBuilder(ProviderMetadata<S, A, C, M> providerMetadata) {
      this(providerMetadata, providerMetadata.getApiMetadata());
   }

   protected ContextBuilder(@Nullable ProviderMetadata<S, A, C, M> providerMetadata, M apiMetadata) {
      this.apiMetadata = checkNotNull(apiMetadata, "apiMetadata");
      this.providerMetadata = providerMetadata;
      if (providerMetadata != null)
         this.endpoint = providerMetadata.getEndpoint();
      if (apiMetadata.getDefaultIdentity().isPresent())
         identity = apiMetadata.getDefaultIdentity().get();
      if (apiMetadata.getDefaultCredential().isPresent())
         credential = apiMetadata.getDefaultCredential().get();
      this.apiVersion = apiMetadata.getVersion();
      this.buildVersion = apiMetadata.getBuildVersion().or("");
      if (endpoint == null)
         endpoint = apiMetadata.getDefaultEndpoint().orNull();
   }

   public ContextBuilder(M apiMetadata) {
      this(null, apiMetadata);
   }

   public ContextBuilder<S, A, C, M> credentials(String identity, String credential) {
      this.identity = identity;
      this.credential = credential;
      return this;
   }

   public ContextBuilder<S, A, C, M> endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
   }

   public ContextBuilder<S, A, C, M> apiVersion(String apiVersion) {
      this.apiVersion = checkNotNull(apiVersion, "apiVersion");
      return this;
   }

   public ContextBuilder<S, A, C, M> buildVersion(String buildVersion) {
      this.buildVersion = checkNotNull(buildVersion, "buildVersion");
      return this;
   }

   public ContextBuilder<S, A, C, M> modules(Iterable<Module> modules) {
      addAll(this.modules, modules);
      return this;
   }

   public ContextBuilder<S, A, C, M> overrides(Properties overrides) {
      this.overrides.putAll(overrides);
      return this;
   }

   /**
    * 
    * @throws NoSuchElementException
    */

   public static String searchPropertiesForProviderScopedProperty(Properties overrides, String prov, String key,
         String defaultVal) {
      try {
         return Iterables.find(Lists.newArrayList(overrides.getProperty(prov + "." + key),
               overrides.getProperty("jclouds." + key), defaultVal), Predicates.notNull());
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException("no " + key + " configured for provider: " + prov);
      }
   }

   public Injector buildInjector() {
      checkNotNull(modules, "modules");
      checkNotNull(overrides, "overrides");
      checkNotNull(apiMetadata, "api");

      final Properties mutable = new Properties();
      mutable.putAll(apiMetadata.getDefaultProperties());
      String providerId;
      if (providerMetadata != null) {
         mutable.putAll(providerMetadata.getDefaultProperties());
         mutable.setProperty(PROPERTY_PROVIDER, providerId = providerMetadata.getId());
         mutable.setProperty(PROPERTY_ISO3166_CODES, Joiner.on(',').join(providerMetadata.getIso3166Codes()));
      } else {
         mutable.setProperty(PROPERTY_PROVIDER, providerId = apiMetadata.getId());
      }
      mutable.putAll(checkNotNull(overrides, "overrides"));
      mutable.putAll(propertiesPrefixedWithJcloudsApiOrProviderId(System.getProperties(), providerId));

      mutable.setProperty(PROPERTY_ENDPOINT,
            searchPropertiesForProviderScopedProperty(mutable, providerId, "endpoint", endpoint));
      mutable.setProperty(PROPERTY_API,
            searchPropertiesForProviderScopedProperty(mutable, providerId, "api", apiMetadata.getName()));
      mutable.setProperty(PROPERTY_API_VERSION,
            searchPropertiesForProviderScopedProperty(mutable, providerId, "api-version", apiVersion));
      mutable.setProperty(PROPERTY_BUILD_VERSION,
            searchPropertiesForProviderScopedProperty(mutable, providerId, "build-version", buildVersion));
      mutable.setProperty(PROPERTY_IDENTITY,
            searchPropertiesForProviderScopedProperty(mutable, providerId, "identity", identity));
      try {
         mutable.setProperty(PROPERTY_CREDENTIAL,
               searchPropertiesForProviderScopedProperty(mutable, providerId, "credential", credential));
      } catch (NoSuchElementException e) {
         if (apiMetadata.getCredentialName().isPresent())
            throw e;
      }
      if (providerMetadata == null)
         providerMetadata = AnonymousProviderMetadata.forApiWithEndpoint(apiMetadata,
               mutable.getProperty(PROPERTY_ENDPOINT));
      modules.add(Rocoto.expandVariables(new ConfigurationModule() {

         @Override
         protected void bindConfigurations() {
            bindProperties(mutable);
         }

      }));
      addContextModule(modules);
      addClientModuleIfNotPresent(modules);
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      ifHttpConfigureRestOtherwiseGuiceClientFactory(modules);
      addExecutorServiceIfNotPresent(modules);
      addCredentialStoreIfNotPresent(modules);
      modules.add(new LifeCycleModule());
      modules.add(new BindPropertiesToAnnotations());
      Injector returnVal = Guice.createInjector(Stage.PRODUCTION, modules);
      returnVal.getInstance(ExecutionList.class).execute();
      return returnVal;
   }

   @SuppressWarnings({ "unchecked", "rawtypes" })
   Map<String, Object> propertiesPrefixedWithJcloudsApiOrProviderId(Properties properties, String providerId) {
      return Maps.filterKeys((Map) System.getProperties(),
            Predicates.containsPattern("^(jclouds|" + providerId + "|" + apiMetadata.getId() + ").*"));
   }

   @VisibleForTesting
   protected void addLoggingModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   void addHttpModuleIfNeededAndNotPresent(List<Module> modules) {
      if (defaultOrAtLeastOneModuleRequiresHttp(modules) && nothingConfiguresAnHttpService(modules))
         modules.add(new JavaUrlHttpCommandExecutorServiceModule());
   }

   private boolean nothingConfiguresAnHttpService(List<Module> modules) {
      return (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class);
         }

      }));
   }

   @VisibleForTesting
   protected void addContextModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestContext.class);
         }

      })) {
         addContextModule(modules);
      }
   }

   @VisibleForTesting
   protected void addContextModule(List<Module> modules) {
      modules.add(new AbstractModule() {

         @SuppressWarnings({ "unchecked", "rawtypes" })
         @Override
         protected void configure() {
            bind(
                  (TypeLiteral) TypeLiteral.get(newParameterizedType(RestContext.class, providerMetadata
                        .getApiMetadata().getApi(), apiMetadata.getAsyncApi()))).to(
                  TypeLiteral.get(newParameterizedType(RestContextImpl.class, apiMetadata.getApi(),
                        apiMetadata.getAsyncApi()))).in(SINGLETON);
         }

         public String toString() {
            return String.format("configure rest context %s->%s", apiMetadata.getApi().getSimpleName(), apiMetadata
                  .getAsyncApi().getSimpleName());
         }

      });
   }

   @VisibleForTesting
   protected void ifHttpConfigureRestOtherwiseGuiceClientFactory(List<Module> modules) {
      if (defaultOrAtLeastOneModuleRequiresHttp(modules)) {
         modules.add(new RestModule());
      }
   }

   private boolean defaultOrAtLeastOneModuleRequiresHttp(List<Module> modules) {
      return atLeastOneModuleRequiresHttp(modules) || !restClientModulePresent(modules);
   }

   private boolean atLeastOneModuleRequiresHttp(List<Module> modules) {
      return any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(RequiresHttp.class);
         }
      });
   }

   @VisibleForTesting
   protected void addClientModuleIfNotPresent(List<Module> modules) {
      if (!restClientModulePresent(modules)) {
         addClientModule(modules);
      }
   }

   private boolean restClientModulePresent(List<Module> modules) {
      return any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
         }

      });
   }

   protected void addClientModule(List<Module> modules) {
      modules.add(new RestClientModule<S, A>(apiMetadata.getApi(), providerMetadata.getApiMetadata().getAsyncApi()));
   }
   
   @VisibleForTesting
   protected void addEventBusIfNotPresent(List<Module> modules) {
       if (!any(modules, new Predicate<Module>() {
           public boolean apply(Module input) {
              return input.getClass().isAnnotationPresent(ConfiguresEventBus.class);
           }
        }
       
        )) {
           modules.add(new EventBusModule());
       }
   }
   
   @VisibleForTesting
   protected void addExecutorServiceIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresExecutorService.class);
         }
      }

      )) {
         if (any(modules, new Predicate<Module>() {
            public boolean apply(Module input) {
               return input.getClass().isAnnotationPresent(SingleThreaded.class);
            }
         })) {
            modules.add(new ExecutorServiceModule(MoreExecutors.sameThreadExecutor(), MoreExecutors
                  .sameThreadExecutor()));
         } else {
            modules.add(new ExecutorServiceModule());
         }
      }
   }

   @VisibleForTesting
   protected void addCredentialStoreIfNotPresent(List<Module> modules) {
      if (!any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresCredentialStore.class);
         }
      }

      )) {
         modules.add(new CredentialStoreModule());
      }
   }

   @SuppressWarnings("unchecked")
   public C build() {
      return (C) buildInjector().getInstance(Key.get(TypeLiteral.get(apiMetadata.getContext().getType())));
   }
   
   public M getApiMetadata() {
      return apiMetadata;
   }
}
