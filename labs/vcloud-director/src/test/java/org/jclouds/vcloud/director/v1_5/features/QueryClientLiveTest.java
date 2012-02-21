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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.*;
import static org.testng.Assert.*;

import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
* Tests live behavior of {@link QueryClient}.
* 
* @author grkvlt@apache.org
*/
@Test(groups = { "live", "api", "user" }, singleThreaded = true, testName = "QueryClientLiveTest")
public class QueryClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private CatalogClient catalogClient;
   private QueryClient queryClient;

   @BeforeClass(inheritGroups = true)
   @Override
   public void setupRequiredClients() {
      catalogClient = context.getApi().getCatalogClient();
      queryClient = context.getApi().getQueryClient();
   }

   /*
    * Shared state between dependant tests.
    */

   private QueryResultRecords<?> catalogRecords;
   private CatalogReferences catalogReferences;

   @Test(testName = "GET /catalogs/query")
   public void testQueryAllCatalogs() {
      catalogRecords = queryClient.catalogsQueryAll();
      assertFalse(catalogRecords.getRecords().isEmpty(), String.format(NOT_EMPTY_OBJECT_FMT, "CatalogRecord", "QueryResultRecords"));
   }

   @Test(testName = "GET /catalogs/query?format=references", dependsOnMethods = { "testQueryAllCatalogs" })
   public void testQueryAllCatalogReferences() {
      catalogReferences = queryClient.catalogReferencesQueryAll();
      assertFalse(catalogReferences.getReferences().isEmpty(), String.format(NOT_EMPTY_OBJECT_FMT, "CatalogReference", "CatalogReferences"));
   }
}