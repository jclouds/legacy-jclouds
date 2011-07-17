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
package org.jclouds.terremark.ecloud;

import static org.jclouds.Constants.PROPERTY_API_VERSION;
import static org.jclouds.Constants.PROPERTY_IDENTITY;
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
import org.jclouds.terremark.ecloud.domain.internal.TerremarkECloudOrgImpl;
import org.jclouds.vcloud.CommonVCloudClient;
import org.jclouds.vcloud.VCloudExpressAsyncClientTest.VCloudRestClientModuleExtension.TestOrgCatalogItemSupplier;
import org.jclouds.vcloud.VCloudExpressAsyncClientTest.VCloudRestClientModuleExtension.TestOrgCatalogSupplier;
import org.jclouds.vcloud.VCloudVersionsAsyncClient;
import org.jclouds.vcloud.config.CommonVCloudRestClientModule.OrgVDCSupplier;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.Capacity;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.terremark.TerremarkECloudMediaType;
import org.jclouds.vcloud.terremark.TerremarkVCloudMediaType;
import org.jclouds.vcloud.terremark.config.TerremarkECloudRestClientModule;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkVDCImpl;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
public abstract class BaseTerremarkECloudAsyncClientTest<T> extends RestClientTest<T> {

   @RequiresHttp
   @ConfiguresRestClient
   protected static class TerremarkVCloudRestClientModuleExtension extends TerremarkECloudRestClientModule {
      @Override
      protected URI provideAuthenticationURI(VCloudVersionsAsyncClient versionService,
            @Named(PROPERTY_API_VERSION) String version) {
         return URI.create("https://vcloud/login");
      }

      @Override
      protected void configure() {
         super.configure();
         bind(OrgNameToKeysListSupplier.class).to(TestOrgNameToKeysListSupplier.class);
         bind(OrgMapSupplier.class).to(TestTerremarkOrgMapSupplier.class);
         bind(OrgCatalogSupplier.class).to(TestOrgCatalogSupplier.class);
         bind(OrgCatalogItemSupplier.class).to(TestOrgCatalogItemSupplier.class);
         bind(OrgVDCSupplier.class).to(TestTerremarkOrgVDCSupplier.class);
      }

      @Singleton
      public static class TestOrgNameToKeysListSupplier extends OrgNameToKeysListSupplier {
         @Inject
         protected TestOrgNameToKeysListSupplier(Supplier<VCloudSession> sessionSupplier) {
            super(sessionSupplier, null);
         }

         @Override
         public Map<String, ReferenceType> get() {
            return Maps.transformValues(sessionSupplier.get().getOrgs(), new Function<ReferenceType, ReferenceType>() {

               @Override
               public ReferenceType apply(ReferenceType from) {
                  return new ReferenceTypeImpl(from.getName(), TerremarkECloudMediaType.KEYSLIST_XML, URI.create(from
                        .getHref().toASCIIString() + "/keysList"));
               }
            });
         }
      }

