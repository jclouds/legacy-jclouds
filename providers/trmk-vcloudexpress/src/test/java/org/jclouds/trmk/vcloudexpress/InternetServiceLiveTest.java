/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.trmk.vcloudexpress;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "InternetServiceLiveTest")
public class InternetServiceLiveTest
      extends
      BaseComputeServiceContextLiveTest {
   public InternetServiceLiveTest() {
      provider = "trmk-vcloudexpress";
   }

   TerremarkVCloudExpressClient tmClient;

   private Set<InternetService> services = Sets.newLinkedHashSet();

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testGetAllInternetServices() throws Exception {
      tmClient.getAllInternetServicesInVDC(tmClient.findVDCInOrgNamed(null, null).getHref());
   }

   private void delete(Set<InternetService> set) {
      Set<URI> publicIps = Sets.newHashSet();
      for (InternetService service : set) {
         for (Node node : tmClient.getNodes(service.getId())) {
            tmClient.deleteNode(node.getId());
         }
         tmClient.deleteInternetService(service.getId());
         publicIps.add(service.getPublicIpAddress().getId());
      }
      for (URI id : publicIps) {
         tmClient.deletePublicIp(id);
      }
   }

   @Test
   public void testGetAllPublicIps() throws Exception {
      for (PublicIpAddress ip : tmClient
               .getPublicIpsAssociatedWithVDC(tmClient.findVDCInOrgNamed(null, null).getHref())) {
         tmClient.getInternetServicesOnPublicIp(ip.getId());
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      delete(services);
   }
   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      tmClient = view.unwrap(TerremarkVCloudExpressApiMetadata.CONTEXT_TOKEN).getApi();

   }

   void print(Set<InternetService> set) {
      for (InternetService service : set) {
         System.out.printf("%d (%s:%d%n)", service.getName(), service.getPublicIpAddress().getAddress(), service
                  .getPort());
         for (Node node : tmClient.getNodes(service.getId())) {
            System.out.printf("   %d (%s:%d%n)", node.getName(), node.getIpAddress(), node.getPort());
         }
      }
   }
}
