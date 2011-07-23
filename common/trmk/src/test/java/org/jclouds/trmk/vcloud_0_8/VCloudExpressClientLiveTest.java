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
package org.jclouds.trmk.vcloud_0_8;

import static org.testng.Assert.assertNotNull;

import org.jclouds.trmk.vcloud_0_8.VCloudExpressAsyncClient;
import org.jclouds.trmk.vcloud_0_8.VCloudExpressClient;
import org.jclouds.trmk.vcloud_0_8.VCloudMediaType;
import org.jclouds.trmk.vcloud_0_8.domain.Catalog;
import org.jclouds.trmk.vcloud_0_8.domain.CatalogItem;
import org.jclouds.trmk.vcloud_0_8.domain.Org;
import org.jclouds.trmk.vcloud_0_8.domain.ReferenceType;
import org.jclouds.trmk.vcloud_0_8.domain.VCloudExpressVApp;
import org.jclouds.trmk.vcloud_0_8.domain.VDC;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of {@code VCloudExpressClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public abstract class VCloudExpressClientLiveTest extends
      CommonVCloudClientLiveTest<VCloudExpressClient, VCloudExpressAsyncClient> {

   @Override
   protected Iterable<Org> listOrgs() {
      return Iterables.transform(connection.listOrgs().values(), new Function<ReferenceType, Org>(){

         @Override
         public Org apply(ReferenceType arg0) {
            return connection.getOrg(arg0.getHref());
         }
         
      });
   }
   
   @Test
   public void testGetVAppTemplate() throws Exception {
      for (Org org : orgs) {
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
   }

   @Test
   public void testGetVApp() throws Exception {
      for (Org org : listOrgs()) {
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
   }

   @Test
   public void testFindVAppTemplate() throws Exception {
      for (Org org : listOrgs()) {
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
}
