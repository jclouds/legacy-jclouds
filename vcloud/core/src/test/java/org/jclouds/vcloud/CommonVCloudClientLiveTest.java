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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.network.OrgNetwork;
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
public abstract class CommonVCloudClientLiveTest<S extends CommonVCloudClient, A extends CommonVCloudAsyncClient> {

   protected S connection;
   protected String provider;
   protected String identity;
   protected RestContext<S, A> context;
   protected String credential;

   @Test
   public void testOrg() throws Exception {
      Org response = connection.findOrgNamed(null);
      assertNotNull(response);
      assertNotNull(response.getName());
      assert response.getCatalogs().size() >= 1;
      assert response.getTasksList() != null;
      assert response.getVDCs().size() >= 1;
      assertEquals(connection.findOrgNamed(response.getName()), response);
   }

   @Test
   public void testCatalog() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getId());
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getId());
         assertEquals(connection.findCatalogInOrgNamed(null, response.getName()), response);
      }
   }

   @Test
   public void testGetOrgNetwork() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource resource : org.getNetworks().values()) {
         if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
            OrgNetwork item = connection.getNetwork(resource.getId());
            assertNotNull(item);
         }
      }
   }

   @Test
   public void testGetVDCNetwork() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getId());
         for (NamedResource resource : response.getAvailableNetworks().values()) {
            if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
               OrgNetwork item = connection.getNetwork(resource.getId());
               assertNotNull(item);
            }
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getId());
         for (NamedResource resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getId());
               verifyCatalogItem(item);
            }
         }
      }
   }

   protected void verifyCatalogItem(CatalogItem item) {
      assertNotNull(item);
      assertNotNull(item);
      assertNotNull(item.getEntity());
      assertNotNull(item.getId());
      assertNotNull(item.getProperties());
      assertNotNull(item.getType());
   }

   @Test
   public void testFindCatalogItem() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getId());
         for (NamedResource resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.findCatalogItemInOrgCatalogNamed(org.getName(), response.getName(),
                     resource.getName());
               verifyCatalogItem(item);
            }
         }
      }
   }


   @Test
   public void testDefaultVDC() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getId());
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getId());
         assertNotNull(response.getResourceEntities());
         assertNotNull(response.getAvailableNetworks());
         assertEquals(connection.getVDC(response.getId()), response);
      }
   }

   @Test
   public void testDefaultTasksList() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.findTasksListInOrgNamed(null);
      assertNotNull(response);
      assertNotNull(response.getLocation());
      assertNotNull(response.getTasks());
      assertEquals(connection.getTasksList(response.getLocation()).getLocation(), response.getLocation());
   }

   @Test
   public void testGetTask() throws Exception {
      org.jclouds.vcloud.domain.TasksList response = connection.findTasksListInOrgNamed(null);
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
      Org org = connection.findOrgNamed(null);
      for (NamedResource vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getId());
         for (NamedResource item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               VApp app = connection.getVApp(item.getId());
               assertNotNull(app);
            }
         }
      }
   }

   protected abstract void setupCredentials();

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties props = new Properties();
      props.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      props.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      context = new ComputeServiceContextFactory().createContext(provider, identity, credential,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), props).getProviderSpecificContext();

      connection = context.getApi();
   }
}
