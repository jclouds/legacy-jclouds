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

package org.jclouds.vcloud;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.jclouds.Constants;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContextFactory;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.logging.log4j.config.Log4JLoggingModule;
import org.jclouds.predicates.RetryablePredicate;
import org.jclouds.vcloud.domain.Task;
import org.jclouds.vcloud.domain.VAppTemplate;
import org.jclouds.vcloud.predicates.TaskSuccess;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", enabled = true, sequential = true)
public class CaptureVAppLiveTest {


   protected ComputeService client;
   protected String group = System.getProperty("user.name") + "cap";

   protected String provider = "vcloud";
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
      client = new ComputeServiceContextFactory().createContext(provider, 
               ImmutableSet.<Module> of(new Log4JLoggingModule()), overrides).getComputeService();
   }

   @Test
   public void testCaptureVApp() throws Exception {

      NodeMetadata node = null;
      VAppTemplate vappTemplate = null;
      try {

         node = getOnlyElement(client.createNodesInGroup(group, 1));

         VCloudClient vcloudApi = VCloudClient.class.cast(client.getContext().getProviderSpecificContext().getApi());

         Predicate<URI> taskTester = new RetryablePredicate<URI>(new TaskSuccess(vcloudApi), 600, 5, TimeUnit.SECONDS);

         // I have to powerOff first
         Task task = vcloudApi.powerOffVAppOrVm(URI.create(node.getId()));

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
         vappTemplate = vcloudApi.captureVAppInVDC(URI.create(node.getLocation().getId()), URI.create(node.getId()),
                  group);

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