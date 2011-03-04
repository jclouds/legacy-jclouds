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

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.AuthorizationException;
import org.jclouds.rest.RestContext;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
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
@Test(groups = "live", sequential = true)
public abstract class CommonVCloudClientLiveTest<S extends CommonVCloudClient, A extends CommonVCloudAsyncClient> {

   protected S connection;
   protected RestContext<S, A> context;

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
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getHref());
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getHref());
         assertEquals(connection.findCatalogInOrgNamed(null, response.getName()), response);
      }
   }

   @Test
   public void testGetOrgNetwork() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType resource : org.getNetworks().values()) {
         if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
            OrgNetwork item = connection.getNetwork(resource.getHref());
            assertNotNull(item);
         }
      }
   }

   @Test
   public void testGetVDCNetwork() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getHref());
         for (ReferenceType resource : response.getAvailableNetworks().values()) {
            if (resource.getType().equals(VCloudMediaType.NETWORK_XML)) {
              try{
               OrgNetwork item = connection.getNetwork(resource.getHref());
               assertNotNull(item);
              } catch (AuthorizationException e){
                 
              }
              }
         }
      }
   }

   @Test
   public void testGetCatalogItem() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getHref());
               verifyCatalogItem(item);
            }
         }
      }
   }

   protected void verifyCatalogItem(CatalogItem item) {
      assertNotNull(item);
      assertNotNull(item);
      assertNotNull(item.getEntity());
      assertNotNull(item.getHref());
      assertNotNull(item.getProperties());
      assertNotNull(item.getType());
   }

   @Test
   public void testFindCatalogItem() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
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
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getHref());
         assertNotNull(response);
         assertNotNull(response.getName());
         assertNotNull(response.getHref());
         assertNotNull(response.getResourceEntities());
         assertNotNull(response.getAvailableNetworks());
         assertEquals(connection.getVDC(response.getHref()), response);
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
         assertEquals(connection.getTask(task.getHref()).getHref(), task.getHref());
      }
   }

   protected String provider = "vcloud";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
            + ".identity");
      endpoint = System.getProperty("test." + provider + ".endpoint");
      apiversion = System.getProperty("test." + provider + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      if (endpoint != null)
         overrides.setProperty(provider + ".endpoint", endpoint);
      if (apiversion != null)
         overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();
      context = new ComputeServiceContextFactory().createContext(provider,
            ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getProviderSpecificContext();

      connection = context.getApi();
   }
}
