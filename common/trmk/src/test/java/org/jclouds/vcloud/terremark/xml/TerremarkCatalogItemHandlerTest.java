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
import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.vcloud.domain.internal.ReferenceTypeImpl;
import org.jclouds.vcloud.terremark.TerremarkVCloudPropertiesBuilder;
import org.jclouds.vcloud.terremark.domain.TerremarkCatalogItem;
import org.jclouds.vcloud.terremark.domain.internal.TerremarkCatalogItemImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code TerremarkCatalogItemHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "TerremarkCatalogItemHandlerTest")
public class TerremarkCatalogItemHandlerTest extends BaseHandlerTest {
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

      InputStream is = getClass().getResourceAsStream("/terremark/catalogItem.xml");

      TerremarkCatalogItem result = (TerremarkCatalogItem) factory.create(
            injector.getInstance(TerremarkCatalogItemHandler.class)).parse(is);
      assertEquals(
            result,
            new TerremarkCatalogItemImpl(
                  "CentOS 5.3 (32-bit)",
                  URI.create("https://services.vCloudexpress.terremark.com/api/v0.8a-13ext1.6/catalogItem/37-159"),
                  null,
                  new ReferenceTypeImpl(
                        "Compute Options",
                        "application/vnd.tmrk.vcloudExpress.vappComputeOptionParameters+xml",
                        URI
                              .create("https://services.vCloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/template/37-159/options/compute")),
                  new ReferenceTypeImpl(
                        "Customization Options",
                        "application/vnd.tmrk.vcloudExpress.vappCustomizationParameters+xml",
                        URI
                              .create("https://services.vCloudexpress.terremark.com/api/v0.8a-ext1.6/extensions/template/37-159/options/customization")),
                  new ReferenceTypeImpl("CentOS 5.3 (32-bit)", "application/vnd.vmware.vCloud.vAppTemplate+xml", URI
                        .create("https://services.vCloudexpress.terremark.com/api/v0.8a-ext1.6/vappTemplate/37")),
                  ImmutableMap.<String, String> of("LicensingCost", "0")));
   }
}
