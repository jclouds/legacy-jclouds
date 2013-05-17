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
package org.jclouds.trmk.ecloud.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.URI;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.trmk.ecloud.TerremarkECloudApiMetadata;
import org.jclouds.trmk.ecloud.TerremarkECloudMediaType;
import org.jclouds.trmk.ecloud.domain.ECloudOrg;
import org.jclouds.trmk.vcloud_0_8.domain.internal.ReferenceTypeImpl;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Guice;
import com.google.inject.name.Names;

/**
 * Tests behavior of {@code ECloudOrgHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "ECloudOrgHandlerTest")
public class ECloudOrgHandlerTest extends BaseHandlerTest {
   @Override
   @BeforeTest
   protected void setUpInjector() {
      injector = Guice.createInjector(new SaxParserModule() {
         @Override
         public void configure() {
            super.configure();
            Names.bindProperties(binder(),TerremarkECloudApiMetadata.defaultProperties());
         }
      });
      factory = injector.getInstance(ParseSax.Factory.class);
      assert factory != null;
   }

   public void testApplyInputStream() {

      InputStream is = getClass().getResourceAsStream("/org-ecloud.xml");

      ECloudOrg result = (ECloudOrg) factory.create(
            injector.getInstance(ECloudOrgHandler.class)).parse(is);
      assertEquals(result.getName(), "Cloud Conscious, LLC");
      assertEquals(result.getHref(),
            URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/org/1910324"));

      assertEquals(result.getVDCs(), ImmutableMap.of(
            "Cloud Conscious LLC - MIA",
            new ReferenceTypeImpl("Cloud Conscious LLC - MIA", TerremarkECloudMediaType.VDC_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1155")),
            "Cloud Conscious LLC - AMA",
            new ReferenceTypeImpl("Cloud Conscious LLC - AMA", TerremarkECloudMediaType.VDC_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1169"))));

      assertEquals(result.getCatalogs(), ImmutableMap.of(
            "Cloud Conscious LLC - MIA Catalog",
            new ReferenceTypeImpl("Cloud Conscious LLC - MIA Catalog", TerremarkECloudMediaType.CATALOG_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1155/catalog")),
            "Cloud Conscious LLC - AMA Catalog",
            new ReferenceTypeImpl("Cloud Conscious LLC - AMA Catalog", TerremarkECloudMediaType.CATALOG_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1169/catalog"))));

      assertEquals(result.getTasksLists(), ImmutableMap.of(
            "Cloud Conscious LLC - MIA Tasks List",
            new ReferenceTypeImpl("Cloud Conscious LLC - MIA Tasks List", TerremarkECloudMediaType.TASKSLIST_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1155/tasksList")),
            "Cloud Conscious LLC - AMA Tasks List",
            new ReferenceTypeImpl("Cloud Conscious LLC - AMA Tasks List", TerremarkECloudMediaType.TASKSLIST_XML, URI
                  .create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/vdc/1169/tasksList"))));

      assertEquals(
            result.getKeys(),
            new ReferenceTypeImpl(
                  "Keys",
                  "application/vnd.tmrk.ecloud.keysList+xml",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/keys")));

      assertEquals(
            result.getTags(),
            new ReferenceTypeImpl(
                  "Device Tags",
                  "application/vnd.tmrk.ecloud.tagsList+xml",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/deviceTags")));

      assertEquals(
            result.getVAppCatalog(),
            new ReferenceTypeImpl(
                  "VApp Catalog",
                  "application/vnd.tmrk.ecloud.VAppCatalogList+xml",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/vappCatalog")));
      
      assertEquals(
            result.getDataCenters(),
            new ReferenceTypeImpl(
                  "DataCenters",
                  "application/vnd.tmrk.ecloud.dataCentersList+xml",
                  URI.create("https://services.enterprisecloud.terremark.com/api/v0.8b-ext2.8/extensions/org/1910324/dataCenters")));

   }
}
