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
package org.jclouds.savvis.vpdc.compute;

import java.util.Properties;

import org.jclouds.Constants;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;

/**
 * Takes a long time to list nodes. Average response time is about 10-15 seconds
 * per vm. Hence this test does not complete and is disabled until performance
 * improves.
 * 
 * @author Kedar Dave
 * 
 */
@Test(enabled = true, groups = "live")
public class VPDCComputeServiceLiveTestDisabled extends BaseComputeServiceLiveTest {

   public VPDCComputeServiceLiveTestDisabled() {
      provider = "savvis-symphonyvpdc";
   }

   @Override
   public void setServiceDefaults() {
      group = "savvis-symphonyvpdc";
   }
   
   // savvis does not support metadata
   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(ImmutableMap.<String, String> of()) : String.format(
            "node userMetadata did not match %s %s", userMetadata, node);
   }
   

   @Override
   protected Properties setupProperties() {
      Properties overrides = super.setupProperties();
      // savvis uses untrusted certificates
      overrides.setProperty(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
      overrides.setProperty(Constants.PROPERTY_RELAX_HOSTNAME, "true");
      return overrides;
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

}
