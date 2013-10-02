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
package org.jclouds.openstack.nova.ec2.compute;

import org.jclouds.compute.domain.ExecResponse;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.ec2.compute.EC2ComputeServiceLiveTest;
import org.testng.annotations.Test;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "live", singleThreaded = true, testName = "NovaEC2ComputeServiceLiveTest")
public class NovaEC2ComputeServiceLiveTest extends EC2ComputeServiceLiveTest {

   public NovaEC2ComputeServiceLiveTest() {
      provider = "openstack-nova-ec2";
   }
   
   protected void checkResponseEqualsHostname(ExecResponse execResponse, NodeMetadata node1) {
      // hostname is not predictable based on node metadata
      assert execResponse.getOutput().trim().equals("ubuntu");
   }
}
