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
package org.jclouds.vcloud.xml;

import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.internal.OrgImpl;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code OrgHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class OrgHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/org-1.0.xml");

      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(result.getName(), "ExampleOrg");
      assertEquals(result.getFullName(), "ExampleOrg");
      assertEquals(result.getDescription(), "Example Corp's Primary Organization.");
      assertEquals(result.getHref(), URI.create("http://vcloud.example.com/api/v1.0/org/5"));
      assertEquals(result.getCatalogs(), ImmutableMap.of("Main Catalog", new ReferenceTypeImpl("Main Catalog",
               CATALOG_XML, URI.create("http://vcloud.example.com/api/v1.0/catalog/32")), "Shared Catalog",
               new ReferenceTypeImpl("Shared Catalog", CATALOG_XML, URI
                        .create("http://vcloud.example.com/api/v1.0/catalog/37"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("ExampleVdc01", new ReferenceTypeImpl("ExampleVdc01",
               VCloudMediaType.VDC_XML, URI.create("http://vcloud.example.com/api/v1.0/vdc/5"))));
      assertEquals(result.getNetworks(), ImmutableMap.of("TestNetwork", new ReferenceTypeImpl("TestNetwork",
               VCloudMediaType.NETWORK_XML, URI.create("http://vcloud.example.com/api/v1.0/network/14")),
               "ProductionNetwork", new ReferenceTypeImpl("ProductionNetwork", VCloudMediaType.NETWORK_XML, URI
                        .create("http://vcloud.example.com/api/v1.0/network/54"))));
      assertEquals(result.getTasksList(), new ReferenceTypeImpl(null, TASKSLIST_XML, URI
               .create("http://vcloud.example.com/api/v1.0/tasksList/5")));
   }

   public void testTerremark() {
      InputStream is = getClass().getResourceAsStream("/org.xml");

      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(result.getName(), "adrian@jclouds.org");
      assertEquals(result.getFullName(), "adrian@jclouds.org");
      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/org/48"));
      assertEquals(result.getCatalogs(), ImmutableMap.of("Miami Environment 1 Catalog", new ReferenceTypeImpl(
               "Miami Environment 1 Catalog", CATALOG_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32/catalog"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("Miami Environment 1", new ReferenceTypeImpl(
               "Miami Environment 1", VCloudMediaType.VDC_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8/vdc/32"))));
      assertEquals(result.getTasksList(), new ReferenceTypeImpl("Miami Environment 1 Tasks List", TASKSLIST_XML, URI
               .create("https://services.vcloudexpress.terremark.com/api/v0.8/tasksList/32")));
   }

   public void testHosting() {
      InputStream is = getClass().getResourceAsStream("/org-hosting.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(result.getName(), "Customer 188849");
      assertEquals(result.getFullName(), "Customer 188849");
      assertEquals(result.getHref(), URI.create("https://vcloud.safesecureweb.com/api/v0.8/org/188849"));
      assertEquals(result.getCatalogs(), ImmutableMap.of("HMS Shared Catalog", new ReferenceTypeImpl(
               "HMS Shared Catalog", CATALOG_XML, URI.create("https://vcloud.safesecureweb.com/api/v0.8/catalog/1"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("188849 Virtual DataCenter", new ReferenceTypeImpl(
               "188849 Virtual DataCenter", VCloudMediaType.VDC_XML, URI
                        .create("https://vcloud.safesecureweb.com/api/v0.8/vdc/188849"))));
      assertEquals(result.getTasksList(), new ReferenceTypeImpl("188849 Task List", TASKSLIST_XML, URI
               .create("https://vcloud.safesecureweb.com/api/v0.8/tasksList/188849")));
   }

   public void testSavvis() {
      InputStream is = getClass().getResourceAsStream("/org-savvis.xml");
      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);
      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(result, new OrgImpl("607968.0", null, null, "607968.0", "Gravitant Inc", ImmutableMap
               .<String, ReferenceType> of(), ImmutableMap.<String, ReferenceType> of("GravDataCenter1(Saved)",
               new ReferenceTypeImpl("GravDataCenter1(Saved)", "application/vnd.vmware.vcloud.vdc+xml", URI
                        .create("https://api.symphonyVPDC.savvis.net/rest/api/v0.8/org/607968.0/vdc/2826"))),
               ImmutableMap.<String, ReferenceType> of(), null, ImmutableSet.<Task> of()));

   }
}
