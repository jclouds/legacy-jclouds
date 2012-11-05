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
import java.util.List;
import java.util.Set;

import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Resource;
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
import org.jclouds.vcloud.director.v1_5.internal.BaseVCloudDirectorApiLiveTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

/**
 * Tests live behavior of {@link QueryApi}.
 * 
 * @author grkvlt@apache.org
 */
@Test(groups = { "live", "user" }, singleThreaded = true, testName = "QueryApiLiveTest")
public class QueryApiLiveTest extends BaseVCloudDirectorApiLiveTest {

   /*
    * Convenience references to API apis.
    */

   private QueryApi queryApi;
   private VAppApi vAppApi;

   private VApp vApp;

   @AfterClass(alwaysRun = true)
   public void cleanUp() throws Exception {
      if (vApp != null)
         cleanUpVApp(vApp);
   }

   @Override
   @BeforeClass(alwaysRun = true)
   public void setupRequiredApis() {
      queryApi = context.getApi().getQueryApi();
      vAppApi = context.getApi().getVAppApi();
      
      cleanUpVAppTemplateInOrg();
   }

   @Test(description = "GET /query")
   public void testQuery() {
      VAppTemplate vAppTemplate = lazyGetVAppTemplate();
      QueryResultRecords queryResult = queryApi
               .query("vAppTemplate", String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null),
               QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(vAppTemplate.getHref()), "VAppTemplates query result should include vAppTemplate "
               + vAppTemplate.getHref() + "; but only has " + hrefs);
   }

   @Test(description = "GET /catalogs/query")
   public void testQueryAllCatalogs() {
      QueryResultRecords catalogRecords = queryApi.catalogsQueryAll();
      assertFalse(catalogRecords.getRecords().isEmpty(),
               String.format(NOT_EMPTY_OBJECT_FMT, "CatalogRecord", "QueryResultRecords"));
   }

   @Test(description = "GET /catalogs/query?format=references", dependsOnMethods = { "testQueryAllCatalogs" })
   public void testQueryAllCatalogReferences() {
      CatalogReferences catalogReferences = queryApi.catalogReferencesQueryAll();
      assertFalse(catalogReferences.getReferences().isEmpty(),
               String.format(NOT_EMPTY_OBJECT_FMT, "CatalogReference", "CatalogReferences"));
   }

   @Test(description = "GET /vAppTemplates/query")
   public void testQueryAllVAppTemplates() {
      QueryResultRecords queryResult = queryApi.vAppTemplatesQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null),
               QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(lazyGetVAppTemplate().getHref()), "VAppTemplates query result should include vAppTemplate "
               + lazyGetVAppTemplate().getHref() + "; but only has " + hrefs);
   }

   @Test(description = "GET /vAppTemplates/query?filter")
   public void testQueryVAppTemplatesWithFilter() {
      VAppTemplate vAppTemplate = lazyGetVAppTemplate();
      QueryResultRecords queryResult = queryApi.vAppTemplatesQuery(String.format("name==%s", vAppTemplate.getName()));
      Set<URI> hrefs = toHrefs(queryResult);

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP_TEMPLATE, null),
               QueryResultVAppTemplateRecord.class);
      assertTrue(hrefs.contains(vAppTemplate.getHref()), "VAppTemplates query result should have found vAppTemplate "
               + vAppTemplate.getHref());
   }

   @Test(description = "GET /vApps/query")
   public void testQueryAllVApps() {
      vApp = instantiateVApp();
      QueryResultRecords queryResult = queryApi.vAppsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultVAppRecord.class);
      assertTrue(hrefs.contains(vApp.getHref()), "VApp query result should include vapp " + vApp.getHref()
               + "; but only has " + hrefs);
   }

	@Test(description = "GET /vApps/query?filter", dependsOnMethods = { "testQueryAllVApps" })
	public void testQueryVAppsWithFilter() {
		QueryResultRecords queryResult = queryApi.vAppsQuery(String.format(
				"name==%s", vApp.getName()));
		Set<URI> hrefs = toHrefs(queryResult);

		assertRecordTypes(queryResult,
				Arrays.asList(VCloudDirectorMediaType.VAPP, null),
				QueryResultVAppRecord.class);
		String message = "VApps query result should have found vApp "
				+ vApp.getHref();
		assertTrue(
				ImmutableSet.copyOf(hrefs).equals(
						ImmutableSet.of(vApp.getHref())), message);
	}

   @Test(description = "GET /vms/query", dependsOnMethods = { "testQueryAllVApps" })
   public void testQueryAllVms() {
      // Wait for vApp to have been entirely instantiated
      Task instantiateTask = Iterables.getFirst(vApp.getTasks(), null);
      if (instantiateTask != null) {
         assertTaskSucceedsLong(instantiateTask);
      }

      // Start the vApp so that it has VMs
      Task task = vAppApi.powerOn(vApp.getId());
      assertTaskSucceedsLong(task);

      vApp = vAppApi.get(vApp.getId()); // reload, so it has the VMs
      List<Vm> vms = vApp.getChildren().getVms();
      Set<URI> vmHrefs = toHrefs(vms);

      // Method under test: do the query
      QueryResultRecords queryResult = queryApi.vmsQueryAll();
      Set<URI> hrefs = toHrefs(queryResult);

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VM, null), QueryResultVMRecord.class);
      assertTrue(hrefs.containsAll(vmHrefs), "VMs query result should include vms " + vmHrefs + "; but only has "
               + hrefs);
   }

	@Test(description = "GET /vms/query?filter", dependsOnMethods = { "testQueryAllVms" })
	public void testQueryAllVmsWithFilter() {
		List<Vm> vms = vApp.getChildren().getVms();
		Set<URI> vmHrefs = toHrefs(vms);

		QueryResultRecords queryResult = queryApi.vmsQuery(String.format(
				"containerName==%s", vApp.getName()));
		Set<URI> hrefs = toHrefs(queryResult);

		assertRecordTypes(queryResult,
				Arrays.asList(VCloudDirectorMediaType.VM, null),
				QueryResultVMRecord.class);
		String message = "VMs query result should equal vms of vApp "
				+ vApp.getName() + " (" + vmHrefs + "); but only has " + hrefs;
		assertTrue(
				ImmutableSet.copyOf(hrefs).equals(ImmutableSet.copyOf(vmHrefs)),
				message);
	}

   @Test(description = "GET /mediaList/query")
   public void testQueryAllMedia() {
      QueryResultRecords queryResult = queryApi.mediaListQueryAll();

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultMediaRecord.class);
   }

   @Test(description = "GET /mediaList/query?filter")
   public void testQueryMediaWithFilter() {
      String mediaName = "abc";
      QueryResultRecords queryResult = queryApi.mediaListQuery(String.format("name==%s", mediaName));

      assertRecordTypes(queryResult, Arrays.asList(VCloudDirectorMediaType.VAPP, null), QueryResultMediaRecord.class);
   }

   private static void assertRecordTypes(QueryResultRecords queryResult, Collection<String> validTypes,
            Class<?> validClazz) {
      for (QueryResultRecordType record : queryResult.getRecords()) {
         assertTrue(validTypes.contains(record.getType()), "invalid type for query result record, " + record.getType()
                  + "; valid types are " + validTypes);
         assertEquals(record.getClass(), validClazz, "invalid type for query result record, " + record.getClass()
                  + "; expected " + validClazz);
      }
   }

   private Set<URI> toHrefs(QueryResultRecords queryResult) {
      Set<URI> hrefs = Sets.newLinkedHashSet();
      for (QueryResultRecordType record : queryResult.getRecords()) {
         hrefs.add(record.getHref());
      }
      return hrefs;
   }

   private Set<URI> toHrefs(Iterable<? extends Resource> resources) {
      Set<URI> hrefs = Sets.newLinkedHashSet();
      for (Resource resource : resources) {
         hrefs.add(resource.getHref());
      }
      return hrefs;
   }
     
}
