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
package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Predicates.notNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.transformValues;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindClientAndAsyncClient;
import static org.jclouds.util.Maps2.uniqueIndex;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_FENCEMODE;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.cim.xml.ResourceAllocationSettingDataHandler;
import org.jclouds.domain.Location;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.ovf.Envelope;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.util.Suppliers2;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.VCloudVersionsClient;
import org.jclouds.vcloud.compute.functions.FindLocationForResource;
import org.jclouds.vcloud.compute.functions.ValidateVAppTemplateAndReturnEnvelopeOrThrowIllegalArgumentException;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.OrgList;
import org.jclouds.vcloud.features.CatalogAsyncClient;
import org.jclouds.vcloud.features.CatalogClient;
import org.jclouds.vcloud.features.NetworkAsyncClient;
import org.jclouds.vcloud.features.NetworkClient;
import org.jclouds.vcloud.features.OrgAsyncClient;
import org.jclouds.vcloud.features.OrgClient;
import org.jclouds.vcloud.features.TaskAsyncClient;
import org.jclouds.vcloud.features.TaskClient;
import org.jclouds.vcloud.features.VAppAsyncClient;
import org.jclouds.vcloud.features.VAppClient;
import org.jclouds.vcloud.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.features.VAppTemplateClient;
import org.jclouds.vcloud.features.VDCAsyncClient;
import org.jclouds.vcloud.features.VDCClient;
import org.jclouds.vcloud.features.VmAsyncClient;
import org.jclouds.vcloud.features.VmClient;
import org.jclouds.vcloud.functions.AllCatalogItemsInCatalog;
import org.jclouds.vcloud.functions.AllCatalogItemsInOrg;
import org.jclouds.vcloud.functions.AllCatalogsInOrg;
import org.jclouds.vcloud.functions.AllVDCsInOrg;
import org.jclouds.vcloud.functions.DefaultNetworkNameInTemplate;
import org.jclouds.vcloud.functions.OrgsForLocations;
import org.jclouds.vcloud.functions.OrgsForNames;
import org.jclouds.vcloud.functions.VAppTemplatesForCatalogItems;
import org.jclouds.vcloud.handlers.ParseVCloudErrorFromHttpResponse;
import org.jclouds.vcloud.internal.VCloudLoginAsyncClient;
import org.jclouds.vcloud.internal.VCloudLoginClient;
import org.jclouds.vcloud.loaders.OVFLoader;
import org.jclouds.vcloud.loaders.VAppTemplateLoader;
import org.jclouds.vcloud.location.DefaultVDC;
import org.jclouds.vcloud.location.OrgAndVDCToLocationSupplier;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.jclouds.vcloud.xml.ovf.VCloudResourceAllocationSettingDataHandler;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@ConfiguresRestClient
public class VCloudRestClientModule extends RestClientModule<VCloudClient, VCloudAsyncClient> {

   public static final Map<Class<?>, Class<?>> DELEGATE_MAP = ImmutableMap.<Class<?>, Class<?>> builder()//
            .put(VAppTemplateClient.class, VAppTemplateAsyncClient.class)//
            .put(VAppClient.class, VAppAsyncClient.class)//
            .put(VmClient.class, VmAsyncClient.class)//
            .put(CatalogClient.class, CatalogAsyncClient.class)//
            .put(TaskClient.class, TaskAsyncClient.class)//
            .put(VDCClient.class, VDCAsyncClient.class)//
            .put(NetworkClient.class, NetworkAsyncClient.class)//
            .put(OrgClient.class, OrgAsyncClient.class)//
            .build();

