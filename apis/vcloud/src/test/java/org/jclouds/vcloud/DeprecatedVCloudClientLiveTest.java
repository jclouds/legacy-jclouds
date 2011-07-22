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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.domain.Vm;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Tests behavior of deprecated {@code VCloudClient} features
 * 
 * @author Adrian Cole
 */
@Deprecated
@Test(groups = "live", singleThreaded = true)
public class DeprecatedVCloudClientLiveTest extends CommonVCloudClientLiveTest<VCloudClient, VCloudAsyncClient> {

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
   public void testListOrgs() throws Exception {
      for (Org org : orgs) {
         assertNotNull(org);
         assertNotNull(org.getName());
         assertNotNull(org.getHref());
         assertEquals(connection.getOrg(org.getHref()).getName(), org.getName());
         assertEquals(connection.findOrgNamed(org.getName()).getName(), org.getName());
      }
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
                     try {
                        assertNotNull(connection.getVAppTemplate(item.getEntity().getHref()));
                     } catch (AuthorizationException e) {

                     }
                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetOvfEnvelopeForVAppTemplate() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
                  try {
                     CatalogItem item = connection.getCatalogItem(resource.getHref());
                     if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                        assertNotNull(connection.getOvfEnvelopeForVAppTemplate(item.getEntity().getHref()));
                     }
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetVApp() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType item : response.getResourceEntities().values()) {
               if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
                  try {
                     VApp app = connection.getVApp(item.getHref());
                     assertNotNull(app);
                  } catch (RuntimeException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetThumbnailOfVm() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType item : response.getResourceEntities().values()) {
               if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
                  try {
                     VApp app = connection.getVApp(item.getHref());
                     assertNotNull(app);
                     for (Vm vm : app.getChildren()) {
                        assert connection.getThumbnailOfVm(vm.getHref()) != null;
                     }
                  } catch (RuntimeException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetVm() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType vdc : org.getVDCs().values()) {
            VDC response = connection.getVDC(vdc.getHref());
            for (ReferenceType item : response.getResourceEntities().values()) {
               if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
                  try {
                     VApp app = connection.getVApp(item.getHref());
                     assertNotNull(app);
                     assert app.getChildren().size() > 0;
                  } catch (RuntimeException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testFindVAppTemplate() throws Exception {
      for (Org org : orgs) {
         for (ReferenceType cat : org.getCatalogs().values()) {
            Catalog response = connection.getCatalog(cat.getHref());
            for (ReferenceType resource : response.values()) {
               if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
                  CatalogItem item = connection.getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                     try {
                        assertNotNull(connection.findVAppTemplateInOrgCatalogNamed(org.getName(), response.getName(),
                              item.getEntity().getName()));
                     } catch (AuthorizationException e) {

                     }
                  }
               }
            }
         }
      }
   }

}
