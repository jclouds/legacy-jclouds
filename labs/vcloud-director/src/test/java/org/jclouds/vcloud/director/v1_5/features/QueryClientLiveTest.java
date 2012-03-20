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
import java.util.List;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.ResourceEntityType.Status;
import org.jclouds.vcloud.director.v1_5.domain.ResourceType;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.UndeployVAppParams;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.query.CatalogReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecordType;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVAppRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVAppTemplateRecord;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultVMRecord;
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorClientLiveTest;
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
   private VAppTemplateClient vappTemplateClient;
   private VAppClient vappClient;

   private VApp vApp;
   
   @AfterClass(groups = { "live" })
   public void cleanUp() throws Exception {
      if (vApp != null) {
         vApp = vappClient.getVApp(vApp.getHref()); // update
         
         // Shutdown and power off the VApp if necessary
         if (vApp.getStatus().equals(Status.POWERED_ON.getValue())) {
            try {
               Task shutdownTask = vappClient.shutdown(vApp.getHref());
               retryTaskSuccess.apply(shutdownTask);
            } catch (Exception e) {
               // keep going; cleanup as much as possible
               logger.warn(e, "Continuing cleanup after error shutting down VApp %s", vApp);
            }
         }

         // Undeploy the VApp if necessary
         if (vApp.isDeployed()) {
            try {
               UndeployVAppParams params = UndeployVAppParams.builder().build();
               Task undeployTask = vappClient.undeploy(vApp.getHref(), params);
               retryTaskSuccess.apply(undeployTask);
            } catch (Exception e) {
               // keep going; cleanup as much as possible
               logger.warn(e, "Continuing cleanup after error undeploying VApp %s", vApp);
            }
         }
         
         Task task = vappClient.deleteVApp(vApp.getHref());
         assertTaskSucceeds(task);
      }
   }

   @Override
   @BeforeClass(inheritGroups = true)
   public void setupRequiredClients() {
      queryClient = context.getApi().getQueryClient();
      vappTemplateClient = context.getApi().getVAppTemplateClient();
      vappClient = context.getApi().getVAppClient();
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
      VAppTemplate vAppTemplate = vappTemplateClient.getVAppTemplate(vAppTemplateURI);
      QueryResultRecords queryResult = queryClient.vAppTemplatesQuery(String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);
      
      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null), QueryResultVAppTemplateRecord.class);
      assertEquals(hrefs, Collections.singleton(vAppTemplateURI), "VAppTemplates query result should have found vAppTemplate "+vAppTemplateURI);
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
      Task task = vappClient.powerOn(vApp.getHref());
      assertTaskSucceedsLong(task);
      
      vApp = vappClient.getVApp(vApp.getHref()); // reload, so it has the VMs
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
   
   private static void assertRecordTypes(QueryResultRecords queryResult, Collection<String> validTypes, Class validClazz) {
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
