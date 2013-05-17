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
package org.jclouds.trmk.vcloud_0_8.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.internal.CatalogItemImpl;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.jclouds.trmk.vcloud_0_8.internal.TerremarkVCloudApiMetadata;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSortedMap;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code CatalogItemHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "CatalogItemHandlerTest")
public class CatalogItemHandlerTest extends BaseHandlerTest {
   @Override
   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         @Override
         public void configure() {
            super.configure();
            Names.bindProperties(binder(), TerremarkVCloudApiMetadata.defaultProperties());
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/catalogItem-terremark.xml");

      CatalogItem result = factory.create(
            injector.getInstance(CatalogItemHandler.class)).parse(is);
      assertEquals(
            result,
            new CatalogItemImpl(
                  "Windows Web Server 2008 R2 (64-bit)",
                  URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/22"),
                  null,
                  new ReferenceTypeImpl(
                        "Compute Options",
                        "application/xml",
                        URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/22/options/compute")),
                  new ReferenceTypeImpl(
                        "Customization Options",
                        "application/xml",
                        URI.create("https://services.vcloudexpress.terremark.com/api/v0.8/catalogItem/22/options/customization")),
                  new ReferenceTypeImpl("Windows Web Server 2008 R2 (64-bit)",
                        "application/vnd.vmware.vcloud.vAppTemplate+xml", URI
                              .create("https://services.vcloudexpress.terremark.com/api/v0.8/vAppTemplate/22")),
                  ImmutableSortedMap.of("LicensingCost", "0")));
   }
}
