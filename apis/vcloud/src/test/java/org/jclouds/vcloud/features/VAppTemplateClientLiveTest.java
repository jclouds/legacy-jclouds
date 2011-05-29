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
package org.jclouds.vcloud.features;

import static org.testng.Assert.assertNotNull;

import org.jclouds.rest.AuthorizationException;
import org.jclouds.vcloud.BaseVCloudClientLiveTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "VAppTemplateClientLiveTest")
public class VAppTemplateClientLiveTest extends BaseVCloudClientLiveTest {

   @Test
   public void testGetVAppTemplate() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = getVCloudApi().getCatalogClient().getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = getVCloudApi().getCatalogClient().getCatalogItem(resource.getHref());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  try {
                     assertNotNull(getVCloudApi().getVAppTemplateClient().getVAppTemplate(item.getEntity().getHref()));
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }

   @Test
   public void testGetOvfEnvelopeForVAppTemplate() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = getVCloudApi().getCatalogClient().getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               try {
                  CatalogItem item = getVCloudApi().getCatalogClient().getCatalogItem(resource.getHref());
                  if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                     assertNotNull(getVCloudApi().getVAppTemplateClient().getOvfEnvelopeForVAppTemplate(
                              item.getEntity().getHref()));
                  }
               } catch (AuthorizationException e) {

               }
            }
         }
      }
   }

   @Test
   public void testFindVAppTemplate() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType cat : org.getCatalogs().values()) {
         Catalog response = getVCloudApi().getCatalogClient().getCatalog(cat.getHref());
         for (ReferenceType resource : response.values()) {
            if (resource.getType().equals(VCloudMediaType.CATALOGITEM_XML)) {
               CatalogItem item = getVCloudApi().getCatalogClient().getCatalogItem(resource.getHref());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  try {
                     assertNotNull(getVCloudApi().getVAppTemplateClient().findVAppTemplateInOrgCatalogNamed(
                              org.getName(), response.getName(), item.getEntity().getName()));
                  } catch (AuthorizationException e) {

                  }
               }
            }
         }
      }
   }

}