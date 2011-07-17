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
package org.jclouds.vcloud.terremark.xml;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.jclouds.vcloud.VCloudMediaType.CATALOG_XML;
import static org.jclouds.vcloud.VCloudMediaType.TASKSLIST_XML;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.VCloudExpressMediaType;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.terremark.TerremarkVCloudPropertiesBuilder;
import org.jclouds.vcloud.terremark.domain.TerremarkOrg;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code TerremarkOrgHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TerremarkOrgHandlerTest")
public class TerremarkOrgHandlerTest extends BaseHandlerTest {
   @Override
   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         @Override
         public void configure() {
            super.configure();
            Properties props = new Properties();
            Names.bindProperties(binder(), checkNotNull(new TerremarkVCloudPropertiesBuilder(props).build(),
                     "properties"));
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/terremark/org.xml");

      TerremarkOrg result = (TerremarkOrg) factory.create(injector.getInstance(TerremarkOrgHandler.class)).parse(is);
      assertEquals(result.getName(), "adrian@jclouds.org");
      assertEquals(result.getHref(), URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/org/48"));
      assertEquals(result.getCatalogs(), ImmutableMap.of("Miami Environment 1 Catalog", new ReferenceTypeImpl(
               "Miami Environment 1 Catalog", CATALOG_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32/catalog"))));
      assertEquals(result.getVDCs(), ImmutableMap.of("Miami Environment 1", new ReferenceTypeImpl(
               "Miami Environment 1", VCloudExpressMediaType.VDC_XML, URI
                        .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32"))));
      assertEquals(result.getTasksLists(), ImmutableMap.of(
            "Miami Environment 1 Tasks List",
            new ReferenceTypeImpl("Miami Environment 1 Tasks List", TASKSLIST_XML, URI
                  .create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/vdc/32/tasksList"))));
      assertEquals(result.getKeys(), new ReferenceTypeImpl("Keys", "application/vnd.tmrk.vcloudExpress.keysList+xml",
               URI.create("https://services.vcloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/org/48/keys")));

   }
}
