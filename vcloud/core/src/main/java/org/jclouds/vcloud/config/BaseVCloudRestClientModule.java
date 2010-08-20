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

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Throwables.propagate;
import static com.google.common.collect.Iterables.get;
import static com.google.common.collect.Maps.newLinkedHashMap;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.vcloud.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;

import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.http.RequiresHttp;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.rest.AsyncClientFactory;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.suppliers.RetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.vcloud.VCloudAsyncClient;
import org.jclouds.vcloud.VCloudClient;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.functions.AllCatalogItemsInCatalog;
import org.jclouds.vcloud.functions.AllCatalogsInOrg;
import org.jclouds.vcloud.functions.AllVDCsInOrg;
import org.jclouds.vcloud.functions.OrgsForLocations;
import org.jclouds.vcloud.functions.OrgsForNames;
import org.jclouds.vcloud.functions.VAppTemplatesForCatalogItems;
import org.jclouds.vcloud.predicates.TaskSuccess;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import domain.VCloudLoginAsyncClient;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class BaseVCloudRestClientModule<S extends VCloudClient, A extends VCloudAsyncClient> extends
         CommonVCloudRestClientModule<S, A> {

   public BaseVCloudRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Catalog, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInCatalog>() {
      });
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
      bind(new TypeLiteral<Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>>>() {
      }).to(new TypeLiteral<VAppTemplatesForCatalogItems>() {
      });
   }

   @Override
   protected URI provideDefaultNetwork(VCloudClient client) {
      if (authException.get() != null)
         throw authException.get();
      try {
         org.jclouds.vcloud.domain.VDC vDC = client.findVDCInOrgNamed(null, null);
         Map<String, NamedResource> networks = vDC.getAvailableNetworks();
         checkState(networks.size() > 0, "No networks present in vDC: " + vDC.getName());
         return get(networks.values(), 0).getId();
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Provides
   @Singleton
   protected Org provideOrg(VCloudClient discovery) {
      if (authException.get() != null)
         throw authException.get();
      try {
         return discovery.findOrgNamed(null);
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Override
   protected Predicate<URI> successTester(Injector injector,
            @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<URI>(injector.getInstance(TaskSuccess.class), completed);
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
   protected URI provideDefaultVDC(Org org) {
      checkState(org.getVDCs().size() > 0, "No vdcs present in org: " + org.getName());
      return get(org.getVDCs().values(), 0).getId();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Catalog
   @Singleton
   protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return get(org.getCatalogs().values(), 0).getId();
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
      return org.getTasksList().getId();
   }

   @Provides
   @Singleton
   protected VCloudLoginAsyncClient provideVCloudLogin(AsyncClientFactory factory) {
      return factory.create(VCloudLoginAsyncClient.class);
   }

   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final VCloudLoginAsyncClient login) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<VCloudSession>(authException, seconds,
               new Supplier<VCloudSession>() {

                  @Override
                  public VCloudSession get() {
                     try {
                        return login.login().get(10, TimeUnit.SECONDS);
                     } catch (Exception e) {
                        propagate(e);
                        assert false : e;
                        return null;
                     }
                  }

               });
   }
}
