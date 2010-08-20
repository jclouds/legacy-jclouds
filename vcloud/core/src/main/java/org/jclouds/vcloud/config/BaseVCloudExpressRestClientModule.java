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
import org.jclouds.vcloud.VCloudExpressAsyncClient;
import org.jclouds.vcloud.VCloudExpressClient;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.endpoints.TasksList;
import org.jclouds.vcloud.functions.VCloudExpressAllCatalogItemsInCatalog;
import org.jclouds.vcloud.functions.VCloudExpressAllCatalogsInOrganization;
import org.jclouds.vcloud.functions.VCloudExpressAllVDCsInOrganization;
import org.jclouds.vcloud.functions.VCloudExpressOrganizationsForNames;
import org.jclouds.vcloud.functions.VCloudExpressOrganizatonsForLocations;
import org.jclouds.vcloud.functions.VCloudExpressVAppTemplatesForCatalogItems;
import org.jclouds.vcloud.predicates.VCloudExpressTaskSuccess;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

import domain.VCloudExpressLoginAsyncClient;

/**
 * Configures the VCloud authentication service connection, including logging and http transport.
 * 
 * @author Adrian Cole
 */
@RequiresHttp
@ConfiguresRestClient
public abstract class BaseVCloudExpressRestClientModule<S extends VCloudExpressClient, A extends VCloudExpressAsyncClient>
         extends CommonVCloudRestClientModule<S, A> {

   public BaseVCloudExpressRestClientModule(Class<S> syncClientType, Class<A> asyncClientType) {
      super(syncClientType, asyncClientType);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Catalog, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<VCloudExpressAllCatalogItemsInCatalog>() {
      });
      bind(new TypeLiteral<Function<Organization, Iterable<? extends Catalog>>>() {
      }).to(new TypeLiteral<VCloudExpressAllCatalogsInOrganization>() {
      });
      bind(new TypeLiteral<Function<Organization, Iterable<? extends VDC>>>() {
      }).to(new TypeLiteral<VCloudExpressAllVDCsInOrganization>() {
      });
      bind(new TypeLiteral<Function<Iterable<String>, Iterable<? extends Organization>>>() {
      }).to(new TypeLiteral<VCloudExpressOrganizationsForNames>() {
      });
      bind(new TypeLiteral<Function<Iterable<? extends Location>, Iterable<? extends Organization>>>() {
      }).to(new TypeLiteral<VCloudExpressOrganizatonsForLocations>() {
      });
      bind(new TypeLiteral<Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>>>() {
      }).to(new TypeLiteral<VCloudExpressVAppTemplatesForCatalogItems>() {
      });
   }

   @Provides
   @Singleton
   protected Organization provideOrganization(VCloudExpressClient discovery) {
      if (authException.get() != null)
         throw authException.get();
      try {
         return discovery.findOrganizationNamed(null);
      } catch (AuthorizationException e) {
         authException.set(e);
         throw e;
      }
   }

   @Override
   protected URI provideDefaultNetwork(VCloudExpressClient client) {
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

   @Override
   protected Predicate<URI> successTester(Injector injector,
            @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return new RetryablePredicate<URI>(injector.getInstance(VCloudExpressTaskSuccess.class), completed);
   }

   @Provides
   @Singleton
   @org.jclouds.vcloud.endpoints.VDC
   protected Supplier<Map<String, String>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final Supplier<Map<String, ? extends Organization>> orgToVDCSupplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, String>>(authException, seconds,
               new Supplier<Map<String, String>>() {
                  @Override
                  public Map<String, String> get() {
                     Map<String, String> returnVal = newLinkedHashMap();
                     for (Entry<String, ? extends Organization> orgr : orgToVDCSupplier.get().entrySet()) {
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
   protected URI provideDefaultVDC(Organization org) {
      checkState(org.getVDCs().size() > 0, "No vdcs present in org: " + org.getName());
      return get(org.getVDCs().values(), 0).getId();
   }

   @Provides
   @org.jclouds.vcloud.endpoints.Catalog
   @Singleton
   protected URI provideCatalog(Organization org, @Named(PROPERTY_IDENTITY) String user) {
      checkState(org.getCatalogs().size() > 0, "No catalogs present in org: " + org.getName());
      return get(org.getCatalogs().values(), 0).getId();
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends Organization>> provideOrgMapCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrganizationMapSupplier supplier) {
      return new RetryOnTimeOutButNotOnAuthorizationExceptionSupplier<Map<String, ? extends Organization>>(
               authException, seconds, new Supplier<Map<String, ? extends Organization>>() {
                  @Override
                  public Map<String, ? extends Organization> get() {
                     return supplier.get();
                  }

               });
   }

   @Singleton
   public static class OrganizationMapSupplier implements Supplier<Map<String, ? extends Organization>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final Function<Iterable<String>, Iterable<? extends Organization>> organizationsForNames;

      @Inject
      protected OrganizationMapSupplier(Supplier<VCloudSession> sessionSupplier,
               Function<Iterable<String>, Iterable<? extends Organization>> organizationsForNames) {
         this.sessionSupplier = sessionSupplier;
         this.organizationsForNames = organizationsForNames;
      }

      @Override
      public Map<String, ? extends Organization> get() {
         return uniqueIndex(organizationsForNames.apply(sessionSupplier.get().getOrgs().keySet()), name);
      }
   }

   @Singleton
   public static class OrganizationCatalogSupplier implements
            Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> {
      protected final Supplier<Map<String, ? extends Organization>> orgSupplier;
      private final Function<Organization, Iterable<? extends org.jclouds.vcloud.domain.Catalog>> allCatalogsInOrganization;

      @Inject
      protected OrganizationCatalogSupplier(Supplier<Map<String, ? extends Organization>> orgSupplier,
               Function<Organization, Iterable<? extends org.jclouds.vcloud.domain.Catalog>> allCatalogsInOrganization) {
         this.orgSupplier = orgSupplier;
         this.allCatalogsInOrganization = allCatalogsInOrganization;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>> get() {
         return transformValues(
                  transformValues(orgSupplier.get(), allCatalogsInOrganization),
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
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> provideOrganizationCatalogItemMapSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrganizationCatalogSupplier supplier) {
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
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> provideOrganizationVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrganizationVDCSupplier supplier) {
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
   public static class OrganizationVDCSupplier implements
            Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> {
      protected final Supplier<Map<String, ? extends Organization>> orgSupplier;
      private final Function<Organization, Iterable<? extends org.jclouds.vcloud.domain.VDC>> allVDCsInOrganization;

      @Inject
      protected OrganizationVDCSupplier(Supplier<Map<String, ? extends Organization>> orgSupplier,
               Function<Organization, Iterable<? extends org.jclouds.vcloud.domain.VDC>> allVDCsInOrganization) {
         this.orgSupplier = orgSupplier;
         this.allVDCsInOrganization = allVDCsInOrganization;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> get() {
         return transformValues(
                  transformValues(orgSupplier.get(), allVDCsInOrganization),
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
   public static class OrganizationCatalogItemSupplier implements
            Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> {
      protected final Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>>> catalogSupplier;
      private final Function<org.jclouds.vcloud.domain.Catalog, Iterable<? extends CatalogItem>> allCatalogItemsInCatalog;

      @Inject
      protected OrganizationCatalogItemSupplier(
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
   protected Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>>> provideOrganizationCatalogItemSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrganizationCatalogItemSupplier supplier) {
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
   @Singleton
   protected VCloudExpressLoginAsyncClient provideVCloudLogin(AsyncClientFactory factory) {
      return factory.create(VCloudExpressLoginAsyncClient.class);
   }

   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            final VCloudExpressLoginAsyncClient login) {
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

   @Provides
   @TasksList
   @Singleton
   protected URI provideDefaultTasksList(Organization org) {
      checkState(org.getTasksLists().size() > 0, "No tasks lists present in org: " + org.getName());
      return get(org.getTasksLists().values(), 0).getId();
   }
}
