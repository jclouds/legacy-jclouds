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
import static com.google.common.base.Predicates.containsPattern;
import static com.google.common.base.Predicates.instanceOf;
import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.addAll;
import static com.google.common.collect.Iterables.any;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Lists.newArrayList;
import static org.jclouds.Constants.PROPERTY_API;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_BUILD_VERSION;
import static org.jclouds.Constants.PROPERTY_CREDENTIAL;
import static org.jclouds.Constants.PROPERTY_ENDPOINT;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_ISO3166_CODES;
import static org.jclouds.Constants.PROPERTY_PROVIDER;
import static org.jclouds.util.Throwables2.propagateAuthorizationOrOriginalException;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Set;

import org.jclouds.apis.ApiMetadata;
import org.jclouds.apis.Apis;
import org.jclouds.concurrent.MoreExecutors;
import org.jclouds.concurrent.SingleThreaded;
import org.jclouds.concurrent.config.ConfiguresExecutorService;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.config.BindNameToContext;
import org.jclouds.config.BindPropertiesToExpandedValues;
import org.jclouds.config.BindRestContextWithWildcardExtendsExplicitAndRawType;
import org.jclouds.domain.Credentials;
import org.jclouds.events.config.ConfiguresEventBus;
import org.jclouds.events.config.EventBusModule;
import org.jclouds.http.config.ConfiguresHttpCommandExecutorService;
import org.jclouds.http.config.JavaUrlHttpCommandExecutorServiceModule;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.lifecycle.config.LifeCycleModule;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.jdk.config.JDKLoggingModule;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.providers.Providers;
import org.jclouds.providers.config.BindProviderMetadataContextAndCredentials;
import org.jclouds.providers.internal.UpdateProviderMetadataFromProperties;
import org.jclouds.rest.ConfiguresCredentialStore;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestApiMetadata;
import org.jclouds.rest.config.CredentialStoreModule;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.config.RestModule;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.reflect.TypeToken;
import com.google.common.util.concurrent.ExecutionList;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;

/**
 * Creates {@link Context} or {@link Injector} configured to an api and
 * endpoint. Alternatively, this can be used to make a portable {@link View} of
 * that api.
 * 
 * <br/>
 * ex. to build a {@link RestContext} on a particular endpoint using the typed
 * interface
 * 
 * <pre>
 * context = ContextBuilder.newBuilder(new NovaApiMetadata())
 *                         .endpoint("http://10.10.10.10:5000/v2.0")
 *                         .credentials(user, pass)
 *                         .build(NovaApiMetadata.CONTEXT_TOKEN)
 * </pre>
 * 
 * <br/>
 * ex. to build a {@link View} of a particular backend context, looked up by
 * key.
 * 
 * <pre>
 * context = ContextBuilder.newBuilder("aws-s3")
 *                         .credentials(apikey, secret)
 *                         .buildView(BlobStoreContext.class);
 * </pre>
 * 
 * <h4>Assumptions</h4>
 * 
 * Threadsafe objects will be bound as singletons to the Injector or Context
 * provided.
 * <p/>
 * If no <code>Module</code>s are specified, the default
 * {@link JDKLoggingModule logging} and
 * {@link JavaUrlHttpCommandExecutorServiceModule http transports} will be
 * installed.
 * 
 * @author Adrian Cole, Andrew Newdigate
 * @see Context
 * @see View
 * @see ApiMetadata
 * @see ProviderMetadata
 */
public class ContextBuilder {

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
         builder.putAll("providers", transform(Providers.all(), Providers.idFunction()));
         builder.putAll("apis", transform(Apis.all(), Apis.idFunction()));
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

   protected Optional<String> name = Optional.absent();
   protected Optional<ProviderMetadata> providerMetadata = Optional.absent();
   protected final String providerId;
   protected Optional<String> endpoint = Optional.absent();
   protected Optional<String> identity = Optional.absent();
   @Nullable
   protected String credential;
   protected ApiMetadata apiMetadata;
   protected String apiVersion;
   protected String buildVersion;
   protected Optional<Properties> overrides = Optional.absent();
   protected List<Module> modules = Lists.newArrayListWithCapacity(3);

   @Override
   public String toString() {
      return Objects.toStringHelper("").add("providerMetadata", providerMetadata).add("apiMetadata", apiMetadata)
               .toString();
   }

