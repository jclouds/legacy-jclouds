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
package org.jclouds.vcloud.internal;

import static org.jclouds.Constants.PROPERTY_SESSION_INTERVAL;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.jclouds.http.HttpRequest;
import org.jclouds.ovf.Envelope;
import org.jclouds.ovf.xml.EnvelopeHandlerTest;
import org.jclouds.providers.AnonymousProviderMetadata;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.ConfiguresRestClient;
import org.jclouds.rest.internal.BaseAsyncClientTest;
import org.jclouds.vcloud.VCloudApiMetadata;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.VCloudVersionsClient;
import org.jclouds.vcloud.config.VCloudRestClientModule;
import org.jclouds.vcloud.domain.AllocationModel;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VCloudSession;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.VDCStatus;
import org.jclouds.vcloud.domain.internal.CatalogImpl;
import org.jclouds.vcloud.domain.internal.CatalogItemImpl;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.domain.internal.VDCImpl;
import org.jclouds.vcloud.filters.SetVCloudTokenCookie;
import org.jclouds.vcloud.xml.VAppTemplateHandlerTest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code VCloudAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "BaseVCloudAsyncClientTest")
public abstract class BaseVCloudAsyncClientTest<T> extends BaseAsyncClientTest<T> {

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
   protected ProviderMetadata createProviderMetadata() {
      return  AnonymousProviderMetadata.forApiWithEndpoint(new VCloudApiMetadata(), "https://vcenterprise.bluelock.com/api/v1.0");
   }
   
   protected static final ReferenceTypeImpl ORG_REF = new ReferenceTypeImpl("org", VCloudMediaType.ORG_XML,
         URI.create("https://vcenterprise.bluelock.com/api/v1.0/org/1"));

   protected static final ReferenceTypeImpl CATALOG_REF = new ReferenceTypeImpl("catalog", VCloudMediaType.CATALOG_XML,
         URI.create("https://vcenterprise.bluelock.com/api/v1.0/catalog/1"));

   protected static final ReferenceTypeImpl TASKSLIST_REF = new ReferenceTypeImpl("tasksList",
         VCloudMediaType.TASKSLIST_XML, URI.create("https://vcenterprise.bluelock.com/api/v1.0/tasksList/1"));

   protected static final ReferenceTypeImpl VDC_REF = new ReferenceTypeImpl("vdc", VCloudMediaType.VDC_XML,
         URI.create("https://vcenterprise.bluelock.com/api/v1.0/vdc/1"));

   protected static final ReferenceTypeImpl NETWORK_REF = new ReferenceTypeImpl("network", VCloudMediaType.NETWORK_XML,
         URI.create("https://vcloud.safesecureweb.com/network/1990"));

   protected static final Org ORG = new OrgImpl(ORG_REF.getName(), ORG_REF.getType(), ORG_REF.getHref(), "org", null,
         ImmutableMap.<String, ReferenceType> of(CATALOG_REF.getName(), CATALOG_REF),
         ImmutableMap.<String, ReferenceType> of(VDC_REF.getName(), VDC_REF), ImmutableMap.<String, ReferenceType> of(
               NETWORK_REF.getName(), NETWORK_REF), TASKSLIST_REF, ImmutableList.<Task> of());

