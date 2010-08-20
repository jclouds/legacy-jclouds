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

import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ParseSax.Factory;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.internal.NamedResourceImpl;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code OrgHandler}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "vcloud.OrgHandlerTest")
public class OrgHandlerTest {

   public void testApplyInputStream() {
      InputStream is = getClass().getResourceAsStream("/org.xml");

      Injector injector = Guice.createInjector(new SaxParserModule());
      Factory factory = injector.getInstance(ParseSax.Factory.class);

      Org result = (Org) factory.create(injector.getInstance(OrgHandler.class)).parse(is);
      assertEquals(result.getName(), "ExampleOrg");
      assertEquals(result.getDescription(), "Example Corp's Primary Organization.");
      assertEquals(result.getId(), URI.create("http://vcloud.example.com/api/v1.0/org/5"));
      assertEquals(result.getCatalogs(), ImmutableMap.of("Main Catalog", new NamedResourceImpl("Main Catalog",
            CATALOG_XML, URI.create("http://vcloud.example.com/api/v1.0/catalog/32")), "Shared Catalog",
            new NamedResourceImpl("Shared Catalog", CATALOG_XML, URI
                  .create("http://vcloud.example.com/api/v1.0/catalog/37"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("ExampleVdc01", new NamedResourceImpl("ExampleVdc01",
            VCloudMediaType.VDC_XML, URI.create("http://vcloud.example.com/api/v1.0/vdc/5"))));
      assertEquals(result.getNetworks(), ImmutableMap.of("TestNetwork", new NamedResourceImpl("TestNetwork",
            VCloudMediaType.NETWORK_XML, URI.create("http://vcloud.example.com/api/v1.0/network/14")),
            "ProductionNetwork", new NamedResourceImpl("ProductionNetwork", VCloudMediaType.NETWORK_XML, URI
                  .create("http://vcloud.example.com/api/v1.0/network/54"))));
      assertEquals(result.getTasksList(), new NamedResourceImpl(null, TASKSLIST_XML, URI
            .create("http://vcloud.example.com/api/v1.0/tasksList/5")));
   }

}
