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
package org.jclouds;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.any;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.inject.Singleton;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.events.config.ConfiguresEventBus;
import org.jclouds.events.config.EventBusModule;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.lifecycle.Closer;
import org.jclouds.lifecycle.config.LifeCycleModule;
import org.jclouds.location.Provider;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.rest.ConfiguresCredentialStore;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.config.BindPropertiesToAnnotations;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.config.RestModule;
import org.jclouds.rest.internal.BaseRestApiMetadata;
import org.jclouds.rest.internal.RestContextImpl;
import org.nnsoft.guice.rocoto.Rocoto;
import org.nnsoft.guice.rocoto.configuration.ConfigurationModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;

/**
 * Creates {@link RestContext} or {@link Injector} instances based on the most commonly requested
 * arguments.
 * <p/>
 * Note that Threadsafe objects will be bound as singletons to the Injector or Context provided.
 * <p/>
 * <p/>
 * If no <code>Module</code>s are specified, the default {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see RestContext
 */
public final class ContextBuilder {

   static class ResolveRestContextModule extends AbstractModule {
      private final RestApiMetadata restApiMetadata;

      ResolveRestContextModule(RestApiMetadata restApiMetadata) {
         this.restApiMetadata = restApiMetadata;
      }

      @SuppressWarnings("unchecked")
      @Override
      protected void configure() {
         if (restApiMetadata.getContext().getRawType().equals(RestContext.class)) {
            TypeToken concreteType = BaseRestApiMetadata.contextToken(TypeToken.of(restApiMetadata.getApi()), TypeToken
                     .of(restApiMetadata.getAsyncApi()));
            // bind explicit type
            bind(TypeLiteral.get(concreteType.getType())).to(
                     (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class, restApiMetadata
                              .getApi(), restApiMetadata.getAsyncApi())));
            // bind potentially wildcard type
            if (!concreteType.equals(restApiMetadata.getContext())) {
               bind(TypeLiteral.get(restApiMetadata.getContext().getType())).to(
                        (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class, restApiMetadata
                                 .getApi(), restApiMetadata.getAsyncApi())));
            }
            // bind w/o types
            bind(TypeLiteral.get(RestContext.class)).to(
                     (TypeLiteral) TypeLiteral.get(Types.newParameterizedType(RestContextImpl.class, restApiMetadata
                              .getApi(), restApiMetadata.getAsyncApi())));
         }
      }
   }

   private final class BindDefaultContextQualifiedToProvider extends AbstractModule {
      @Override
      protected void configure() {
         bind(new TypeLiteral<TypeToken<? extends Closeable>>() {
         }).annotatedWith(Provider.class).toInstance(apiMetadata.getContext());
      }

      @SuppressWarnings("unused")
      @Provides
      @Provider
      @Singleton
      protected Closeable wrapped(Injector i, @Provider TypeToken<? extends Closeable> wrappedType) {
         return (Closeable) i.getInstance(Key.get(TypeLiteral.get(wrappedType.getType())));
      }
   }

   /**
    * looks up a provider or api with the given id
    * 
    * @param providerOrApi
    *           id of the provider or api
    * @return means to build a context to that provider
    * @throws NoSuchElementException
    *            if the id was not configured.
    */
   public static ContextBuilder newBuilder(String providerOrApi) throws NoSuchElementException {
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

   public static ContextBuilder newBuilder(ApiMetadata apiMetadata) {
      try {
         return new ContextBuilder(apiMetadata);
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   public static ContextBuilder newBuilder(ProviderMetadata providerMetadata) {
      try {
         return new ContextBuilder(providerMetadata);
      } catch (Exception e) {
         return propagateAuthorizationOrOriginalException(e);
      }
   }

   protected ProviderMetadata providerMetadata;
   protected String endpoint;
   protected String identity;
   protected String credential;
   protected ApiMetadata apiMetadata;
   protected String apiVersion;
   protected String buildVersion;
   protected Properties overrides = new Properties();;
   protected List<Module> modules = new ArrayList<Module>(3);

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("providerMetadata", providerMetadata).add("apiMetadata", apiMetadata)
               .toString();
   }

   protected ContextBuilder(ProviderMetadata providerMetadata) {
      this(providerMetadata, providerMetadata.getApiMetadata());
   }

   protected ContextBuilder(@Nullable ProviderMetadata providerMetadata, ApiMetadata apiMetadata) {
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

   public ContextBuilder(ApiMetadata apiMetadata) {
      this(null, apiMetadata);
   }

   public ContextBuilder credentials(String identity, String credential) {
      this.identity = identity;
      this.credential = credential;
      return this;
   }

   public ContextBuilder endpoint(String endpoint) {
      this.endpoint = endpoint;
      return this;
   }

   public ContextBuilder apiVersion(String apiVersion) {
      this.apiVersion = checkNotNull(apiVersion, "apiVersion");
      return this;
   }

   public ContextBuilder buildVersion(String buildVersion) {
      this.buildVersion = checkNotNull(buildVersion, "buildVersion");
      return this;
   }

   public ContextBuilder modules(Iterable<? extends Module> modules) {
      addAll(this.modules, modules);
      return this;
   }

   public ContextBuilder overrides(Properties overrides) {
      this.overrides.putAll(overrides);
      return this;
   }

   /**
    * @throws NoSuchElementException
    */
   public static String searchPropertiesForProviderScopedProperty(Properties overrides, String prov, String key,
            String defaultVal) {
      try {
         return Iterables.find(Lists.newArrayList(overrides.getProperty(prov + "." + key), overrides
                  .getProperty("jclouds." + key), defaultVal), Predicates.notNull());
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

      mutable.setProperty(PROPERTY_ENDPOINT, searchPropertiesForProviderScopedProperty(mutable, providerId, "endpoint",
               endpoint));
      mutable.setProperty(PROPERTY_API, searchPropertiesForProviderScopedProperty(mutable, providerId, "api",
               apiMetadata.getName()));
      mutable.setProperty(PROPERTY_API_VERSION, searchPropertiesForProviderScopedProperty(mutable, providerId,
               "api-version", apiVersion));
      mutable.setProperty(PROPERTY_BUILD_VERSION, searchPropertiesForProviderScopedProperty(mutable, providerId,
               "build-version", buildVersion));
      mutable.setProperty(PROPERTY_IDENTITY, searchPropertiesForProviderScopedProperty(mutable, providerId, "identity",
               identity));
      try {
         mutable.setProperty(PROPERTY_CREDENTIAL, searchPropertiesForProviderScopedProperty(mutable, providerId,
                  "credential", credential));
      } catch (NoSuchElementException e) {
         if (apiMetadata.getCredentialName().isPresent())
            throw e;
      }
      if (providerMetadata == null)
         providerMetadata = AnonymousProviderMetadata.forApiWithEndpoint(apiMetadata, mutable
                  .getProperty(PROPERTY_ENDPOINT));

      modules.add(Rocoto.expandVariables(new ConfigurationModule() {

         @Override
         protected void bindConfigurations() {
            bindProperties(mutable);
         }

      }));

      boolean restModuleSpecifiedByUser = restClientModulePresent(modules);
      Iterable<Module> defaultModules = ifSpecifiedByUserDontIncludeDefaultRestModule(restModuleSpecifiedByUser);
      Iterables.addAll(modules, defaultModules);
      addClientModuleIfNotPresent(modules);
      addRestContextBinding();
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      addExecutorServiceIfNotPresent(modules);
      addEventBusIfNotPresent(modules);
      addCredentialStoreIfNotPresent(modules);
      modules.add(new LifeCycleModule());
      modules.add(new BindPropertiesToAnnotations());
      modules.add(new BindDefaultContextQualifiedToProvider());
      Injector returnVal = Guice.createInjector(Stage.PRODUCTION, modules);
      returnVal.getInstance(ExecutionList.class).execute();
      return returnVal;
   }

   void addRestContextBinding() {
      if (apiMetadata instanceof RestApiMetadata) {
         modules.add(new ResolveRestContextModule(RestApiMetadata.class.cast(apiMetadata)));
      }
   }

   private Iterable<Module> ifSpecifiedByUserDontIncludeDefaultRestModule(boolean restModuleSpecifiedByUser) {
      Iterable<Module> defaultModules = Iterables.transform(apiMetadata.getDefaultModules(),
               new Function<Class<? extends Module>, Module>() {

                  @Override
                  public Module apply(Class<? extends Module> arg0) {
                     try {
                        return arg0.newInstance();
                     } catch (InstantiationException e) {
                        throw Throwables.propagate(e);
                     } catch (IllegalAccessException e) {
                        throw Throwables.propagate(e);
                     }
                  }

               });
      if (restModuleSpecifiedByUser)
         defaultModules = Iterables.filter(defaultModules, Predicates.not(configuresRest));
      return defaultModules;
   }

   @SuppressWarnings( { "unchecked" })
   Map<String, Object> propertiesPrefixedWithJcloudsApiOrProviderId(Properties properties, String providerId) {
      return Maps.filterKeys((Map) System.getProperties(), Predicates.containsPattern("^(jclouds|" + providerId + "|"
               + apiMetadata.getId() + ").*"));
   }

   @VisibleForTesting
   protected void addLoggingModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   void addHttpModuleIfNeededAndNotPresent(List<Module> modules) {
      if (nothingConfiguresAnHttpService(modules))
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
   protected void addClientModuleIfNotPresent(List<Module> modules) {
      if (!restClientModulePresent(modules)) {
         addClientModule(modules);
      }
   }

   Predicate<Module> configuresRest = new Predicate<Module>() {
      public boolean apply(Module input) {
         return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
      }

   };

   private boolean restClientModulePresent(List<Module> modules) {
      return any(modules, configuresRest);
   }

   @SuppressWarnings("unchecked")
   protected void addClientModule(List<Module> modules) {
      // TODO: move this up
      if (apiMetadata instanceof RestApiMetadata) {
         RestApiMetadata rest = RestApiMetadata.class.cast(apiMetadata);
         modules.add(new RestClientModule(TypeToken.of(rest.getApi()), TypeToken.of(rest.getAsyncApi())));
      } else {
         modules.add(new RestModule());
      }
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

   /**
    * Builds the base context for this api. Note that this may be of type {@link Closer}, if nothing
    * else was configured via {@link ApiMetadata#getContext()}. Typically, the type returned is
    * {@link RestContext}
    * 
    * @see ApiMetadata#getContext()
    * @see #build(TypeToken)
    */
   @SuppressWarnings("unchecked")
   public <C extends Closeable> C build() {
      return (C) build(apiMetadata.getContext());
   }

   /**
    * @see #build(TypeToken)
    */
   public <C extends Closeable> C build(Class<C> contextType) {
      return build(TypeToken.of(checkNotNull(contextType, "contextType")));
   }

   /**
    * this will build any context supported by the ApiMetadata. This includes the base
    * {@link ApiMetadata#getContext() context}, or any {@link ApiMetadata#getWrappers() wrapper} it
    * supports.
    * 
    * ex. {@code builder.build(BlobStoreContext.class) } will work, if {@code TypeToken<BlobStore>}
    * is a configured {@link ApiMetadata#getWrappers() wrapper} of this api.
    * 
    * 
    */
   @SuppressWarnings("unchecked")
   public <C extends Closeable> C build(final TypeToken<C> contextType) {
      TypeToken<C> returnType = null;
      if (contextType.isAssignableFrom(apiMetadata.getContext()))
         returnType = (TypeToken<C>) apiMetadata.getContext();
      else
         try {
            returnType = (TypeToken<C>) Apis.findWrapper(apiMetadata, contextType);
         } catch (NoSuchElementException e) {
            throw new IllegalArgumentException(String.format(
                     "api %s not assignable from or transformable to %s; context: %s, wrappers: %s", apiMetadata,
                     contextType, apiMetadata.getContext(), apiMetadata.getWrappers()));
         }
      return (C) buildInjector().getInstance(Key.get(TypeLiteral.get(returnType.getType())));
   }

   public ApiMetadata getApiMetadata() {
      return apiMetadata;
   }
}
