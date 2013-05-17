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
package org.jclouds.cloudsigma.compute;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.internal.BaseComputeServiceLiveTest;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "CloudSigmaComputeServiceLiveTest")
public class CloudSigmaComputeServiceLiveTest extends BaseComputeServiceLiveTest {

   public CloudSigmaComputeServiceLiveTest() {
      provider = "cloudsigma";
   }

   @Override
   protected Module getSshModule() {
      return new SshjSshClientModule();
   }

   // cloudsigma does not support metadata
   @Override
   protected void checkUserMetadataContains(NodeMetadata node, ImmutableMap<String, String> userMetadata) {
      assert node.getUserMetadata().equals(ImmutableMap.<String, String> of()) : String.format(
               "node userMetadata did not match %s %s", userMetadata, node);
   }

   // cloudsigma does not support tags
   @Override
   protected void checkTagsInNodeEquals(final NodeMetadata node, final ImmutableSet<String> tags) {
      assert node.getTags().equals(ImmutableSet.<String> of()) : String.format("node tags did not match %s %s", tags,
               node);
   }

   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // hostname is not predictable based on node metadata
      assert execResponse.getOutput().trim().equals("ubuntu");
   }

   @Override
   public void testOptionToNotBlock() {
      // start call has to block until we have a pool of reserved pre-cloned drives.
   }
}
