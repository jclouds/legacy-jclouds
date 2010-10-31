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

package org.jclouds.vcloud.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_DEFAULT_NETWORK;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;
import java.util.SortedMap;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.RequiresHttp;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.logging.Logger;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.vcloud.CommonVCloudAsyncClient;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.VCloudToken;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.Network;
import org.jclouds.vcloud.endpoints.OrgList;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.functions.AllCatalogItemsInCatalog;
import org.jclouds.vcloud.functions.AllCatalogsInOrg;
import org.jclouds.vcloud.functions.AllVDCsInOrg;
import org.jclouds.vcloud.functions.OrgsForLocations;
import org.jclouds.vcloud.functions.OrgsForNames;
import org.jclouds.vcloud.handlers.ParseVCloudErrorFromHttpResponse;
import org.jclouds.vcloud.predicates.TaskSuccess;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import domain.VCloudVersionsAsyncClient;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public class CommonVCloudRestClientModule<S extends CommonVCloudClient, A extends CommonVCloudAsyncClient> extends
         RestClientModule<S, A> {

   public CommonVCloudRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      requestInjection(this);
      super.configure();
      bind(new TypeLiteral<Function<Org, Iterable<? extends Catalog>>>() {
      }).to(new TypeLiteral<AllCatalogsInOrg>() {
      });
      bind(new TypeLiteral<Function<Org, Iterable<? extends VDC>>>() {
      }).to(new TypeLiteral<AllVDCsInOrg>() {
      });
      bind(new TypeLiteral<Function<Iterable<String>, Iterable<? extends Org>>>() {
      }).to(new TypeLiteral<OrgsForNames>() {
      });
      bind(new TypeLiteral<Function<Iterable<? extends Location>, Iterable<? extends Org>>>() {
      }).to(new TypeLiteral<OrgsForLocations>() {
      });
      bind(new TypeLiteral<Function<Catalog, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInCatalog>() {
      });
   }

   @Singleton
   @Provides
   CommonVCloudAsyncClient provideCommonVCloudAsyncClient(A in) {
      return in;
   }

   @Singleton
   @Provides
   CommonVCloudClient provideCommonVCloudClient(S in) {
      return in;
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VDC
   protected Supplier<Map<String, String>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Supplier<Map<String, ? extends Org>> orgToVDCSupplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, String>>(authException, seconds,
               new Supplier<Map<String, String>>() {
                  @Override
                  public Map<String, String> get() {
                     Map<String, String> returnVal = newLinkedHashMap();
                     for (Entry<String, ? extends Org> orgr : orgToVDCSupplier.get().entrySet()) {
                        for (String vdc : orgr.getValue().getVDCs().keySet()) {
                           returnVal.put(vdc, orgr.getKey());
                        }
                     }
                     return returnVal;
                  }
               });

   }

   @Provides
   @org.jclouds.vcloud.endpoints.VDC
   @Singleton
   protected URI provideDefaultVDC(Org org, @org.jclouds.vcloud.endpoints.VDC String defaultVDC) {
      checkState(org.getVDCs().size() > 0, "No vdcs present in org %s", org.getName());
      return checkNotNull(org.getVDCs().get(defaultVDC), "vdc %s not present in org %s", defaultVDC, org.getName())
               .getHref();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.VDC
   @Singleton
   protected String provideDefaultVDCName(@org.jclouds.vcloud.endpoints.VDC Supplier<Map<String, String>> vDCtoOrgSupplier) {
      Map<String, String> vDCtoOrg = vDCtoOrgSupplier.get();
      checkState(vDCtoOrg.keySet().size() > 0, "No vdcs present!");
      return get(vDCtoOrg.keySet(), 0);
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Catalog
   @Singleton
   protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return get(org.getCatalogs().values(), 0).getHref();
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Org>> provideOrgMapCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final OrgMapSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, ? extends Org>>(authException,
               seconds, new Supplier<Map<String, ? extends Org>>() {
                  @Override
                  public Map<String, ? extends Org> get() {
                     return supplier.get();
                  }

               });
   }

   @Provides
   @Singleton
   @OrgList
   URI provideOrgListURI(Supplier<VCloudSession> sessionSupplier) {
      VCloudSession session = sessionSupplier.get();
      return URI.create(Iterables.getLast(session.getOrgs().values()).getHref().toASCIIString().replaceAll("org/.*",
               "org"));
   }

   @Singleton
   public static class OrgMapSupplier implements Supplier<Map<String, ? extends Org>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final Function<Iterable<String>, Iterable<? extends Org>> organizationsForNames;

      @Inject
      protected OrgMapSupplier(Supplier<VCloudSession> sessionSupplier,
               Function<Iterable<String>, Iterable<? extends Org>> organizationsForNames) {
         this.sessionSupplier = sessionSupplier;
         this.organizationsForNames = organizationsForNames;
      }

      @Override
      public Map<String, ? extends Org> get() {
         return uniqueIndex(organizationsForNames.apply(sessionSupplier.get().getOrgs().keySet()), name);
      }
   }

   @Singleton
   public static class OrgCatalogSupplier implements
            Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> {
      protected final Supplier<Map<String, ? extends Org>> orgSupplier;
      private final Function<Org, Iterable<? extends org.jclouds.vcloud.domain.Catalog>> allCatalogsInOrg;

      @Inject
      protected OrgCatalogSupplier(Supplier<Map<String, ? extends Org>> orgSupplier,
               Function<Org, Iterable<? extends org.jclouds.vcloud.domain.Catalog>> allCatalogsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allCatalogsInOrg = allCatalogsInOrg;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>> get() {
         return transformValues(
                  transformValues(orgSupplier.get(), allCatalogsInOrg),
                  new Function<Iterable<? extends org.jclouds.vcloud.domain.Catalog>, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>() {

                     @Override
                     public Map<String, ? extends org.jclouds.vcloud.domain.Catalog> apply(
                              Iterable<? extends org.jclouds.vcloud.domain.Catalog> from) {
                        return uniqueIndex(from, name);
                     }

                  });
      }
   }

   @Resource
   protected Logger logger = Logger.NULL;

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return checkNotNull(cache.get().getVCloudToken(), "No token present in session");
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Org
   @Singleton
   protected URI provideOrg(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
      return getLast(orgs).getHref();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Org
   @Singleton
   protected String provideOrgName(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
      return getLast(orgs).getName();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Org
   @Singleton
   protected Supplier<Map<String, ReferenceType>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final OrgNameToOrgSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, ReferenceType>>(authException,
               seconds, new Supplier<Map<String, ReferenceType>>() {
                  @Override
                  public Map<String, ReferenceType> get() {
                     return supplier.get();
                  }
               });
   }

   @Provides
   @Singleton
   protected Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>> provideURIToVDC(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final URItoVDC supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>>(
               authException, seconds, new Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>>() {
                  @Override
                  public Map<URI, ? extends org.jclouds.vcloud.domain.VDC> get() {
                     return supplier.get();
                  }
               });
   }

   @Singleton
   public static class URItoVDC implements Supplier<Map<URI, ? extends org.jclouds.vcloud.domain.VDC>> {
      private final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap;

      @Inject
      URItoVDC(Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> orgVDCMap) {
         this.orgVDCMap = orgVDCMap;
      }

      @Override
      public Map<URI, ? extends org.jclouds.vcloud.domain.VDC> get() {
         return uniqueIndex(
                  concat(transform(
                           orgVDCMap.get().values(),
                           new Function<Map<String, ? extends org.jclouds.vcloud.domain.VDC>, Iterable<? extends org.jclouds.vcloud.domain.VDC>>() {

                              @Override
                              public Iterable<? extends org.jclouds.vcloud.domain.VDC> apply(
                                       Map<String, ? extends org.jclouds.vcloud.domain.VDC> from) {
                                 return from.values();
                              }

                           })), new Function<org.jclouds.vcloud.domain.VDC, URI>() {

                     @Override
                     public URI apply(org.jclouds.vcloud.domain.VDC from) {
                        return from.getHref();
                     }

                  });
      }

   }

   @Provides
   @org.jclouds.vcloud.endpoints.Org
   @Singleton
   protected Iterable<ReferenceType> provideOrgs(Supplier<VCloudSession> cache, @Named(PROPERTY_IDENTITY) String user) {
      VCloudSession discovery = cache.get();
      checkState(discovery.getOrgs().size() > 0, "No orgs present for user: " + user);
      return discovery.getOrgs().values();
   }

   protected AtomicReference<AuthorizationException> authException = new AtomicReference<AuthorizationException>();

   final static Function<ReferenceType, String> name = new Function<ReferenceType, String>() {

      @Override
      public String apply(ReferenceType from) {
         return from.getName();
      }

   };

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VCloudLogin
   protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
            @Named(PROPERTY_API_VERSION) String version) throws InterruptedException, ExecutionException,
            TimeoutException {
      SortedMap<String, URI> versions = versionService.getSupportedVersions().get(180, TimeUnit.SECONDS);
      checkState(versions.size() > 0, "No versions present");
      checkState(versions.containsKey(version), "version " + version + " not present in: " + versions);
      return versions.get(version);
   }

   @Singleton
   private static class OrgNameToOrgSupplier implements Supplier<Map<String, ReferenceType>> {
      private final Supplier<VCloudSession> sessionSupplier;

      @SuppressWarnings("unused")
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
   protected VCloudVersionsAsyncClient provideVCloudVersions(AsyncClientFactory factory) {
      return factory.create(VCloudVersionsAsyncClient.class);
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Catalog
   @Singleton
   protected String provideCatalogName(
            Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> catalogs) {
      return getLast(getLast(catalogs.get().values()).keySet());
   }

   @Provides
   @Network
   @Singleton
   protected URI provideDefaultNetwork(@org.jclouds.vcloud.endpoints.VDC URI defaultVDC, CommonVCloudClient client,
            Injector injector) {
      if (authException.get() != null)
         throw authException.get();
      try {
         org.jclouds.vcloud.domain.VDC vDC = client.getVDC(defaultVDC);
         Map<String, ReferenceType> networks = vDC.getAvailableNetworks();
         checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
         if (networks.size() == 1)
            return Iterables.getLast(networks.values()).getHref();
         try {
            String networkName = injector.getInstance(Key.get(String.class, Names
                     .named(PROPERTY_VCLOUD_DEFAULT_NETWORK)));
            ReferenceType network = networks.get(networkName);
            checkState(network != null, String.format("network named %s not in %s", networkName, networks.keySet()));
            return network.getHref();
         } catch (ConfigurationException e) {
            // TODO FIXME XXX: In Terremark Enterprise environment with multiple VDC's this does not
            // work well.
            // Each VDC will have differnt network subnets. So we cannot assume the default VDC's
            // networks will
            // work with non-default VDC's. So make PROPERTY_VCLOUD_DEFAULT_NETWORK optional. If
            // this property
            // is not set, they are expected to add NetworkConfig to the options when launching a
            // server.
            return null;
            // throw new
            // IllegalStateException(String.format("you must specify the property %s as one of %s",
            // PROPERTY_VCLOUD_DEFAULT_NETWORK, networks.keySet()), e);
         }
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Provides
   @Singleton
   protected Org provideOrg(CommonVCloudClient discovery) {
      if (authException.get() != null)
         throw authException.get();
      try {
         return discovery.findOrgNamed(null);
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Provides
   @Singleton
   protected Predicate<URI> successTester(Injector injector,
            @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), completed);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> provideOrgCatalogItemMapSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgCatalogSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>>(
               authException, seconds,
               new Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>>() {
                  @Override
                  public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>> get() {
                     return supplier.get();
                  }

               });
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> provideOrgVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgVDCSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>>(
               authException, seconds,
               new Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>>() {
                  @Override
                  public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> get() {
                     return supplier.get();
                  }

               });
   }

   @Singleton
   public static class OrgVDCSupplier implements
            Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> {
      protected final Supplier<Map<String, ? extends Org>> orgSupplier;
      private final Function<Org, Iterable<? extends org.jclouds.vcloud.domain.VDC>> allVDCsInOrg;

      @Inject
      protected OrgVDCSupplier(Supplier<Map<String, ? extends Org>> orgSupplier,
               Function<Org, Iterable<? extends org.jclouds.vcloud.domain.VDC>> allVDCsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allVDCsInOrg = allVDCsInOrg;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> get() {
         return transformValues(
                  transformValues(orgSupplier.get(), allVDCsInOrg),
                  new Function<Iterable<? extends org.jclouds.vcloud.domain.VDC>, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>() {

                     @Override
                     public Map<String, ? extends org.jclouds.vcloud.domain.VDC> apply(
                              Iterable<? extends org.jclouds.vcloud.domain.VDC> from) {
                        return uniqueIndex(from, name);
                     }

                  });
      }
   }

   @Singleton
   public static class OrgCatalogItemSupplier implements
            Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> {
      protected final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> catalogSupplier;
      private final Function<org.jclouds.vcloud.domain.Catalog, Iterable<? extends CatalogItem>> allCatalogItemsInCatalog;

      @Inject
      protected OrgCatalogItemSupplier(
               Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> catalogSupplier,
               Function<org.jclouds.vcloud.domain.Catalog, Iterable<? extends CatalogItem>> allCatalogItemsInCatalog) {
         this.catalogSupplier = catalogSupplier;
         this.allCatalogItemsInCatalog = allCatalogItemsInCatalog;
      }

      @Override
      public Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>> get() {
         return transformValues(
                  catalogSupplier.get(),
                  new Function<Map<String, ? extends org.jclouds.vcloud.domain.Catalog>, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>() {

                     @Override
                     public Map<String, Map<String, ? extends CatalogItem>> apply(
                              Map<String, ? extends org.jclouds.vcloud.domain.Catalog> from) {
                        return transformValues(
                                 from,
                                 new Function<org.jclouds.vcloud.domain.Catalog, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>() {

                                    @Override
                                    public Map<String, ? extends CatalogItem> apply(
                                             org.jclouds.vcloud.domain.Catalog from) {
                                       return uniqueIndex(allCatalogItemsInCatalog.apply(from), name);
                                    }
                                 });

                     }
                  });
      }
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> provideOrgCatalogItemSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgCatalogItemSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>>(
               authException, seconds,
               new Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>>() {
                  @Override
                  public Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>> get() {
                     return supplier.get();
                  }
               });
   }

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(Org org) {
      return org.getTasksList().getHref();
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseVCloudErrorFromHttpResponse.class);
   }
}
