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

package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import javax.inject.Singleton;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.jclouds.vcloud.endpoints.VCloudApi;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;

/**
 * Tests behavior of {@code OrgHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.OrgHandlerTest")
public class OrgHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/org.xml");

      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {

         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         @VCloudApi
         URI provide() {
            return URI.create("https://services.vcloudexpress.terremark.com/api/v0.8");
         }

      });
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      Organization result = (Organization) factory.create(injector.getInstance(OrgHandler.class))
               .parse(is);
      assertEquals(result.getName(), "adrian@jclouds.org");
      assertEquals(result.getId(), 48 + "");
      assertEquals(result.getLocation(), URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"));
      assertEquals(result.getCatalogs(),  ImmutableMap.of("Miami Environment 1 Catalog",new NamedResourceImpl("catalog",
               "Miami Environment 1 Catalog", CATALOG_XML,
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("Miami Environment 1", new NamedResourceImpl(
               "32", "Miami Environment 1", VCloudMediaType.VDC_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"))));
      assertEquals(
               result.getTasksLists(),
               ImmutableMap
                        .of(
                                 "Miami Environment 1 Tasks List",
                                 new NamedResourceImpl(
                                          "32",
                                          "Miami Environment 1 Tasks List",
                                          TASKSLIST_XML,
                                          URI
                                                   .create("https://services.vcloudexpress.terremark.com/api/v0.8/tasksList/32"))));
   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/org-hosting.xml");
      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {

         }

         @SuppressWarnings("unused")
         @Provides
         @Singleton
         @VCloudApi
         URI provide() {
            return URI.create("https://vcloud.safesecureweb.com/api/v0.8");
         }

      });
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Organization result = (Organization) factory.create(injector.getInstance(OrgHandler.class))
               .parse(is);
      assertEquals(result.getName(), "Customer 188849");
      assertEquals(result.getId(), 188849 + "");
      assertEquals(result.getLocation(), URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/org/188849"));
      assertEquals(result.getCatalogs(), ImmutableMap.of( "HMS Shared Catalog", new NamedResourceImpl("1", "HMS Shared Catalog",
               CATALOG_XML, URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("188849 Virtual DataCenter",
               new NamedResourceImpl("188849", "188849 Virtual DataCenter",
                        VCloudMediaType.VDC_XML, URI
                                 .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/188849"))));
      assertEquals(result.getTasksLists(), ImmutableMap.of("188849 Task List",
               new NamedResourceImpl("188849", "188849 Task List", TASKSLIST_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/188849"))));
   }
}
