/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Network;
import org.jclouds.vcloud.domain.Organization;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Tests behavior of {@code VCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudClientLiveTest")
public class VCloudExpressClientLiveTest {

   protected VCloudExpressClient connection;
   protected String identity;
   protected RestContext<VCloudExpressClient, VCloudExpressAsyncClient> context;
   private String credential;

   @Test
   public void testOrganization() throws Exception {
      Organization response = connection.findOrganizationNamed(null);
      assertNotNull(response);
      assertNotNull(response.getName());
      assert response.getCatalogs().size() >= 1;
      assert response.getTasksLists().size() >= 1;
      assert response.getVDCs().size() >= 1;
      assertEquals(connection.findOrganizationNamed(response.getName()), response);
   }

   @Test
   public void testCatalog() throws Exception {
      Catalog response = connection.findCatalogInOrgNamed(null, null);
      assertNotNull(response);
      assertNotNull(response.getName());
      assertNotNull(response.getId());
      assertEquals(connection.findCatalogInOrgNamed(null, response.getName()), response);
   }

   @Test
   public void testGetNetwork() throws Exception {
      VDC response = connection.findVDCInOrgNamed(null, null);
      for (NamedResource resource : response.getAvailableNetworks().values()) {
         if (resource.getType().equals(VCloudExpressMediaType.NETWORK_XML)) {
            Network item = connection.getNetwork(resource.getId());
            assertNotNull(item);
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      Catalog response = connection.findCatalogInOrgNamed(null, null);
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudExpressMediaType.CATALOGITEM_XML)) {
            CatalogItem item = connection.findCatalogItemInOrgCatalogNamed(null, null, resource.getName());
            assertNotNull(item);
            assertNotNull(item.getEntity());
            assertNotNull(item.getId());
            assertNotNull(item.getProperties());
            assertNotNull(item.getType());
         }
      }
   }

   @Test
   public void testGetVAppTemplate() throws Exception {
      Catalog response = connection.findCatalogInOrgNamed(null, null);
      for (NamedResource resource : response.values()) {
         if (resource.getType().equals(VCloudExpressMediaType.CATALOGITEM_XML)) {
            CatalogItem item = connection.getCatalogItem(resource.getId());
            if (item.getEntity().getType().equals(VCloudExpressMediaType.VAPPTEMPLATE_XML)) {
               assertNotNull(connection.findVAppTemplateInOrgCatalogNamed(null, null, item.getEntity().getName()));
            }
         }
      }
   }

   @Test
   public void testDefaultVDC() throws Exception {
      VDC response = connection.findVDCInOrgNamed(null, null);
      assertNotNull(response);
      assertNotNull(response.getName());
      assertNotNull(response.getId());
      assertNotNull(response.getResourceEntities());
      assertNotNull(response.getAvailableNetworks());
      assertEquals(connection.getVDC(response.getId()), response);
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.findTasksListInOrgNamed(null, null);
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      assertEquals(connection.getTasksList(response.getLocation()).getLocation(), response.getLocation());
   }

   @Test
   public void testGetTask() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.findTasksListInOrgNamed(null, null);
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      if (response.getTasks().size() > 0) {
         Task task = response.getTasks().last();
         assertEquals(connection.getTask(task.getLocation()).getLocation(), task.getLocation());
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      VDC response = connection.findVDCInOrgNamed(null, null);
      for (NamedResource item : response.getResourceEntities().values()) {
         if (item.getType().equals(VCloudExpressMediaType.VAPP_XML)) {
            VApp app = connection.getVApp(item.getId());
            assertNotNull(app);
         }
      }
   }

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("vcloudexpress.identity"), "vcloudexpress.identity");
      credential = checkNotNull(System.getProperty("vcloudexpress.credential"), "vcloudexpress.credential");
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties props = new Properties();
      context = new RestContextFactory().createContext("vcloudexpress", identity, credential, ImmutableSet
               .<Module> of(new Log4JLoggingModule()), props);

      connection = context.getApi();
   }

}
