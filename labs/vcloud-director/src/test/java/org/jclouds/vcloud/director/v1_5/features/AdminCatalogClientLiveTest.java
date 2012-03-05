/*
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
package org.jclouds.vcloud.director.v1_5.features;

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.MUST_EXIST_FMT;
import static org.testng.Assert.assertEquals;

import org.jclouds.vcloud.director.v1_5.domain.AdminCatalog;
import org.jclouds.vcloud.director.v1_5.domain.Checks;
import org.jclouds.vcloud.director.v1_5.domain.ReferenceType;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

/**
 * Tests live behavior of {@link AdminCatalogClient}.
 * 
 * @author danikov
 */
@Test(groups = { "live", "admin", "catalog" }, singleThreaded = true, testName = "CatalogClientLiveTest")
public class AdminCatalogClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private AdminCatalogClient catalogClient;
   private QueryClient queryClient;

   /*
    * Shared state between dependant tests.
    */
   private ReferenceType<?> catalogRef;
   private AdminCatalog catalog;

   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      catalogClient = context.getApi().getAdminCatalogClient();
      queryClient = context.getApi().getQueryClient();
   }

   @Test(testName = "GET /catalog/{id}")
   public void testGetCatalog() {
      // TODO use property from default property set
      CatalogReferences catalogReferences = queryClient.catalogReferencesQuery(String.format("name==%s", catalogName));
      assertEquals(Iterables.size(catalogReferences.getReferences()), 1, String.format(MUST_EXIST_FMT, catalogName, "Catalog"));
      catalogRef = Iterables.getOnlyElement(catalogReferences.getReferences());
      catalog = catalogClient.getCatalog(catalogRef);
      
      Checks.checkAdminCatalog(catalog);
   }
}
