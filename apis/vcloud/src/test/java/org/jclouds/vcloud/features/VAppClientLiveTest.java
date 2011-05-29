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

import static com.google.common.collect.Iterables.getOnlyElement;
import static org.testng.Assert.assertNotNull;

import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.BaseVCloudClientLiveTest;
import org.jclouds.vcloud.VCloudMediaType;
import org.jclouds.vcloud.domain.Org;
import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VApp;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.domain.VDC;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, singleThreaded = true, testName = "VAppClientLiveTest")
public class VAppClientLiveTest extends BaseVCloudClientLiveTest {

   @Test
   public void testGetVApp() throws Exception {
      Org org = getVCloudApi().getOrgClient().findOrgNamed(null);
      for (ReferenceType vdc : org.getVDCs().values()) {
         VDC response = getVCloudApi().getVDCClient().getVDC(vdc.getHref());
         for (ReferenceType item : response.getResourceEntities().values()) {
            if (item.getType().equals(VCloudMediaType.VAPP_XML)) {
               try {
                  VApp app = getVCloudApi().getVAppClient().getVApp(item.getHref());
                  assertNotNull(app);
               } catch (RuntimeException e) {

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
      try {

         node = getOnlyElement(client.createNodesInGroup(group, 1));

         Predicate<URI> taskTester = new RetryablePredicate<URI>(new TaskSuccess(getVCloudApi()), 600, 5,
                  TimeUnit.SECONDS);

         // I have to powerOff first
         Task task = getVCloudApi().getVAppClient().powerOffVApp(URI.create(node.getId()));

         // wait up to ten minutes per above
         assert taskTester.apply(task.getHref()) : node;

         // having a problem where the api is returning an error telling us to stop!

         // // I have to undeploy first
         // task = vcloudApi.undeployVAppOrVm(URI.create(node.getId()));
         //
         // // wait up to ten minutes per above
         // assert taskTester.apply(task.getHref()) : node;

         // vdc is equiv to the node's location
         // vapp uri is the same as the node's id
         vappTemplate = getVCloudApi().getVAppTemplateClient().captureVAppAsTemplateInVDC(URI.create(node.getId()),
                  group, URI.create(node.getLocation().getId()));

         task = vappTemplate.getTasks().get(0);

         // wait up to ten minutes per above
         assert taskTester.apply(task.getHref()) : vappTemplate;

         // TODO implement delete vAppTemplate
      } finally {
         if (node != null)
            client.destroyNode(node.getId());
      }
   }

}