   public VCloudRestClientModule() {
      super(DELEGATE_MAP);
   }

   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, final VCloudLoginClient login) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException,
               new Supplier<VCloudSession>() {

                  @Override
                  public VCloudSession get() {
                     return login.login();
                  }
                  
                  @Override
                  public String toString() {
                     return Objects.toStringHelper(login).add("method", "login").toString();
                  }
               }, seconds, TimeUnit.SECONDS);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Iterable<CatalogItem>, Iterable<VAppTemplate>>>() {
      }).to(new TypeLiteral<VAppTemplatesForCatalogItems>() {
      });
      bind(ResourceAllocationSettingDataHandler.class).to(VCloudResourceAllocationSettingDataHandler.class);
      installDefaultVCloudEndpointsModule();
      bind(new TypeLiteral<Function<ReferenceType, Location>>() {
      }).to(new TypeLiteral<FindLocationForResource>() {
      });
      bind(new TypeLiteral<Function<Org, Iterable<Catalog>>>() {
      }).to(new TypeLiteral<AllCatalogsInOrg>() {
      });
      bind(new TypeLiteral<Function<Org, Iterable<VDC>>>() {
      }).to(new TypeLiteral<AllVDCsInOrg>() {
      });
      bind(new TypeLiteral<Function<Iterable<String>, Iterable<Org>>>() {
      }).to(new TypeLiteral<OrgsForNames>() {
      });
      bind(new TypeLiteral<Function<Iterable<Location>, Iterable<Org>>>() {
      }).to(new TypeLiteral<OrgsForLocations>() {
      });
      bind(new TypeLiteral<Function<Catalog, Iterable<CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInCatalog>() {
      });
      bind(new TypeLiteral<Function<Org, Iterable<CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInOrg>() {
      });

      bindCacheLoaders();

      bind(new TypeLiteral<Function<VAppTemplate, String>>() {
      }).annotatedWith(Network.class).to(new TypeLiteral<DefaultNetworkNameInTemplate>() {
      });

      bind(new TypeLiteral<Function<VAppTemplate, Envelope>>() {
      }).to(new TypeLiteral<ValidateVAppTemplateAndReturnEnvelopeOrThrowIllegalArgumentException>() {
      });
      bindClientAndAsyncClient(binder(), VCloudVersionsClient.class, VCloudVersionsAsyncClient.class);
      bindClientAndAsyncClient(binder(), VCloudLoginClient.class, VCloudLoginAsyncClient.class);
   }

   protected void bindCacheLoaders() {
      bind(new TypeLiteral<CacheLoader<URI, VAppTemplate>>() {
      }).to(new TypeLiteral<VAppTemplateLoader>() {
      });

      bind(new TypeLiteral<CacheLoader<URI, Envelope>>() {
      }).to(new TypeLiteral<OVFLoader>() {
      });
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VDC
   protected Supplier<Map<String, String>> provideVDCtoORG(Supplier<Map<String, Org>> orgNameToOrgSuppier) {
      return Suppliers2.compose(new Function<Map<String, Org>, Map<String, String>>() {

         @Override
         public Map<String, String> apply(Map<String, Org> arg0) {
            Builder<String, String> returnVal = ImmutableMap.builder();
            for (Entry<String, Org> orgr : arg0.entrySet()) {
               for (String vdc : orgr.getValue().getVDCs().keySet()) {
                  returnVal.put(vdc, orgr.getKey());
               }
            }
            return returnVal.build();
         }
      }, orgNameToOrgSuppier);

   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Org>> provideOrgMapCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, OrgMapSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @OrgList
   protected Supplier<URI> provideOrgListURI(Supplier<VCloudSession> sessionSupplier) {
      return Suppliers2.compose(new Function<VCloudSession, URI>() {

         @Override
         public URI apply(VCloudSession arg0) {
            return URI.create(getLast(arg0.getOrgs().values()).getHref().toASCIIString().replaceAll("org/.*", "org"));
         }

         @Override
         public String toString() {
            return "orgListURI()";
         }

      }, sessionSupplier);
   }

   @Singleton
   public static class OrgMapSupplier implements Supplier<Map<String, Org>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      protected final Function<Iterable<String>, Iterable<Org>> organizationsForNames;

      @Inject
      protected OrgMapSupplier(Supplier<VCloudSession> sessionSupplier,
               Function<Iterable<String>, Iterable<Org>> organizationsForNames) {
         this.sessionSupplier = sessionSupplier;
         this.organizationsForNames = organizationsForNames;
      }

      @Override
      public Map<String, Org> get() {
         return uniqueIndex(organizationsForNames.apply(sessionSupplier.get().getOrgs().keySet()), name);
      }
   }

   protected void installDefaultVCloudEndpointsModule() {
      install(new DefaultVCloudReferencesModule());
   }

   @Singleton
   public static class OrgCatalogSupplier implements
            Supplier<Map<String, Map<String, Catalog>>> {
      protected final Supplier<Map<String, Org>> orgSupplier;
      protected final Function<Org, Iterable<Catalog>> allCatalogsInOrg;

      @Inject
      protected OrgCatalogSupplier(Supplier<Map<String, Org>> orgSupplier,
               Function<Org, Iterable<Catalog>> allCatalogsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allCatalogsInOrg = allCatalogsInOrg;
      }

      @Override
      public Map<String, Map<String, Catalog>> get() {
         return transformValues(
                  transformValues(orgSupplier.get(), allCatalogsInOrg),
                  new Function<Iterable<? extends Catalog>, 
                  Map<String, Catalog>>() {

                     @Override
                     public Map<String, Catalog> apply(
                              Iterable<? extends Catalog> from) {
                        return uniqueIndex(from, name);
                     }

                  });
      }
   }

   @VCloudToken
   @Provides
   @Singleton
   Supplier<String> provideVCloudToken(Supplier<VCloudSession> cache) {
      return Suppliers2.compose(new Function<VCloudSession, String>() {

         @Override
         public String apply(VCloudSession input) {
            return checkNotNull(input.getVCloudToken(), "No token present in session");
         }
      }, cache);
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Org
   @Singleton
   protected Supplier<Map<String, ReferenceType>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, OrgNameToOrgSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Map<URI, VDC>> provideURIToVDC(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            URItoVDC supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Singleton
   public static class URItoVDC implements Supplier<Map<URI, VDC>> {
      private final Supplier<Map<String, Map<String, VDC>>> orgVDCMap;

      @Inject
      URItoVDC(Supplier<Map<String, Map<String, VDC>>> orgVDCMap) {
         this.orgVDCMap = orgVDCMap;
      }

      @Override
      public Map<URI, VDC> get() {
         return uniqueIndex(concat(transform(orgVDCMap.get().values(),
                  new Function<Map<String, VDC>, Iterable<VDC>>() {

                     @Override
                     public Iterable<VDC> apply(
                              Map<String, VDC> from) {
                        return from.values();
                     }

                  })), new Function<VDC, URI>() {

            @Override
            public URI apply(VDC from) {
               return from.getHref();
            }

         });
      }

   }

   final static Function<ReferenceType, String> name = new Function<ReferenceType, String>() {

      @Override
      public String apply(ReferenceType from) {
         return from.getName();
      }

   };

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VCloudLogin
   protected Supplier<URI> provideAuthenticationURI(final VCloudVersionsClient versionService,
            @ApiVersion final String version) {
      return new Supplier<URI>() {

         @Override
         public URI get() {
            SortedMap<String, URI> versions = versionService.getSupportedVersions();
            checkState(versions.size() > 0, "No versions present");
            checkState(versions.containsKey(version), "version " + version + " not present in: " + versions);
            return versions.get(version);
         }

         public String toString() {
            return "login()";
         }
      };
   }

   @Singleton
   private static class OrgNameToOrgSupplier implements Supplier<Map<String, ReferenceType>> {
      private final Supplier<VCloudSession> sessionSupplier;

      @Inject
      OrgNameToOrgSupplier(Supplier<VCloudSession> sessionSupplier) {
         this.sessionSupplier = sessionSupplier;
      }

      @Override
      public Map<String, ReferenceType> get() {
         return sessionSupplier.get().getOrgs();
      }

   }

   @Provides
   @Singleton
   protected Supplier<Org> provideOrg(final Supplier<Map<String, Org>> orgSupplier,
         @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg) {
      return Suppliers2.compose(new Function<ReferenceType, Org>() {

         @Override
         public Org apply(ReferenceType input) {
            return orgSupplier.get().get(input.getName());

         }
      }, defaultOrg);
   }

   @Provides
   @Singleton
   protected Predicate<URI> successTester(Injector injector,
            @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), completed);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, Catalog>>> provideOrgCatalogItemMapSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgCatalogSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, VDC>>> provideOrgVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgVDCSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Singleton
   public static class OrgVDCSupplier implements Supplier<Map<String, Map<String, VDC>>> {
      protected final Supplier<Map<String, Org>> orgSupplier;
      private final Function<Org, Iterable<VDC>> allVDCsInOrg;

      @Inject
      protected OrgVDCSupplier(Supplier<Map<String, Org>> orgSupplier,
               Function<Org, Iterable<VDC>> allVDCsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allVDCsInOrg = allVDCsInOrg;
      }

      @Override
      public Map<String, Map<String, VDC>> get() {
         return transformValues(transformValues(orgSupplier.get(), allVDCsInOrg),
                  new Function<Iterable<? extends VDC>, Map<String, VDC>>() {

                     @Override
                     public Map<String, VDC> apply(
                              Iterable<? extends VDC> from) {
                        return uniqueIndex(Lists.newArrayList(from), name);
                     }

                  });
      }
   }

   @Singleton
   public static class OrgCatalogItemSupplier implements
            Supplier<Map<String, Map<String, Map<String, CatalogItem>>>> {
      protected final Supplier<Map<String, Map<String, Catalog>>> catalogSupplier;
      protected final Function<Catalog, Iterable<CatalogItem>> allCatalogItemsInCatalog;

      @Inject
      protected OrgCatalogItemSupplier(
               Supplier<Map<String, Map<String, Catalog>>> catalogSupplier,
               Function<Catalog, Iterable<CatalogItem>> allCatalogItemsInCatalog) {
         this.catalogSupplier = catalogSupplier;
         this.allCatalogItemsInCatalog = allCatalogItemsInCatalog;
      }

      @Override
      public Map<String, Map<String, Map<String, CatalogItem>>> get() {
         return transformValues(
                  catalogSupplier.get(),
                  new Function<Map<String, Catalog>, Map<String, Map<String, CatalogItem>>>() {

                     @Override
                     public Map<String, Map<String, CatalogItem>> apply(
                              Map<String, Catalog> from) {
                        return transformValues(
                                 from,
                                 new Function<Catalog, Map<String, CatalogItem>>() {

                                    @Override
                                    public Map<String, CatalogItem> apply(Catalog from) {
                                       return uniqueIndex(filter(allCatalogItemsInCatalog.apply(from), notNull()), name);
                                    }
                                 });

                     }
                  });
      }
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, Map<String, CatalogItem>>>> provideOrgCatalogItemSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgCatalogItemSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected FenceMode defaultFenceMode(@Named(PROPERTY_VCLOUD_DEFAULT_FENCEMODE) String fenceMode) {
      return FenceMode.fromValue(fenceMode);
   }

   @Provides
   @Singleton
   protected LoadingCache<URI, VAppTemplate> vAppTemplates(CacheLoader<URI, VAppTemplate> vAppTemplates) {
      return CacheBuilder.newBuilder().build(vAppTemplates);
   }

   @Provides
   @Singleton
   protected LoadingCache<URI, Envelope> envelopes(CacheLoader<URI, Envelope> envelopes) {
      return CacheBuilder.newBuilder().build(envelopes);
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseVCloudErrorFromHttpResponse.class);
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(DefaultVDC.class).in(Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(OrgAndVDCToLocationSupplier.class).in(Scopes.SINGLETON);
   }
}
