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
package org.jclouds.trmk.vcloud_0_8.compute.suppliers;

import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.compute.domain.Image;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.trmk.vcloud_0_8.TerremarkVCloudClient;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.functions.AllCatalogItemsInOrg;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code VAppTemplatesInOrgs}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "VAppTemplatesInOrgsLiveTest")
public class VAppTemplatesInOrgsLiveTest
extends BaseComputeServiceContextLiveTest {
   
   public VAppTemplatesInOrgsLiveTest() {
      provider = "trmk-vcloudexpress";
   }

   private TerremarkVCloudClient tmClient;
   private VAppTemplatesInOrgs parser;
   private AllCatalogItemsInOrg allCatalogItemsInOrg;

   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      Injector injector = view.utils().injector();

      tmClient = injector.getInstance(TerremarkVCloudClient.class);
      allCatalogItemsInOrg = injector.getInstance(AllCatalogItemsInOrg.class);
      parser = injector.getInstance(VAppTemplatesInOrgs.class);
   }

   @Test
   public void testParseAllImages() {

      Set<? extends Image> images = parser.get();

      Iterable<? extends CatalogItem> templates = allCatalogItemsInOrg.apply(tmClient
               .findOrgNamed(null));

      assertEquals(images.size(), Iterables.size(templates));
      assert images.size() > 0;
   }

}
