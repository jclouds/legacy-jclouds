/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
import static org.testng.Assert.assertEquals;

import java.util.SortedSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.vcloud.terremark.domain.InternetService;
import org.jclouds.vcloud.terremark.domain.InternetServiceConfiguration;
import org.jclouds.vcloud.terremark.domain.Node;
import org.jclouds.vcloud.terremark.domain.Protocol;
import org.jclouds.vcloud.terremark.domain.PublicIpAddress;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;
import com.google.inject.Injector;

/**
 * Tests behavior of {@code TerremarkVCloudClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", sequential = true, testName = "vcloud.TerremarkVCloudClientLiveTest")
public class InternetServiceLiveTest {
   TerremarkVCloudClient tmClient;

   private SortedSet<InternetService> services = Sets.newTreeSet();

   public static final String PREFIX = System.getProperty("user.name") + "-terremark";

   @Test
   public void testGetAllInternetServices() throws Exception {
      tmClient.getAllInternetServicesInVDC(tmClient.getDefaultVDC().getId());
   }

   @Test
   public void testAddAndConfigureInternetService() throws InterruptedException {
      InternetService is = tmClient.addInternetServiceToVDC(tmClient.getDefaultVDC().getId(),
               "test-" + 22, Protocol.TCP, 22);
      is = tmClient.configureInternetService(is.getId(), new InternetServiceConfiguration()
               .changeNameTo("test-33"));
      assertEquals(is.getName(), "test-33");
      services.add(is);
      // PublicIpAddress ip = is.getPublicIpAddress();
      // current bug in terremark
      // for (int port : new int[] { 80, 8080 }) {
      // services.add(tmClient.addInternetServiceToExistingIp(ip.getId(), "test-" + port,
      // Protocol.HTTP, port));
      // }
   }

   private void delete(SortedSet<InternetService> set) {
      for (InternetService service : set) {
         for (Node node : tmClient.getNodes(service.getId())) {
            tmClient.deleteNode(node.getId());
         }
         tmClient.deleteInternetService(service.getId());
         tmClient.deletePublicIp(service.getPublicIpAddress().getId());
      }
   }

   @Test
   public void testGetAllPublicIps() throws Exception {
      for (PublicIpAddress ip : tmClient.getPublicIpsAssociatedWithVDC(tmClient.getDefaultVDC()
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
      String account = checkNotNull(System.getProperty("jclouds.test.user"), "jclouds.test.user");
      String key = checkNotNull(System.getProperty("jclouds.test.key"), "jclouds.test.key");
      Injector injector = new TerremarkVCloudContextBuilder(new TerremarkVCloudPropertiesBuilder(
               account, key).relaxSSLHostname().build()).withModules(new Log4JLoggingModule(),
               new JschSshClientModule()).buildInjector();

      tmClient = injector.getInstance(TerremarkVCloudClient.class);

   }

   void print(SortedSet<InternetService> set) {
      for (InternetService service : set) {
         System.out.printf("%d (%s:%d%n)", service.getId(), service.getPublicIpAddress()
                  .getAddress().getHostAddress(), service.getPort());
         for (Node node : tmClient.getNodes(service.getId())) {
            System.out.printf("   %d (%s:%d%n)", node.getId(),
                     node.getIpAddress().getHostAddress(), node.getPort());
         }
      }
   }
}