   protected static final VDC VDC = new VDCImpl(VDC_REF.getName(), VDC_REF.getType(), VDC_REF.getHref(),
         VDCStatus.READY, null, "description", ImmutableSet.<Task> of(), AllocationModel.ALLOCATION_POOL, null, null,
         null, ImmutableMap.<String, ReferenceType> of(
               "vapp",
               new ReferenceTypeImpl("vapp", "application/vnd.vmware.vcloud.vApp+xml", URI
                     .create("https://vcenterprise.bluelock.com/api/v1.0/vApp/188849-1")),
               "network",
               new ReferenceTypeImpl("network", "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                     .create("https://vcenterprise.bluelock.com/api/v1.0/vdcItem/2"))),
         ImmutableMap.<String, ReferenceType> of(NETWORK_REF.getName(), NETWORK_REF), 0, 0, 0, false);

      @ConfiguresRestClient
   public static class VCloudRestClientModuleExtension extends VCloudRestClientModule {

      @Override
      protected Supplier<URI> provideAuthenticationURI(VCloudVersionsClient versionService, String version) {
         return Suppliers.ofInstance(URI.create("https://vcenterprise.bluelock.com/api/v1.0/login"));
      }

      @Override
      protected Supplier<Org> provideOrg(Supplier<Map<String, Org>> orgSupplier,
            @org.jclouds.vcloud.endpoints.Org Supplier<ReferenceType> defaultOrg) {
         return Suppliers.ofInstance(ORG);
      }

      @Override
      protected void installDefaultVCloudEndpointsModule() {
         install(new AbstractModule() {

            @Override
            protected void configure() {
               TypeLiteral<Supplier<ReferenceType>> refTypeSupplier = new TypeLiteral<Supplier<ReferenceType>>() {
               };
               bind(refTypeSupplier).annotatedWith(org.jclouds.vcloud.endpoints.Org.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(ORG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.vcloud.endpoints.Catalog.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(CATALOG_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.vcloud.endpoints.TasksList.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(TASKSLIST_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.vcloud.endpoints.VDC.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(VDC_REF));
               bind(refTypeSupplier).annotatedWith(org.jclouds.vcloud.endpoints.Network.class).toInstance(
                     Suppliers.<ReferenceType> ofInstance(NETWORK_REF));
            }

         });
      }

      @Override
      protected Supplier<VCloudSession> provideVCloudTokenCache(@Named(PROPERTY_SESSION_INTERVAL) long seconds,
            AtomicReference<AuthorizationException> authException, final VCloudLoginClient login) {
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
      
      @SuppressWarnings("unchecked")
      @Override
      protected void bindCacheLoaders() {
         bind(new TypeLiteral<CacheLoader<URI, VAppTemplate>>() {
         }).toInstance((CacheLoader) CacheLoader.from(Functions.constant(VAppTemplateHandlerTest.parseTemplate())));

         bind(new TypeLiteral<CacheLoader<URI, Envelope>>() {
         }).toInstance((CacheLoader) CacheLoader.from(Functions.constant(EnvelopeHandlerTest.parseEnvelope())));
      }

      @Override
      protected Supplier<Map<String, Map<String, org.jclouds.vcloud.domain.VDC>>> provideOrgVDCSupplierCache(
            @Named(PROPERTY_SESSION_INTERVAL) long seconds, AtomicReference<AuthorizationException> authException,
            OrgVDCSupplier supplier) {
         return Suppliers.<Map<String, Map<String, org.jclouds.vcloud.domain.VDC>>> ofInstance(ImmutableMap
               .<String, Map<String, org.jclouds.vcloud.domain.VDC>> of(ORG_REF.getName(),
                     ImmutableMap.<String, org.jclouds.vcloud.domain.VDC> of(VDC.getName(), VDC)));
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
         public Map<String, Map<String, org.jclouds.vcloud.domain.Catalog>> get() {
            return ImmutableMap.<String, Map<String, org.jclouds.vcloud.domain.Catalog>> of(
                  ORG_REF.getName(), ImmutableMap.<String, org.jclouds.vcloud.domain.Catalog> of(
                        CATALOG_REF.getName(),
                        new CatalogImpl(CATALOG_REF.getName(), CATALOG_REF.getType(), CATALOG_REF.getHref(), null,
                              "description", ImmutableMap.<String, ReferenceType> of(
                                    "item",
                                    new ReferenceTypeImpl("item", "application/vnd.vmware.vcloud.catalogItem+xml", URI
                                          .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/1")),
                                    "template",
                                    new ReferenceTypeImpl("template", "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI.create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2"))),
                              ImmutableList.<Task> of(), true, false)));
         }
      }

      @Singleton
      public static class TestOrgCatalogItemSupplier extends OrgCatalogItemSupplier {
         protected TestOrgCatalogItemSupplier() {
            super(null, null);
         }

         @Override
         public Map<String, Map<String, Map<String, CatalogItem>>> get() {
            return ImmutableMap.<String, Map<String, Map<String, CatalogItem>>> of(
                  ORG_REF.getName(), ImmutableMap
                        .<String, Map<String, CatalogItem>> of(CATALOG_REF
                              .getName(), ImmutableMap.<String, CatalogItem> of(
                              "template",
                              new CatalogItemImpl("template", URI
                                    .create("https://vcenterprise.bluelock.com/api/v1.0/catalogItem/2"), "description",
                                    new ReferenceTypeImpl("template", "application/vnd.vmware.vcloud.vAppTemplate+xml",
                                          URI.create("https://vcenterprise.bluelock.com/api/v1.0/vAppTemplate/2")),
                                    ImmutableMap.<String, String> of()))));

         }
      }

   }

}