   protected ContextBuilder(ProviderMetadata providerMetadata) {
      this(providerMetadata, checkNotNull(providerMetadata, "providerMetadata").getApiMetadata());
   }

   protected ContextBuilder(@Nullable ProviderMetadata providerMetadata, ApiMetadata apiMetadata) {
      this.apiMetadata = checkNotNull(apiMetadata, "apiMetadata");
      if (providerMetadata != null) {
         this.providerMetadata = Optional.of(providerMetadata);
         this.endpoint = Optional.of(providerMetadata.getEndpoint());
         this.providerId = providerMetadata.getId();
      } else {
         this.endpoint = apiMetadata.getDefaultEndpoint();
         this.providerId = apiMetadata.getId();
      }
      this.identity = apiMetadata.getDefaultIdentity();
      this.credential = apiMetadata.getDefaultCredential().orNull();
      this.apiVersion = apiMetadata.getVersion();
      this.buildVersion = apiMetadata.getBuildVersion().or("");
   }

   public ContextBuilder(ApiMetadata apiMetadata) {
      this(null, apiMetadata);
   }

   public ContextBuilder name(String name) {
     this.name = Optional.of(checkNotNull(name, "name"));
     return this;
   }

   public ContextBuilder credentials(String identity, @Nullable String credential) {
      this.identity = Optional.of(checkNotNull(identity, "identity"));
      this.credential = credential;
      return this;
   }

   public ContextBuilder endpoint(String endpoint) {
      this.endpoint = Optional.of(checkNotNull(endpoint, "endpoint"));
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
      addAll(this.modules, checkNotNull(modules, "modules"));
      return this;
   }

   public ContextBuilder overrides(Properties overrides) {
      this.overrides = Optional.of(checkNotNull(overrides, "overrides"));
      return this;
   }

