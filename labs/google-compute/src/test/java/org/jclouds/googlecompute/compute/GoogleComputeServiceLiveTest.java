/*
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

package org.jclouds.googlecompute.compute;

import com.google.common.collect.ImmutableMap;
import com.google.inject.Module;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.oauth.v2.OAuthTestUtils;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Properties;

import static org.testng.Assert.assertTrue;

/**
 * @author David Alves
 */
@Test(groups = "live", singleThreaded = true)
public class GoogleComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public GoogleComputeServiceLiveTest() {
      provider = "google-compute";
   }

   /**
    * Nodes may have additional metadata entries (particularly they may have an "sshKeys" entry)
    */
   protected void checkUserMetadataInNodeEquals(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assertTrue(node.getUserMetadata().keySet().containsAll(userMetadata.keySet()));
   }

   // do not run until the auth exception problem is figured out.
   @Test(enabled = false)
   @Override
   public void testCorrectAuthException() throws Exception {
   }

   // reboot is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testGet")
   public void testReboot() throws Exception {
   }

   // suspend/Resume is not supported by GCE
   @Test(enabled = true, dependsOnMethods = "testReboot")
   public void testSuspendResume() throws Exception {
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testGetNodesWithDetails() throws Exception {
      super.testGetNodesWithDetails();
   }

   @Test(enabled = true, dependsOnMethods = "testSuspendResume")
   @Override
   public void testListNodes() throws Exception {
      super.testListNodes();
   }

   @Test(enabled = true, dependsOnMethods = {"testListNodes", "testGetNodesWithDetails"})
   @Override
   public void testDestroyNodes() {
      super.testDestroyNodes();
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   @Override
   protected Properties setupProperties() {
      Properties properties = super.setupProperties();
      OAuthTestUtils.setCredentialFromPemFile(properties, "google-compute.credential");
      return properties;
   }
}
