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
import static org.testng.Assert.assertNotNull;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.NamedResource;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.VCloudClientLiveTest")
public class VCloudClientLiveTest extends CommonVCloudClientLiveTest<VCloudClient, VCloudAsyncClient> {

   protected void setupCredentials() {
      provider = "vcloud";
      identity = checkNotNull(System.getProperty("vcloud.identity"), "vcloud.identity");
      credential = checkNotNull(System.getProperty("vcloud.credential"), "vcloud.credential");
   }

   @Test
   public void testGetVAppTemplate() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getId());
         for (NamedResource resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getId());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  try {
                     assertNotNull(connection.getVAppTemplate(item.getEntity().getId()));
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getId());
         for (NamedResource item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VApp app = connection.getVApp(item.getId());
                  assertNotNull(app);
               } catch (RuntimeException e) {

               }
            }
         }
      }
   }

   @Test
   public void testFindVAppTemplate() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (NamedResource cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getId());
         for (NamedResource resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getId());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  try {
                     assertNotNull(connection.findVAppTemplateInOrgCatalogNamed(org.getName(), response.getName(), item
                              .getEntity().getName()));
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }
}