   public static String searchPropertiesForProviderScopedProperty(Properties mutable, String prov, String key) throws NoSuchElementException {
      try {
         return find(newArrayList(mutable.getProperty(prov + "." + key), mutable.getProperty("jclouds." + key)),
                  notNull());
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("property %s.%s not present in properties: %s", prov, key, mutable.keySet()));
      } finally {
         mutable.remove(prov + "." + key);
         mutable.remove("jclouds." + key);
      }
   }

   public Injector buildInjector() {

      Properties unexpanded = currentStateToUnexpandedProperties();

      ImmutableSet<String> keysToResolve = ImmutableSet.of(PROPERTY_ENDPOINT, PROPERTY_IDENTITY, PROPERTY_CREDENTIAL,
               PROPERTY_API, PROPERTY_API_VERSION, PROPERTY_BUILD_VERSION);

      ImmutableSet<String> optionalKeys = apiMetadata.getCredentialName().isPresent() ? ImmutableSet.<String> of()
               : ImmutableSet.of(PROPERTY_CREDENTIAL);

      Properties resolved = resolveProperties(unexpanded, providerId, keysToResolve, optionalKeys);

      Properties expanded = expandProperties(resolved);

      Credentials creds = new Credentials(getAndRemove(expanded, PROPERTY_IDENTITY), getAndRemove(expanded,
               PROPERTY_CREDENTIAL));

      ProviderMetadata providerMetadata = new UpdateProviderMetadataFromProperties(apiMetadata, this.providerMetadata).apply(expanded);

      //We use either the specified name (optional) or a hash of provider/api, endpoint, api version & identity. Hash is used to be something readable.
      return buildInjector(name.or(String.valueOf(Objects.hashCode(providerMetadata.getId(),
               providerMetadata.getEndpoint(), providerMetadata.getApiMetadata().getVersion(), creds.identity))), providerMetadata, creds, modules);
   }

   private static String getAndRemove(Properties expanded, String key) {
      try {
         return expanded.getProperty(key);
      } finally {
         expanded.remove(key);
      }
   }
   
   private Properties currentStateToUnexpandedProperties() {
      Properties defaults = new Properties();
      defaults.putAll(apiMetadata.getDefaultProperties());
      defaults.setProperty(PROPERTY_PROVIDER,providerId);
      if (providerMetadata.isPresent()) {
         defaults.putAll(providerMetadata.get().getDefaultProperties());
         defaults.setProperty(PROPERTY_ISO3166_CODES, Joiner.on(',').join(providerMetadata.get().getIso3166Codes()));
      }
      if (endpoint.isPresent())
         defaults.setProperty(PROPERTY_ENDPOINT, endpoint.get());
      defaults.setProperty(PROPERTY_API, apiMetadata.getName());
      defaults.setProperty(PROPERTY_API_VERSION, apiVersion);
      defaults.setProperty(PROPERTY_BUILD_VERSION, buildVersion);
      if (identity.isPresent())
         defaults.setProperty(PROPERTY_IDENTITY, identity.get());
      if (credential != null)
         defaults.setProperty(PROPERTY_CREDENTIAL, credential);
      if (overrides.isPresent())
         defaults.putAll(checkNotNull(overrides.get(), "overrides"));
      defaults.putAll(propertiesPrefixedWithJcloudsApiOrProviderId(getSystemProperties(), apiMetadata.getId(), providerId));
      return defaults;
   }

   @VisibleForTesting
   protected Properties getSystemProperties() {
      return System.getProperties();
   }


   private Properties expandProperties(final Properties resolved) {
      return Guice.createInjector(new BindPropertiesToExpandedValues(resolved)).getInstance(Properties.class);
   }

   public static Injector buildInjector(String name, ProviderMetadata providerMetadata, Credentials creds, List<Module> inputModules) {
      List<Module> modules = newArrayList();
      modules.addAll(inputModules);
      boolean restModuleSpecifiedByUser = restClientModulePresent(inputModules);
      Iterable<Module> defaultModules = ifSpecifiedByUserDontIncludeDefaultRestModule(
               providerMetadata.getApiMetadata(), restModuleSpecifiedByUser);
      addAll(modules, defaultModules);
      addClientModuleIfNotPresent(providerMetadata.getApiMetadata(), modules);
      addRestContextBinding(providerMetadata.getApiMetadata(), modules);
      addLoggingModuleIfNotPresent(modules);
      addHttpModuleIfNeededAndNotPresent(modules);
      addExecutorServiceIfNotPresent(modules);
      addEventBusIfNotPresent(modules);
      addCredentialStoreIfNotPresent(modules);
      modules.add(new LifeCycleModule());
      modules.add(new BindProviderMetadataContextAndCredentials(providerMetadata, creds));
      modules.add(new BindNameToContext(name));
      Injector returnVal = Guice.createInjector(Stage.PRODUCTION, modules);
      returnVal.getInstance(ExecutionList.class).execute();
      return returnVal;
   }

   static Properties resolveProperties(Properties mutable, String providerId, Set<String> keys, Set<String> optionalKeys) throws NoSuchElementException {
      for (String key : keys){
         try {
            String scopedProperty = ImmutableList.copyOf(Splitter.on('.').split(key)).get(1);
            mutable.setProperty(key, searchPropertiesForProviderScopedProperty(mutable, providerId,scopedProperty));
         } catch (NoSuchElementException e){
    if (!optionalKeys.contains(key))
       throw e;
         }
      }
      return mutable;
   }

   static void addRestContextBinding(ApiMetadata apiMetadata, List<Module> modules) {
      if (apiMetadata instanceof RestApiMetadata) {
         try {
            modules.add(new BindRestContextWithWildcardExtendsExplicitAndRawType(RestApiMetadata.class
                     .cast(apiMetadata)));
         } catch (IllegalArgumentException e) {

         }
      }
   }

   static Iterable<Module> ifSpecifiedByUserDontIncludeDefaultRestModule(ApiMetadata apiMetadata,
            boolean restModuleSpecifiedByUser) {
      Iterable<Module> defaultModules = transform(apiMetadata.getDefaultModules(),
               new Function<Class<? extends Module>, Module>() {

                  @Override
                  public Module apply(Class<? extends Module> arg0) {
                     try {
                        return arg0.newInstance();
                     } catch (InstantiationException e) {
                        throw propagate(e);
                     } catch (IllegalAccessException e) {
                        throw propagate(e);
                     }
                  }

               });
      if (restModuleSpecifiedByUser)
         defaultModules = filter(defaultModules, not(configuresRest));
      return defaultModules;
   }

   @SuppressWarnings( { "unchecked" })
   static Map<String, Object> propertiesPrefixedWithJcloudsApiOrProviderId(Properties properties, String apiId,
            String providerId) {
      return Maps.filterKeys(Map.class.cast(properties), containsPattern("^(jclouds|" + providerId + "|" + apiId
               + ").*"));
   }

   @VisibleForTesting
   static void addLoggingModuleIfNotPresent(List<Module> modules) {
      if (!any(modules, instanceOf(LoggingModule.class)))
         modules.add(new JDKLoggingModule());
   }

   @VisibleForTesting
   static void addHttpModuleIfNeededAndNotPresent(List<Module> modules) {
      if (nothingConfiguresAnHttpService(modules))
         modules.add(new JavaUrlHttpCommandExecutorServiceModule());
   }

   static boolean nothingConfiguresAnHttpService(List<Module> modules) {
      return !any(modules, new Predicate<Module>() {
         public boolean apply(Module input) {
            return input.getClass().isAnnotationPresent(ConfiguresHttpCommandExecutorService.class);
         }

      });
   }

   @VisibleForTesting
   static void addClientModuleIfNotPresent(ApiMetadata apiMetadata, List<Module> modules) {
      if (!restClientModulePresent(modules)) {
         addClientModule(apiMetadata, modules);
      }
   }

   static Predicate<Module> configuresRest = new Predicate<Module>() {
      public boolean apply(Module input) {
         return input.getClass().isAnnotationPresent(ConfiguresRestClient.class);
      }

   };

   static boolean restClientModulePresent(List<Module> modules) {
      return any(modules, configuresRest);
   }

   @SuppressWarnings("unchecked")
   static void addClientModule(ApiMetadata apiMetadata, List<Module> modules) {
      // TODO: move this up
      if (apiMetadata instanceof RestApiMetadata) {
         RestApiMetadata rest = RestApiMetadata.class.cast(apiMetadata);
         modules.add(new RestClientModule(TypeToken.of(rest.getApi()), TypeToken.of(rest.getAsyncApi())));
      } else {
         modules.add(new RestModule());
      }
   }

   @VisibleForTesting
   static void addEventBusIfNotPresent(List<Module> modules) {
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
   static void addExecutorServiceIfNotPresent(List<Module> modules) {
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
   static void addCredentialStoreIfNotPresent(List<Module> modules) {
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
   public <C extends Context> C build() {
      return (C) build(apiMetadata.getContext());
   }

   /**
    * @see #buildView(Class)
    */
   public <V extends View> V build(Class<V> viewType) {
     return buildView(checkNotNull(viewType, "viewType"));
   }

   /**
    * @see #buildView(TypeToken)
    */
   public <V extends View> V buildView(Class<V> viewType) {
     return buildView(TypeToken.of(checkNotNull(viewType, "viewType")));
   }
   
   /**
    * this will build any {@link ApiMetadata#getViews() view} supported by the ApiMetadata.
    * 
    * ex. {@code builder.build(BlobStoreContext.class) } will work, if {@code TypeToken<BlobStore>}
    * is a configured {@link ApiMetadata#getViews() view} of this api.
    * 
    */
   @SuppressWarnings("unchecked")
   public <V extends View> V buildView(TypeToken<V> viewType) {
      TypeToken<V> returnType;
      try {
         returnType = (TypeToken<V>) Apis.findView(apiMetadata, checkNotNull(viewType, "viewType"));
      } catch (NoSuchElementException e) {
         throw new IllegalArgumentException(String.format(
                  "api %s not wrappable as %s; context: %s, views: %s", apiMetadata,
                  viewType, apiMetadata.getContext(), apiMetadata.getViews()));
      }
      return (V) buildInjector().getInstance(Key.get(TypeLiteral.get(returnType.getType())));
   }

   /**
    * this will build the {@link ApiMetadata#getContext() context} supported by the current ApiMetadata.
    */
   @SuppressWarnings("unchecked")
   public <C extends Context> C build(TypeToken<C> contextType) {
      TypeToken<C> returnType = null;
      if (contextType.isAssignableFrom(apiMetadata.getContext()))
         returnType = (TypeToken<C>) apiMetadata.getContext();
      else
         throw new IllegalArgumentException(String.format("api %s not assignable from %s; context: %s", apiMetadata,
                  contextType, apiMetadata.getContext()));
      return (C) buildInjector().getInstance(Key.get(TypeLiteral.get(returnType.getType())));
   }

   public ApiMetadata getApiMetadata() {
      return apiMetadata;
   }
}
