/**
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
package org.jclouds.vcloud.features;

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertEquals;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Catalog;
import org.jclouds.vcloud.domain.CatalogItem;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Status;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.internal.BaseVCloudClientLiveTest;
import org.jclouds.vcloud.options.CatalogItemOptions;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;

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
                  VAppTemplate template = getVCloudApi().getVAppTemplateClient().getVAppTemplate(item.getEntity().getHref());
                  if (template != null) {
                     // the UUID in the href is the only way to actually link templates
                     assertEquals(template.getHref(), item.getEntity().getHref());
                  } else {
                     // null can be no longer available or auth exception
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
               CatalogItem item = getVCloudApi().getCatalogClient().getCatalogItem(resource.getHref());
               if (item.getEntity().getType().equals(VCloudMediaType.VAPPTEMPLATE_XML)) {
                  getVCloudApi().getVAppTemplateClient().getOvfEnvelopeForVAppTemplate(item.getEntity().getHref());
                  // null can be no longer available or auth exception
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
                  VAppTemplate template = getVCloudApi().getVAppTemplateClient().findVAppTemplateInOrgCatalogNamed(
                           org.getName(), response.getName(), item.getEntity().getName());
                  if (template != null) {
                     // the UUID in the href is the only way to actually link templates
                     assertEquals(template.getHref(), item.getEntity().getHref());
                  } else {
                     // null can be no longer available or auth exception
                  }
               }
            }
         }
      }
   }

   @Test
   public void testCaptureVApp() throws Exception {
      String group = prefix + "cap";
      NodeMetadata node = null;
      VAppTemplate vappTemplate = null;
      CatalogItem item = null;
      try {

         node = getOnlyElement(client.createNodesInGroup(group, 1));

         Predicate<URI> taskTester = new RetryablePredicate<URI>(new TaskSuccess(getVCloudApi()), 600, 5,
                  TimeUnit.SECONDS);

         // I have to undeploy first
         Task task = getVCloudApi().getVAppClient().undeployVApp(URI.create(node.getId()));

         // wait up to ten minutes per above
         assert taskTester.apply(task.getHref()) : node;

         VApp vApp = getVCloudApi().getVAppClient().getVApp(URI.create(node.getId()));

         // wait up to ten minutes per above
         assertEquals(vApp.getStatus(), Status.OFF);

         // vdc is equiv to the node's location
         // vapp uri is the same as the node's id
         vappTemplate = getVCloudApi().getVAppTemplateClient().captureVAppAsTemplateInVDC(URI.create(node.getId()),
                  group, URI.create(node.getLocation().getId()));

         assertEquals(vappTemplate.getName(), group);

         task = vappTemplate.getTasks().get(0);

         // wait up to ten minutes per above
         assert taskTester.apply(task.getHref()) : vappTemplate;

         item = getVCloudApi().getCatalogClient().addVAppTemplateOrMediaImageToCatalogAndNameItem(
                  vappTemplate.getHref(),
                  getVCloudApi().getCatalogClient().findCatalogInOrgNamed(null, null).getHref(), "fooname",
                  CatalogItemOptions.Builder.description("description").properties(ImmutableMap.of("foo", "bar")));

         assertEquals(item.getName(), "fooname");
         assertEquals(item.getDescription(), "description");
         assertEquals(item.getProperties(), ImmutableMap.of("foo", "bar"));
         assertEquals(item.getEntity().getName(), "fooname");
         assertEquals(item.getEntity().getHref(), vappTemplate.getHref());
         assertEquals(item.getEntity().getType(), vappTemplate.getType());

      } finally {
         if (item != null)
            getVCloudApi().getCatalogClient().deleteCatalogItem(item.getHref());
         if (vappTemplate != null)
            getVCloudApi().getVAppTemplateClient().deleteVAppTemplate(vappTemplate.getHref());
         if (node != null)
            client.destroyNode(node.getId());
      }
   }
}