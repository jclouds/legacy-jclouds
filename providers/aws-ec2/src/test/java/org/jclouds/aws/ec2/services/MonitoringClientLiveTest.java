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
package org.jclouds.aws.ec2.services;

import static org.testng.Assert.assertEquals;

import java.util.Map;

import org.jclouds.aws.ec2.AWSEC2ApiMetadata;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.compute.internal.BaseComputeServiceContextLiveTest;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code MonitoringClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true)
public class MonitoringClientLiveTest extends BaseComputeServiceContextLiveTest {
   public MonitoringClientLiveTest() {
      provider = "aws-ec2";
   }

   private MonitoringClient client;
   private static final String DEFAULT_INSTANCE = "i-TODO";

   
   @Override
   @BeforeClass(groups = { "integration", "live" })
   public void setupContext() {
      super.setupContext();
      client = view.unwrap(AWSEC2ApiMetadata.CONTEXT_TOKEN).getApi().getMonitoringServices();
   }

   @Test(enabled = false)
   // TODO get instance
   public void testMonitorInstances() {
      Map<String, MonitoringState> monitoringState = client.monitorInstancesInRegion(null, DEFAULT_INSTANCE);
      assertEquals(monitoringState.get(DEFAULT_INSTANCE), MonitoringState.PENDING);
   }

   @Test(enabled = false)
   // TODO get instance
   public void testUnmonitorInstances() {
      Map<String, MonitoringState> monitoringState = client.unmonitorInstancesInRegion(null, DEFAULT_INSTANCE);
      assertEquals(monitoringState.get(DEFAULT_INSTANCE), MonitoringState.PENDING);
   }

}
