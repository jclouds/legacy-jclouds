/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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

import static org.testng.Assert.assertNotNull;

import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VCloudExpressVApp;
import org.jclouds.vcloud.domain.VDC;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code VCloudExpressClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true)
public abstract class VCloudExpressClientLiveTest extends
         CommonVCloudClientLiveTest<VCloudExpressClient, VCloudExpressAsyncClient> {

   @Test
   public void testGetVAppTemplate() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getHref());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  assertNotNull(connection.getVAppTemplate(item.getEntity().getHref()));
               }
            }
         }
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      Org org = connection.findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = connection.getVDC(vdc.getHref());
         for (ReferenceType item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VCloudExpressVApp app = connection.getVApp(item.getHref());
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
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = connection.getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = connection.getCatalogItem(resource.getHref());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  assertNotNull(connection.findVAppTemplateInOrgCatalogNamed(org.getName(), response.getName(), item
                           .getEntity().getName()));
               }
            }
         }
      }
   }
}
