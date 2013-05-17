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
package org.jclouds.trmk.ecloud;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.trmk.ecloud.config.TerremarkECloudRestClientModule;
import org.jclouds.trmk.ecloud.domain.internal.ECloudOrgImpl;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudSession;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogItemImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.VDCImpl;
import org.jclouds.trmk.vcloud_0_8.filters.SetVCloudTokenCookie;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudLoginClient;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudVersionsClient;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * @author Adrian Cole
 */
public abstract class BaseTerremarkECloudAsyncClientTest<T> extends BaseAsyncClientTest<T> {

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), SetVCloudTokenCookie.class);
   }

   @Override
   protected Module createModule() {
      return new TerremarkECloudRestClientModuleExtension();
   }

   @Override
   public ProviderMetadata createProviderMetadata() {
      return new TerremarkECloudProviderMetadata();
   }

   protected static final ReferenceTypeImpl ORG_REF = new ReferenceTypeImpl("org", TerremarkECloudMediaType.ORG_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/1"));

   protected static final ReferenceTypeImpl CATALOG_REF = new ReferenceTypeImpl("catalog",
         TerremarkECloudMediaType.CATALOG_XML, URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"));

   protected static final ReferenceTypeImpl TASKSLIST_REF = new ReferenceTypeImpl("tasksList",
         TerremarkECloudMediaType.TASKSLIST_XML, URI.create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/1"));

   protected static final ReferenceTypeImpl VDC_REF = new ReferenceTypeImpl("vdc", TerremarkECloudMediaType.VDC_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/vdc/1"));

   protected static final ReferenceTypeImpl KEYSLIST_REF = new ReferenceTypeImpl("keysList",
         TerremarkECloudMediaType.KEYSLIST_XML, URI.create("https://vcloud.safesecureweb.com/api/v0.8/keysList/1"));
   protected static final ReferenceTypeImpl TAGSLIST_REF = new ReferenceTypeImpl("deviceTags",
         TerremarkECloudMediaType.TAGSLISTLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/deviceTags/1"));
   protected static final ReferenceTypeImpl VAPPCATALOGLIST_REF = new ReferenceTypeImpl("vappCatalog",
         TerremarkECloudMediaType.VAPPCATALOGLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/vappCatalog/1"));
   protected static final ReferenceTypeImpl DATACENTERSLIST_REF = new ReferenceTypeImpl("dataCentersList",
         TerremarkECloudMediaType.DATACENTERSLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/datacentersList/1"));
   protected static final ReferenceTypeImpl NETWORK_REF = new ReferenceTypeImpl("network",
         TerremarkECloudMediaType.NETWORK_XML, URI.create("https://vcloud.safesecureweb.com/network/1990"));

   protected static final ReferenceTypeImpl PUBLICIPS_REF = new ReferenceTypeImpl("publicIps",
         TerremarkECloudMediaType.PUBLICIPSLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/publicIps/1"));

   protected static final ReferenceTypeImpl INTERNETSERVICES_REF = new ReferenceTypeImpl("internetServices",
         TerremarkECloudMediaType.INTERNETSERVICESLIST_XML,
         URI.create("https://vcloud.safesecureweb.com/api/v0.8/internetServices/1"));

   protected static final Org ORG = new ECloudOrgImpl(ORG_REF.getName(), ORG_REF.getType(), ORG_REF.getHref(), "org",
         ImmutableMap.<String, ReferenceType> of(CATALOG_REF.getName(), CATALOG_REF),
         ImmutableMap.<String, ReferenceType> of(VDC_REF.getName(), VDC_REF), ImmutableMap.<String, ReferenceType> of(
               TASKSLIST_REF.getName(), TASKSLIST_REF), KEYSLIST_REF, DATACENTERSLIST_REF, TAGSLIST_REF,
         VAPPCATALOGLIST_REF);

   protected static final VDC VDC = new VDCImpl(VDC_REF.getName(), VDC_REF.getType(), VDC_REF.getHref(), "description",
         CATALOG_REF, PUBLICIPS_REF, INTERNETSERVICES_REF, ImmutableMap.<String, ReferenceType> of(
               "vapp",
               new ReferenceTypeImpl("vapp", "application/vnd.vmware.vcloud.vApp+xml", URI
                     .create("https://vcloud.safesecureweb.com/api/v0.8/vApp/188849-1")),
               "network",
               new ReferenceTypeImpl("network", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                     .create("https://vcloud.safesecureweb.com/api/v0.8/vdcItem/2"))),
         ImmutableMap.<String, ReferenceType> of(NETWORK_REF.getName(), NETWORK_REF));

      @ConfiguresRestClient
   protected static class TerremarkECloudRestClientModuleExtension extends TerremarkECloudRestClientModule {

      @Override
      protected Supplier<URI> provideAuthenticationURI(TerremarkVCloudVersionsClient versionService, String version) {
         return Suppliers.ofInstance(URI.create("https://vcloud.safesecureweb.com/api/v0.8/login"));
      }

      @Override
      protected Supplier<Org> provideOrg(Supplier<Map<String, ? extends Org>> orgSupplier,
            @org.jclouds.trmk.vcloud_0_8.endpoints.Org Supplier<ReferenceType> defaultOrg) {
         return Suppliers.ofInstance(ORG);
      }

      @Override
      protected void installDefaultVCloudEndpointsModule() {
         install(new AbstractModule() {

            @Override
            protected void configure() {
               TypeLiteral<Supplier<ReferenceType>> refTypeSupplier = new TypeLiteral<Supplier<ReferenceType>>() {
               };
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Org.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(ORG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Catalog.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(CATALOG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.TasksList.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(TASKSLIST_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.VDC.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(VDC_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.trmk.vcloud_0_8.endpoints.Network.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(NETWORK_REF));
            }

         });
      }

      @Override
      protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, TerremarkVCloudLoginClient login) {
         return Suppliers.<VCloudSession> ofInstance(new VCloudSession() {

            @Override
            public Map<String, ReferenceType> getOrgs() {
               return ImmutableMap.<String, ReferenceType> of(ORG_REF.getName(), ORG_REF);
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

      @Override
      protected Supplier<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> provideOrgVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgVDCSupplier supplier) {
         return Suppliers
               .<Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>>> ofInstance(ImmutableMap
                     .<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.VDC>> of(ORG_REF.getName(),
                           ImmutableMap.<String, org.jclouds.trmk.vcloud_0_8.domain.VDC> of(VDC.getName(), VDC)));
      }

      @Singleton
      public static class TestOrgMapSupplier extends OrgMapSupplier {

         @Inject
         protected TestOrgMapSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Org> get() {
            return ImmutableMap.<String, Org> of(ORG.getName(), ORG);
         }
      }

      @Singleton
      public static class TestOrgCatalogSupplier extends OrgCatalogSupplier {
         @Inject
         protected TestOrgCatalogSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> get() {
            return ImmutableMap.<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.Catalog>> of(ORG_REF
                  .getName(), ImmutableMap.<String, org.jclouds.trmk.vcloud_0_8.domain.Catalog> of(
                  CATALOG_REF.getName(),
                  new CatalogImpl(CATALOG_REF.getName(), CATALOG_REF.getType(), CATALOG_REF.getHref(), null,
                        ImmutableMap.<String, ReferenceType> of(
                              "item",
                              new ReferenceTypeImpl("item", "application/vnd.vmware.vcloud.catalogItem+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/1")),
                              "template",
                              new ReferenceTypeImpl("template", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                                    .create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2"))))));
         }
      }

      @Singleton
      public static class TestOrgCatalogItemSupplier extends OrgCatalogItemSupplier {
         protected TestOrgCatalogItemSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>> get() {
            return ImmutableMap
                  .<String, Map<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>>> of(
                        ORG_REF.getName(),
                        ImmutableMap.<String, Map<String, ? extends org.jclouds.trmk.vcloud_0_8.domain.CatalogItem>> of(
                              CATALOG_REF.getName(),
                              ImmutableMap
                                    .<String, org.jclouds.trmk.vcloud_0_8.domain.CatalogItem> of(
                                          "template",
                                          new CatalogItemImpl(
                                                "template",
                                                URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalogItem/2"),
                                                "description",
                                                new ReferenceTypeImpl(
                                                      "template",
                                                      "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                                      URI.create("https://vcloud.safesecureweb.com/api/v0.8/vAppTemplate/2")),
                                                null, null, ImmutableMap.<String, String> of()))));

         }
      }

   }
}
