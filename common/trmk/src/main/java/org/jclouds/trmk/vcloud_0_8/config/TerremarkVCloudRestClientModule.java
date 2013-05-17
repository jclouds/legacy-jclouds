/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloud_0_8.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getLast;
import static com.google.common.collect.Iterables.transform;
import static com.google.common.collect.Maps.transformValues;
import static com.google.common.collect.Maps.uniqueIndex;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.jclouds.rest.config.BinderUtils.bindSyncToAsyncHttpApi;
import static org.jclouds.trmk.vcloud_0_8.reference.VCloudConstants.PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED;
import static org.jclouds.util.Predicates2.retry;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.domain.Location;
import org.jclouds.http.HttpErrorHandler;
import org.jclouds.http.annotation.ClientError;
import org.jclouds.http.annotation.Redirection;
import org.jclouds.http.annotation.ServerError;
import org.jclouds.location.suppliers.ImplicitLocationSupplier;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.config.RestClientModule;
import org.jclouds.rest.suppliers.MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.VCloudToken;
import org.jclouds.trmk.vcloud_0_8.compute.functions.FindLocationForResource;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VAppTemplate;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.endpoints.Keys;
import org.jclouds.trmk.vcloud_0_8.endpoints.Org;
import org.jclouds.trmk.vcloud_0_8.endpoints.OrgList;
import org.jclouds.trmk.vcloud_0_8.endpoints.VCloudLogin;
import org.jclouds.trmk.vcloud_0_8.functions.AllCatalogItemsInCatalog;
import org.jclouds.trmk.vcloud_0_8.functions.AllCatalogItemsInOrg;
import org.jclouds.trmk.vcloud_0_8.functions.AllCatalogsInOrg;
import org.jclouds.trmk.vcloud_0_8.functions.AllVDCsInOrg;
import org.jclouds.trmk.vcloud_0_8.functions.OrgsForLocations;
import org.jclouds.trmk.vcloud_0_8.functions.OrgsForNames;
import org.jclouds.trmk.vcloud_0_8.functions.VAppTemplatesForCatalogItems;
import org.jclouds.trmk.vcloud_0_8.handlers.ParseTerremarkVCloudErrorFromHttpResponse;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudLoginAsyncClient;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudLoginClient;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudVersionsAsyncClient;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudVersionsClient;
import org.jclouds.trmk.vcloud_0_8.location.DefaultVDC;
import org.jclouds.trmk.vcloud_0_8.location.OrgAndVDCToLocationSupplier;
import org.jclouds.trmk.vcloud_0_8.predicates.TaskSuccess;
import org.jclouds.util.Strings2;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;

public class TerremarkVCloudRestClientModule<S, A> extends RestClientModule<S, A> {

   public TerremarkVCloudRestClientModule() {
   }

   public TerremarkVCloudRestClientModule(Map<Class<?>, Class<?>> delegateMap) {
      super(delegateMap);
   }

   @Override
   protected void configure() {
      super.configure();
      bind(new TypeLiteral<Function<Iterable<? extends CatalogItem>, Iterable<? extends VAppTemplate>>>() {
      }).to(new TypeLiteral<VAppTemplatesForCatalogItems>() {
      });
      installDefaultVCloudEndpointsModule();
      bind(new TypeLiteral<Function<ReferenceType, Location>>() {
      }).to(new TypeLiteral<FindLocationForResource>() {
      });
      bind(new TypeLiteral<Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends Catalog>>>() {
      }).to(new TypeLiteral<AllCatalogsInOrg>() {
      });
      bind(new TypeLiteral<Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends VDC>>>() {
      }).to(new TypeLiteral<AllVDCsInOrg>() {
      });
      bind(new TypeLiteral<Function<Iterable<String>, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Org>>>() {
      }).to(new TypeLiteral<OrgsForNames>() {
      });
      bind(
            new TypeLiteral<Function<Iterable<? extends Location>, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Org>>>() {
            }).to(new TypeLiteral<OrgsForLocations>() {
      });
      bind(new TypeLiteral<Function<Catalog, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInCatalog>() {
      });
      bind(new TypeLiteral<Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends CatalogItem>>>() {
      }).to(new TypeLiteral<AllCatalogItemsInOrg>() {
      });
      bindSyncToAsyncHttpApi(binder(), TerremarkVCloudVersionsClient.class, TerremarkVCloudVersionsAsyncClient.class);
      bindSyncToAsyncHttpApi(binder(), TerremarkVCloudLoginClient.class, TerremarkVCloudLoginAsyncClient.class);
   }