      @Singleton
      public static class TestTerremarkOrgMapSupplier extends OrgMapSupplier {
         @Inject
         protected TestTerremarkOrgMapSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Org> get() {
            return ImmutableMap.<String, Org> of(
                  "org",
                  new TerremarkECloudOrgImpl("org", null,
                        URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"), null, ImmutableMap
                              .<String, ReferenceType> of(
                                    "catalog",
                                    new ReferenceTypeImpl("catalog", TerremarkECloudMediaType.CATALOG_XML, URI
                                          .create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"))),
                        ImmutableMap.<String, ReferenceType> of(
                              "vdc",
                              new ReferenceTypeImpl("vdc", TerremarkECloudMediaType.VDC_XML, URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"))), ImmutableMap
                              .<String, ReferenceType> of(), ImmutableMap.<String, ReferenceType> of(
                              "tasksList",
                              new ReferenceTypeImpl("tasksList", TerremarkECloudMediaType.TASKSLIST_XML, URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/1"))),
                        new ReferenceTypeImpl("keysList", TerremarkECloudMediaType.KEYSLIST_XML, URI
                              .create("https://vcloud.safesecureweb.com/api/v0.8/keysList/1")), new ReferenceTypeImpl(
                              "deviceTags", TerremarkECloudMediaType.TAGSLISTLIST_XML, URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/deviceTags/1")),
                        new ReferenceTypeImpl("vappCatalog", TerremarkECloudMediaType.VAPPCATALOGLIST_XML, URI
                              .create("https://vcloud.safesecureweb.com/api/v0.8/vappCatalog/1")),
                        new ReferenceTypeImpl("dataCentersList", TerremarkECloudMediaType.DATACENTERSLIST_XML, URI
                              .create("https://vcloud.safesecureweb.com/api/v0.8/datacentersList/1"))));
         }
      }

      @Override
      protected URI provideOrg(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return URI.create("https://org");
      }

      @Override
      protected String provideOrgName(@org.jclouds.vcloud.endpoints.Org Iterable<ReferenceType> orgs) {
         return "org";
      }

      @Override
      protected URI provideCatalog(Org org, @Named(PROPERTY_IDENTITY) String user, WriteableCatalog write) {
         return URI.create("https://catalog");
      }

      @Override
      protected Org provideOrg(CommonVCloudClient discovery) {
         return null;
      }

      @Override
      protected Iterable<ReferenceType> provideOrgs(Supplier<VCloudSession> cache, String user) {
         return null;
      }

      @Override
      protected URI provideDefaultTasksList(Org org) {
         return URI.create("https://taskslist");
      }

      @Override
      protected URI provideDefaultVDC(Org org, @org.jclouds.vcloud.endpoints.VDC String defaultVDC) {
         return URI.create("https://vdc/1");
      }

      @Override
      protected String provideDefaultVDCName(
            @org.jclouds.vcloud.endpoints.VDC Supplier<Map<String, String>> vDCtoOrgSupplier) {
         return "vdc";
      }

      @Override
      protected URI provideDefaultNetwork(URI vdc, Injector injector) {
         return URI.create("https://vcloud.safesecureweb.com/network/1990");
      }
   }

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected Module createModule() {
      return new TerremarkVCloudRestClientModuleExtension();
   }

   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return new RestContextFactory().createContextSpec("trmk-ecloud", "identity", "credential", new Properties());
   }

   @Singleton
   public static class TestTerremarkOrgVDCSupplier extends OrgVDCSupplier {
      @Inject
      protected TestTerremarkOrgVDCSupplier() {
         super(null, null);
      }

      @Override
      public Map<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> get() {
         return ImmutableMap.<String, Map<String, ? extends org.jclouds.vcloud.domain.VDC>> of("org",

         ImmutableMap.<String, org.jclouds.vcloud.domain.VDC> of(
               "vdc",
               new TerremarkVDCImpl("vdc", null, URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"),
                     VDCStatus.READY, null, "description", ImmutableSet.<Task> of(), AllocationModel.UNRECOGNIZED,
                     new Capacity("MB", 0, 0, 0, 0), new Capacity("MB", 0, 0, 0, 0), new Capacity("MB", 0, 0, 0, 0),
                     ImmutableMap.<String, ReferenceType> of(
                           "vapp",
                           new ReferenceTypeImpl("vapp", "application/vnd.vmware.vcloud.vApp+xml", URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1")),
                           "network",
                           new ReferenceTypeImpl("network", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2"))), ImmutableMap
                           .<String, ReferenceType> of(), 0, 0, 0, false, new ReferenceTypeImpl("catalog",
                           TerremarkVCloudMediaType.CATALOG_XML, URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1")),
                     new ReferenceTypeImpl("publicIps", TerremarkVCloudMediaType.PUBLICIPSLIST_XML, URI
                           .create("https://vcloud.safesecureweb.com/api/v0.8/publicIps/1")), new ReferenceTypeImpl(
                           "internetServices", TerremarkVCloudMediaType.INTERNETSERVICESLIST_XML, URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/internetServices/1")))));
      }
   }

}