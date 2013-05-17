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

import static com.google.common.collect.Iterables.contains;
import static com.google.common.collect.Iterables.get;
import static org.jclouds.cloudsigma.compute.options.CloudSigmaTemplateOptions.Builder.diskDriveAffinity;
import static org.jclouds.compute.predicates.NodePredicates.inGroup;
import static org.testng.Assert.assertTrue;

import java.util.Set;

import org.jclouds.cloudsigma.CloudSigmaApiMetadata;
import org.jclouds.cloudsigma.CloudSigmaClient;
import org.jclouds.cloudsigma.compute.options.CloudSigmaTemplateOptions;
import org.jclouds.cloudsigma.domain.AffinityType;
import org.jclouds.cloudsigma.domain.Device;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.domain.TemplateBuilder;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "CloudSigmaZurichComputeServiceLiveTest")
public class CloudSigmaZurichComputeServiceLiveTest extends CloudSigmaComputeServiceLiveTest {

   public CloudSigmaZurichComputeServiceLiveTest() {
      provider = "cloudsigma-zrh";
   }

   @Test
   public void testStartNodeWithSSD() throws RunNodesException {
      String group = this.group + "-ssd";

      TemplateBuilder builder = client.templateBuilder();
      assert builder instanceof CloudSigmaTemplateBuilderImpl;

      Template template = builder.options(diskDriveAffinity(AffinityType.SSD)).build();
      assert template.getOptions() instanceof CloudSigmaTemplateOptions;

      try {
         Set<? extends NodeMetadata> nodes = client.createNodesInGroup(group, 1, template);
         NodeMetadata node = get(nodes, 0);

         CloudSigmaClient api = CloudSigmaClient.class.cast(client.getContext().unwrap(
                  CloudSigmaApiMetadata.CONTEXT_TOKEN).getApi());

         // Note: I wanted to use node.getHardware().getVolumes() but there is no
         // way to go from a Volume to a DriveInfo

         ServerInfo serverInfo = api.getServerInfo(node.getId());
         Device rootDevice = get(serverInfo.getDevices().values(), 0);
         DriveInfo driveInfo = api.getDriveInfo(rootDevice.getDriveUuid());
         assertTrue(contains(driveInfo.getTags(), "affinity:ssd"));

      } finally {
         client.destroyNodesMatching(inGroup(group));
      }
   }

}