   @Provides
   @Singleton
   @org.jclouds.trmk.vcloud_0_8.endpoints.VDC
   protected Supplier<Map<String, String>> provideVDCtoORG(
         Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgNameToOrgSupplier) {
      return Suppliers.compose(
            new Function<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>, Map<String, String>>() {

               @Override
               public Map<String, String> apply(Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org> arg0) {
                  Builder<String, String> returnVal = ImmutableMap.builder();
                  for (Entry<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org> orgr : arg0.entrySet()) {
                     for (String vdc : orgr.getValue().getVDCs().keySet()) {
                        returnVal.put(vdc, orgr.getKey());
                     }
                  }
                  return returnVal.build();
               }
            }, orgNameToOrgSupplier);

   }

   @Provides
   @Singleton
   protected Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> provideOrgMapCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgMapSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   @OrgList
   protected Supplier<URI> provideOrgListURI(Supplier<VCloudSession> sessionSupplier) {
      return Suppliers.compose(new Function<VCloudSession, URI>() {

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
   public static class OrgMapSupplier implements
         Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      protected final Function<Iterable<String>, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> organizationsForNames;

      @Inject
      protected OrgMapSupplier(Supplier<VCloudSession> sessionSupplier,
            Function<Iterable<String>, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> organizationsForNames) {
         this.sessionSupplier = sessionSupplier;
         this.organizationsForNames = organizationsForNames;
      }

      @Override
      public Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org> get() {
         return uniqueIndex(organizationsForNames.apply(sessionSupplier.get().getOrgs().keySet()), name);
      }
   }

   protected void installDefaultVCloudEndpointsModule() {
      install(new DefaultVCloudReferencesModule());
   }

   @Singleton
   public static class OrgCatalogSupplier implements
         Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> {
      protected final Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgSupplier;
      protected final Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> allCatalogsInOrg;

      @Inject
      protected OrgCatalogSupplier(
            Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgSupplier,
            Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> allCatalogsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allCatalogsInOrg = allCatalogsInOrg;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> get() {
         return transformValues(
               transformValues(orgSupplier.get(), allCatalogsInOrg),
               new Function<Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>() {

                  @Override
                  public Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog> apply(
                        Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog> from) {
                     return uniqueIndex(from, name);
                  }

               });
      }
   }

   @VCloudToken
   @Provides
   String provideVCloudToken(Supplier<VCloudSession> cache) {
      return checkNotNull(cache.get().getVCloudToken(), "No token present in session");
   }

   @Provides
   @org.jclouds.trmk.vcloud_0_8.endpoints.Org
   @Singleton
   protected Supplier<Map<String, ReferenceType>> provideVDCtoORG(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
         AtomicReference<AuthorizationException> authException, OrgNameToOrgSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Map<URI, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> provideURIToVDC(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         URItoVDC supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Singleton
   public static class URItoVDC implements Supplier<Map<URI, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> {
      private final Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> orgVDCMap;

      @Inject
      URItoVDC(Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> orgVDCMap) {
         this.orgVDCMap = orgVDCMap;
      }

      @Override
      public Map<URI, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> get() {
         return uniqueIndex(
               concat(transform(
                     orgVDCMap.get().values(),
                     new Function<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>() {

                        @Override
                        public Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> apply(
                              Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> from) {
                           return from.values();
                        }

                     })), new Function<org.jclouds.trmk.vcloud_0_8.domain.VDC, URI>() {

                  @Override
                  public URI apply(org.jclouds.trmk.vcloud_0_8.domain.VDC from) {
                     return from.getHref();
                  }

               });
      }

   }

   static final Function<ReferenceType, String> name = new Function<ReferenceType, String>() {

      @Override
      public String apply(ReferenceType from) {
         return from.getName();
      }

   };


   @Provides
   @Singleton
   @VCloudLogin
   protected Supplier<URI> provideAuthenticationURI(final TerremarkVCloudVersionsClient versionService,
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
   protected Supplier<org.jclouds.trmk.vcloud_0_8.domain.Org> provideOrg(
         final Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgSupplier,
         @org.jclouds.trmk.vcloud_0_8.endpoints.Org Supplier<ReferenceType> defaultOrg) {
      return Suppliers.compose(new Function<ReferenceType, org.jclouds.trmk.vcloud_0_8.domain.Org>() {

         @Override
         public org.jclouds.trmk.vcloud_0_8.domain.Org apply(ReferenceType input) {
            return orgSupplier.get().get(input.getName());

         }
      }, defaultOrg);
   }

   @Provides
   @Singleton
   protected Predicate<URI> successTester(Injector injector,
         @Named(PROPERTY_VCLOUD_TIMEOUT_TASK_COMPLETED) long completed) {
      return retry(injector.getInstance(TaskSuccess.class), completed);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> provideOrgCatalogItemMapSupplierCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgCatalogSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> provideOrgVDCSupplierCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgVDCSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Singleton
   public static class OrgVDCSupplier implements
         Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> {
      protected final Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgSupplier;
      private final Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> allVDCsInOrg;

      @Inject
      protected OrgVDCSupplier(
            Supplier<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Org>> orgSupplier,
            Function<org.jclouds.trmk.vcloud_0_8.domain.Org, Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> allVDCsInOrg) {
         this.orgSupplier = orgSupplier;
         this.allVDCsInOrg = allVDCsInOrg;
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> get() {
         return transformValues(
               transformValues(orgSupplier.get(), allVDCsInOrg),
               new Function<Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>() {

                  @Override
                  public Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> apply(
                        Iterable<? extends org.jclouds.trmk.vcloud_0_8.domain.VDC> from) {
                     return uniqueIndex(from, name);
                  }

               });
      }
   }

   @Singleton
   public static class OrgCatalogItemSupplier implements
         Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>>> {
      protected final Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> catalogSupplier;
      protected final Function<org.jclouds.trmk.vcloud_0_8.domain.Catalog, Iterable<? extends CatalogItem>> allCatalogItemsInCatalog;

      @Inject
      protected OrgCatalogItemSupplier(
            Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>>> catalogSupplier,
            Function<org.jclouds.trmk.vcloud_0_8.domain.Catalog, Iterable<? extends CatalogItem>> allCatalogItemsInCatalog) {
         this.catalogSupplier = catalogSupplier;
         this.allCatalogItemsInCatalog = allCatalogItemsInCatalog;
      }

      @Override
      public Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>> get() {
         return transformValues(
               catalogSupplier.get(),
               new Function<Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>>() {

                  @Override
                  public Map<String, Map<String, ? extends CatalogItem>> apply(
                        Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog> from) {
                     return transformValues(
                           from,
                           new Function<org.jclouds.trmk.vcloud_0_8.domain.Catalog, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>() {

                              @Override
                              public Map<String, ? extends CatalogItem> apply(
                                    org.jclouds.trmk.vcloud_0_8.domain.Catalog from) {
                                 return uniqueIndex(allCatalogItemsInCatalog.apply(from), name);
                              }
                           });

                  }
               });
      }
   }

   @Provides
   @Singleton
   protected Supplier<Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>>> provideOrgCatalogItemSupplierCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgCatalogItemSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   /**
    * 
    * @return a listing of all orgs that the current user has access to.
    */
   @Provides
   @Org
   Map<String, ReferenceType> listOrgs(Supplier<VCloudSession> sessionSupplier) {
      return sessionSupplier.get().getOrgs();
   }


   @Provides
   @Singleton
   protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, final TerremarkVCloudLoginClient login) {
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

   @Singleton
   @Provides
   @Named("CreateInternetService")
   String provideCreateInternetService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateInternetService.xml"));
   }

   @Singleton
   @Provides
   @Named("CreateNodeService")
   String provideCreateNodeService() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateNodeService.xml"));
   }

   @Override
   protected void bindErrorHandlers() {
      bind(HttpErrorHandler.class).annotatedWith(Redirection.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ClientError.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
      bind(HttpErrorHandler.class).annotatedWith(ServerError.class).to(ParseTerremarkVCloudErrorFromHttpResponse.class);
   }

   @Singleton
   public static class OrgNameToKeysListSupplier implements Supplier<Map<String, ReferenceType>> {
      protected final Supplier<VCloudSession> sessionSupplier;
      private final TerremarkVCloudClient client;

      @Inject
      protected OrgNameToKeysListSupplier(Supplier<VCloudSession> sessionSupplier, TerremarkVCloudClient client) {
         this.sessionSupplier = sessionSupplier;
         this.client = client;
      }

      @Override
      public Map<String, ReferenceType> get() {
         return Maps.transformValues(sessionSupplier.get().getOrgs(), new Function<ReferenceType, ReferenceType>() {

            @Override
            public ReferenceType apply(ReferenceType from) {
               return client.findOrgNamed(from.getName()).getKeys();
            }

         });
      }
   }

   @Provides
   @Singleton
   @Keys
   protected Supplier<Map<String, ReferenceType>> provideOrgToKeysListCache(
         @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
         OrgNameToKeysListSupplier supplier) {
      return MemoizedRetryOnTimeOutButNotOnAuthorizationExceptionSupplier.create(authException, supplier, seconds,
               TimeUnit.SECONDS);
   }

   @Singleton
   @Provides
   @Named("CreateKey")
   String provideCreateKey() throws IOException {
      return Strings2.toStringAndClose(getClass().getResourceAsStream("/CreateKey.xml"));
   }

   @Override
   protected void installLocations() {
      super.installLocations();
      bind(ImplicitLocationSupplier.class).to(DefaultVDC.class).in(Scopes.SINGLETON);
      bind(LocationsSupplier.class).to(OrgAndVDCToLocationSupplier.class).in(Scopes.SINGLETON);
   }
}
