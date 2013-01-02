/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.compute;

import static org.jclouds.compute.util.ComputeServiceUtils.getCores;
import static org.testng.Assert.assertEquals;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.domain.ComputeType;
import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.logging.config.LoggingModule;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * Live tests for the Abiquo ComputeService.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "live", testName = "AbiquoComputeServiceLiveTest", singleThreaded = true)
// Since the base class has test configuration, even if we disable tests here,
// or comment them out, the ones in the base class will be executed.
// This class is still a work in progress and will fail until the race condition
// (when creating the virtual appliance) in the AbiquoComputeServiceAdapter is
// fixed and a proper test environment is configured.
public abstract class AbiquoComputeServiceLiveTest extends BaseComputeServiceLiveTest {
   public AbiquoComputeServiceLiveTest() {
      provider = "abiquo";
   }

   @Override
   public void setServiceDefaults() {
      System.setProperty("test.abiquo.template",
            "imageNameMatches=ubuntu_server_ssh_iptables,loginUser=user:abiquo,authenticateSudo=true");
   }

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      overrides.put(Constants.PROPERTY_MAX_RETRIES, "0");
      overrides.put(Constants.PROPERTY_MAX_REDIRECTS, "0");
      overrides.put("jclouds.timeouts.CloudApi.listVirtualMachines", "60000");
      return overrides;
   }

   @Override
   protected void initializeContext() {
      super.initializeContext();
      String templateId = buildTemplate(client.templateBuilder()).getImage().getId();
      view.getUtils().getCredentialStore().put("image#" + templateId, loginCredentials);
   }

   @Override
   protected LoggingModule getLoggingModule() {
      return new SLF4JLoggingModule();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   public void testListSizes() throws Exception {
      for (Hardware hardware : client.listHardwareProfiles()) {
         assert hardware.getProviderId() != null : hardware;
         assert getCores(hardware) > 0 : hardware;
         assert hardware.getVolumes().size() >= 0 : hardware;
         // There are some small images in Abiquo that have less than 1GB of RAM
         // assert hardware.getRam() > 0 : hardware;
         assertEquals(hardware.getType(), ComputeType.HARDWARE);
      }
   }

   @Override
   public void testOptionToNotBlock() throws Exception {
      // By default the provider blocks until the node is running
   }

   // Abiquo does not set the hostname
   @Override
   protected void checkResponseEqualsHostname(final ExecResponse execResponse, final NodeMetadata node) {
      assert node.getHostname() == null : node + " with hostname: " + node.getHostname();
   }

   // Abiquo does not support metadata
   @Override
   protected void checkUserMetadataContains(final NodeMetadata node, final ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(ImmutableMap.<String, String> of()) : String.format(
            "node userMetadata did not match %s %s", userMetadata, node);
   }

   // Abiquo does not support tags
   @Override
   protected void checkTagsInNodeEquals(final NodeMetadata node, final ImmutableSet<String> tags) {
      assert node.getTags().equals(ImmutableSet.<String> of()) : String.format("node tags did not match %s %s", tags,
            node);
   }

}
