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

package org.jclouds.vcloud.terremark;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.google.inject.Module;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class InternetServiceLiveTest {
   TerremarkVCloudExpressClient tmClient;

   private Set<InternetService> services = Sets.newLinkedHashSet();

   private RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient> context;

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testGetAllInternetServices() throws Exception {
      tmClient.getAllInternetServicesInVDC(tmClient.findVDCInOrgNamed(null, null).getId());
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
      for (PublicIpAddress ip : tmClient.getPublicIpsAssociatedWithVDC(tmClient.findVDCInOrgNamed(null, null)
            .getId())) {
         tmClient.getInternetServicesOnPublicIp(ip.getId());
      }
   }

   @AfterTest
   void cleanup() throws InterruptedException, ExecutionException, TimeoutException {
      delete(services);
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      String identity = checkNotNull(System.getProperty("jclouds.test.identity"), "jclouds.test.identity");
      String credential = checkNotNull(System.getProperty("jclouds.test.credential"), "jclouds.test.credential");

      String endpoint = System.getProperty("jclouds.test.endpoint");
      Properties props = new Properties();
      if (endpoint != null && !"".equals(endpoint))
         props.setProperty("terremark.endpoint", endpoint);
      context = new RestContextFactory().createContext("trmk-vcloudexpress", identity, credential, ImmutableSet
            .<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), props);

      tmClient = context.getApi();

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
