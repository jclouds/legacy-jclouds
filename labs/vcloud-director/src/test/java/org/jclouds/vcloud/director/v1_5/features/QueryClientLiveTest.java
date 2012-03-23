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

import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.ENTITY_EQUAL;
import static org.jclouds.vcloud.director.v1_5.VCloudDirectorLiveTestConstants.NOT_EMPTY_OBJECT_FMT;
import static org.jclouds.vcloud.director.v1_5.domain.Checks.checkEntityType;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Link;
import org.jclouds.vcloud.director.v1_5.domain.ResourceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultMediaRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVAppRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVAppTemplateRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVMRecord;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
import org.jclouds.vcloud.director.v1_5.predicates.ReferencePredicates;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

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
   private VAppTemplateClient vAppTemplateClient;
   private VAppClient vAppClient;

   private VApp vApp;
   
   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (vApp != null) {
         cleanUpVApp(vApp);
      }
   }

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredClients() {
      queryClient = context.getApi().getQueryClient();
      vAppTemplateClient = context.getApi().getVAppTemplateClient();
      vAppClient = context.getApi().getVAppClient();
   }

   @Test(testName = "GET /entity/{id}")
   public void testEntity() {
      // Get a VAppTemplate to look up as an entity
      VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      
      // Method under test
      Entity entity = queryClient.entity(vAppTemplate.getId());
      
      // Check returned entity
      checkEntityType(entity);
      
      // Retrieve and check template using entity link
      Link link = Iterables.find(entity.getLinks(), ReferencePredicates.<Link>typeEquals(VCloudDirectorMediaType.VAPP_TEMPLATE));
      VAppTemplate retrieved = vAppTemplateClient.getVAppTemplate(link.getHref());
      assertEquals(retrieved, vAppTemplate, String.format(ENTITY_EQUAL, "VAppTemplate"));
      
   }

   @Test(testName = "GET /query")
   public void testQuery() {
      VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      QueryResultRecords queryResult = queryClient.query("vAppTemplate", String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null), QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(vAppTemplateURI), "VAppTemplates query result should include vAppTemplate "+vAppTemplateURI+"; but only has "+hrefs);
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
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null), QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(vAppTemplateURI), "VAppTemplates query result should include vAppTemplate "+vAppTemplateURI+"; but only has "+hrefs);
   }
   
   @Test(testName = "GET /vAppTemplates/query?filter)")
   public void testQueryVAppTemplatesWithFilter() {
      VAppTemplate vAppTemplate = vAppTemplateClient.getVAppTemplate(vAppTemplateURI);
      QueryResultRecords queryResult = queryClient.vAppTemplatesQuery(String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null), QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(vAppTemplateURI), "VAppTemplates query result should have found vAppTemplate "+vAppTemplateURI);
   }

   @Test(testName = "GET /vApps/query")
   public void testQueryAllVApps() {
      vApp = instantiateVApp();
      
      QueryResultRecords queryResult = queryClient.vAppsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultVAppRecord.class);
      assertTrue(hrefs.contains(vApp.getHref()), "VApp query result should include vapp "+vApp.getHref()+"; but only has "+hrefs);
   }
   
   @Test(testName = "GET /vApps/query?filter", dependsOnMethods = { "testQueryAllVApps" } )
   public void testQueryVAppsWithFilter() {
      QueryResultRecords queryResult = queryClient.vAppsQuery(String.format("name==%s", vApp.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultVAppRecord.class);
      assertEquals(hrefs, Collections.singleton(vApp.getHref()), "VApps query result should have found vApp "+vApp.getHref());
   }
   
   @Test(testName = "GET /vms/query", dependsOnMethods = { "testQueryAllVApps" } )
   public void testQueryAllVms() {
      // Wait for vApp to have been entirely instantiated
      Task instantiateTask = Iterables.getFirst(vApp.getTasks(), null);
      if (instantiateTask != null) {
         assertTaskSucceedsLong(instantiateTask);
      }

      // Start the vApp so that it has VMs
      Task task = vAppClient.powerOn(vApp.getHref());
      assertTaskSucceedsLong(task);
      
      vApp = vAppClient.getVApp(vApp.getHref()); // reload, so it has the VMs
      List<Vm> vms = vApp.getChildren().getVms();
      Set<URI> vmHrefs = toHrefs(vms);

      // Method under test: do the query
      QueryResultRecords queryResult = queryClient.vmsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VM, null), QueryResultVMRecord.class);
      assertTrue(hrefs.containsAll(vmHrefs), "VMs query result should include vms "+vmHrefs+"; but only has "+hrefs);
   }
   
   @Test(testName = "GET /vms/query?filter", dependsOnMethods = { "testQueryAllVms" } )
   public void testQueryAllVmsWithFilter() {
      List<Vm> vms = vApp.getChildren().getVms();
      Set<URI> vmHrefs = toHrefs(vms);
      
      QueryResultRecords queryResult = queryClient.vmsQuery(String.format("containerName==%s", vApp.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VM, null), QueryResultVMRecord.class);
      assertEquals(hrefs, vmHrefs, "VMs query result should equal vms of vApp "+vApp.getName()+" ("+vmHrefs+"); but only has "+hrefs);
   }
   
   @Test(testName = "GET /mediaList/query")
   public void testQueryAllMedia() {
      QueryResultRecords queryResult = queryClient.mediaListQueryAll();
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultMediaRecord.class);
   }
   
   @Test(testName = "GET /mediaList/query?filter")
   public void testQueryMediaWithFilter() {
      String mediaName = "abc";
      QueryResultRecords queryResult = queryClient.mediaListQuery(String.format("name==%s", mediaName));
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultMediaRecord.class);
   }
   
   private static void assertRecordTypes(QueryResultRecords queryResult, Collection<String> validTypes, Class<?> validClazz) {
      for (QueryResultRecordType record : queryResult.getRecords()) {
         assertTrue(validTypes.contains(record.getType()), "invalid type for query result record, "+record.getType()+"; valid types are "+validTypes);
         assertEquals(record.getClass(), validClazz, "invalid type for query result record, "+record.getClass()+"; expected "+validClazz);
      }
   }
   
   private Set<URI> toHrefs(QueryResultRecords queryResult) {
      Set<URI> hrefs = new LinkedHashSet<URI>();
      for (QueryResultRecordType record : queryResult.getRecords()) {
         hrefs.add(record.getHref());
      }
      return hrefs;
   }
   
   private Set<URI> toHrefs(Iterable<? extends ResourceType> resources) {
      Set<URI> hrefs = new LinkedHashSet<URI>();
      for (ResourceType resource : resources) {
         hrefs.add(resource.getHref());
      }
      return hrefs;
   }
}
