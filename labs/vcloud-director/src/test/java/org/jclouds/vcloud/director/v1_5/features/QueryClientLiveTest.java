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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
* Tests live behavior of {@link QueryClient}.
* 
* @author grkvlt@apache.org
*/
@Test(groups = { "live", "user", "query" }, singleThreaded = true, testName = "QueryClientLiveTest")
public class QueryClientLiveTest extends BaseVCloudDirectorClientLiveTest {

   /*
    * Convenience references to API clients.
    */

   private QueryClient queryClient;
   private VAppTemplateClient vappTemplateClient;

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      queryClient = context.getApi().getQueryClient();
      vappTemplateClient = context.getApi().getVAppTemplateClient();
   }

   @Test(testName = "GET /catalogs/query")
   public void testQueryAllCatalogs() {
      QueryResultRecords catalogRecords = queryClient.catalogsQueryAll();
      assertFalse(catalogRecords.getRecords().isEmpty(), String.format(NOT_EMPTY_OBJECT_FMT, "CatalogRecord", "QueryResultRecords"));
   }

   @Test(testName = "GET /catalogs/query?format=references", dependsOnMethods = { "testQueryAllCatalogs" })
   public void testQueryAllCatalogReferences() {
      CatalogReferences catalogReferences = queryClient.catalogReferencesQueryAll();
      assertFalse(catalogReferences.getReferences().isEmpty(), String.format(NOT_EMPTY_OBJECT_FMT, "CatalogReference", "CatalogReferences"));
   }
   
   @Test(testName = "GET /vAppTemplates/query")
   public void testQueryAllVAppTemplates() {
      QueryResultRecords queryResult = queryClient.vAppTemplatesQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null));
      assertTrue(hrefs.contains(vAppTemplateURI), "VAppTemplates query result should include vAppTemplate "+vAppTemplateURI+"; but only has "+hrefs);
   }
   
   @Test(testName = "GET /vAppTemplates/query?filter)")
   public void testQueryVAppTemplates() {
      VAppTemplate vAppTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      QueryResultRecords queryResult = queryClient.vAppTemplatesQuery(String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null));
      assertEquals(hrefs, Collections.singleton(vAppTemplateURI), "VAppTemplates query result should have found vAppTemplate "+vAppTemplateURI);
   }

   @Test(testName = "GET /vApps/query")
   public void testQueryAllVApps() {
      // TODO instantiate a vApp, so can assert it's included
      
      QueryResultRecords queryResult = queryClient.vAppsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null));
      //assertTrue(hrefs.contains(vappUri), "VApp query result should include vapp "+vappUri+"; but only has "+hrefs);
   }
   
   @Test(testName = "GET /vms/query")
   public void testQueryAllVms() {
      // TODO instantiate a vApp + vms, so can assert it's included
      
      QueryResultRecords queryResult = queryClient.vmsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VM, null));
      //assertTrue(hrefs.contains(vappUri), "VApp query result should include vapp "+vappUri+"; but only has "+hrefs);
   }
   
   private void assertRecordTypes(QueryResultRecords queryResult, Collection<String> validTypes) {
      for (QueryResultRecordType record : queryResult.getRecords()) {
         assertTrue(validTypes.contains(record.getType()), "invalid type for query result record, "+record.getType()+"; valid types are "+validTypes);
      }
   }
   
   private Set<URI> toHrefs(QueryResultRecords queryResult) {
      Set<URI> hrefs = new LinkedHashSet<URI>();
      for (QueryResultRecordType record : queryResult.getRecords()) {
         hrefs.add(record.getHref());
      }
      return hrefs;
   }
}
