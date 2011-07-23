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
package org.jclouds.trmk.vcloudexpress;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.rest.RestContext;
import org.jclouds.ssh.jsch.config.JschSshClientModule;
import org.jclouds.trmk.vcloud_0_8.domain.InternetService;
import org.jclouds.trmk.vcloud_0_8.domain.Node;
import org.jclouds.trmk.vcloud_0_8.domain.PublicIpAddress;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressAsyncClient;
import org.jclouds.trmk.vcloudexpress.TerremarkVCloudExpressClient;
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
@Test(groups = "live", sequential = true)
public class InternetServiceLiveTest {
   TerremarkVCloudExpressClient tmClient;

   private Set<InternetService> services = Sets.newLinkedHashSet();

   private RestContext<TerremarkVCloudExpressClient, TerremarkVCloudExpressAsyncClient> context;

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

   protected String provider = "trmk-vcloudexpress";
   protected String identity;
   protected String credential;
   protected String endpoint;
   protected String apiversion;

   protected void setupCredentials() {
      identity = checkNotNull(System.getProperty("test." + provider + ".identity"), "test." + provider + ".identity");
      credential = checkNotNull(System.getProperty("test." + provider + ".credential"), "test." + provider
               + ".credential");
      endpoint = checkNotNull(System.getProperty("test." + provider + ".endpoint"), "test." + provider + ".endpoint");
      apiversion = checkNotNull(System.getProperty("test." + provider + ".apiversion"), "test." + provider
               + ".apiversion");
   }

   protected Properties setupProperties() {
      Properties overrides = new Properties();
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      overrides.setProperty(provider + ".identity", identity);
      overrides.setProperty(provider + ".credential", credential);
      overrides.setProperty(provider + ".endpoint", endpoint);
      overrides.setProperty(provider + ".apiversion", apiversion);
      return overrides;
   }

   @BeforeGroups(groups = { "live" })
   public void setupClient() {
      setupCredentials();
      Properties overrides = setupProperties();

      context = new ComputeServiceContextFactory().createContext(provider,
               ImmutableSet.<Module> of(new Log4JLoggingModule(), new JschSshClientModule()), overrides)
               .getProviderSpecificContext();

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
