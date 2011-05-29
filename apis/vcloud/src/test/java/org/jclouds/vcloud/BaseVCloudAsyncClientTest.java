/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.vcloud;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.http.RequiresHttp;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "BaseVCloudAsyncClientTest")
public abstract class BaseVCloudAsyncClientTest<T> extends RestClientTest<T> {

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected Module createModule() {
      return new VCloudRestClientModuleExtension();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      Properties overrides = new Properties();
      overrides.setProperty("vcloud.endpoint", "https://vcenterprise.bluelock.com/api/v1.0");
      return new RestContextFactory().createContextSpec("vcloud", "identity", "credential", overrides);
   }

   @RequiresHttp
   @ConfiguresRestClient
   public static class VCloudRestClientModuleExtension extends VCloudRestClientModule {
      @Override
      protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
               @Named(PROPERTY_API_VERSION) String version) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/login");
      }

      @Override
      protected URI provideOrg(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/org");

      }

      @Override
      protected String provideOrgName(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return "org";
      }

      @Override
      protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) String user) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/catalog");

      }

      @Override
      protected Org provideOrg(CommonVCloudClient discovery) {
         return null;
      }

      @Override
      protected URI provideDefaultTasksList(Org org) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/taskslist");
      }

      @Override
      protected URI provideDefaultVDC(Org org, @org.jclouds.vcloud.endpoints.VDC String defaultVDC) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1");
      }

      @Override
      protected String provideDefaultVDCName(
               @org.jclouds.vcloud.endpoints.VDC Supplier<Map<String, String>> vDCtoOrgSupplier) {
         return "vdc";
      }

      @Override
      protected URI provideDefaultNetwork(URI vdc, Injector injector) {
         return URI.create("https://vcenterprise.bluelock.com/api/v1.0/network/1990");
      }

      @Override
      protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
               final VCloudLoginAsyncClient login) {
         return Suppliers.<VCloudSession> ofInstance(new VCloudSession() {

            @Override
            public Map<String, ReferenceType> getOrgs() {
               return ImmutableMap.<String, ReferenceType> of("org", new ReferenceTypeImpl("org",
                        VCloudMediaType.ORG_XML, URI.create("https://vcenterprise.bluelock.com/api/v1.0/org/1")));
            }

            @Override
            public String getVCloudToken() {
               return "token";
            }

         });

      }

      @Override
      protected void configure() {
         super.configure();
         bind(OrgMapSupplier.class).to(TestOrgMapSupplier.class);
         bind(OrgCatalogSupplier.class).to(TestOrgCatalogSupplier.class);
         bind(OrgCatalogItemSupplier.class).to(TestOrgCatalogItemSupplier.class);
      }

      protected Supplier<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> provideOrgVDCSupplierCache(
               @Named(PROPERTY_SESSION_INTERVAL) long seconds, final OrgVDCSupplier supplier) {

         return Suppliers
                  .<Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>>> ofInstance(ImmutableMap
                           .<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> of(
                                    "org",

                                    ImmutableMap
                                             .<String, org.jclouds.vcloud.domain.VDC> of(
                                                      "vdc",
                                                      new VDCImpl(
                                                               "vdc",
                                                               null,
                                                               URI
                                                                        .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"),
                                                               VDCStatus.READY,
                                                               null,
                                                               "description",
                                                               ImmutableSet.<Task> of(),
                                                               AllocationModel.ALLOCATION_POOL,
                                                               null,
                                                               null,
                                                               null,
                                                               ImmutableMap
                                                                        .<String, ReferenceType> of(
                                                                                 "vapp",
                                                                                 new ReferenceTypeImpl(
                                                                                          "vapp",
                                                                                          "application/vnd.vmware.vcloud.vApp+xml",
                                                                                          URI
                                                                                                   .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/188849-1")),
                                                                                 "network",
                                                                                 new ReferenceTypeImpl(
                                                                                          "network",
                                                                                          "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                                                                          URI
                                                                                                   .create("https://vcenterprise.bluelock.com/api/v1.0/vdcItem/2"))),
                                                               ImmutableMap.<String, ReferenceType> of(), 0, 0, 0,
                                                               false))));

      }

      @Singleton
      public static class TestOrgMapSupplier extends OrgMapSupplier {
         @Inject
         protected TestOrgMapSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Org> get() {
            return ImmutableMap.<String, Org> of("org", new OrgImpl("org", null, URI
                     .create("https://vcenterprise.bluelock.com/api/v1.0/org/1"), "org", "description", ImmutableMap
                     .<String, ReferenceType> of("catalog", new ReferenceTypeImpl("catalog",
                              VCloudMediaType.CATALOG_XML, URI
                                       .create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1"))), ImmutableMap
                     .<String, ReferenceType> of("vdc", new ReferenceTypeImpl("vdc", VCloudMediaType.VDC_XML, URI
                              .create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"))), ImmutableMap
                     .<String, ReferenceType> of("network", new ReferenceTypeImpl("network",
                              VCloudMediaType.NETWORK_XML, URI
                                       .create("https://vcenterprise.bluelock.com/api/v1.0/network/1"))),
                     new ReferenceTypeImpl("tasksList", VCloudMediaType.TASKSLIST_XML, URI
                              .create("https://vcenterprise.bluelock.com/api/v1.0/tasksList/1")), ImmutableList
                              .<Task> of()));
         }
      }

      @Singleton
      public static class TestOrgCatalogSupplier extends OrgCatalogSupplier {
         @Inject
         protected TestOrgCatalogSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>> get() {
            return ImmutableMap.<String, Map<String, ? extends org.jclouds.vcloud.domain.Catalog>> of("org",

            ImmutableMap.<String, org.jclouds.vcloud.domain.Catalog> of("catalog", new CatalogImpl("catalog", "type",
                     URI.create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1"), null, "description",
                     ImmutableMap.<String, ReferenceType> of("item", new ReferenceTypeImpl("item",
                              "application/vnd.vmware.vcloud.catalogItem+xml", URI
                                       .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/1")),
                              "template", new ReferenceTypeImpl("template",
                                       "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                                .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2"))),
                     ImmutableList.<Task> of(), true)));
         }
      }

      @Singleton
      public static class TestOrgCatalogItemSupplier extends OrgCatalogItemSupplier {
         protected TestOrgCatalogItemSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>> get() {
            return ImmutableMap
                     .<String, Map<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>>> of(
                              "org",
                              ImmutableMap
                                       .<String, Map<String, ? extends org.jclouds.vcloud.domain.CatalogItem>> of(
                                                "catalog",
                                                ImmutableMap
                                                         .<String, org.jclouds.vcloud.domain.CatalogItem> of(
                                                                  "template",
                                                                  new CatalogItemImpl(
                                                                           "template",
                                                                           URI
                                                                                    .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2"),
                                                                           "description",
                                                                           new ReferenceTypeImpl(
                                                                                    "template",
                                                                                    "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                                                                    URI
                                                                                             .create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2")),
                                                                           ImmutableMap.<String, String> of()))));

         }
      }

      @Override
      protected Iterable<ReferenceType> provideOrgs(Supplier<VCloudSession> cache, String user) {
         return null;
      }

   }

}
