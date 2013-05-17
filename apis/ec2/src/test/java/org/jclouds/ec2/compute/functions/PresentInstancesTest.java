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
package org.jclouds.ec2.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.ec2.EC2Client;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.services.InstanceClient;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PresentInstancesTest {
   RunningInstance instance1 = createMock(RunningInstance.class);
   RunningInstance instance2 = createMock(RunningInstance.class);

   @SuppressWarnings("unchecked")
   @Test
   public void testWhenInstancesPresentSingleCall() {

      EC2Client client = createMock(EC2Client.class);
      InstanceClient instanceClient = createMock(InstanceClient.class);

      expect(client.getInstanceServices()).andReturn(instanceClient);

      // avoid imatcher fail.  if you change this, be sure to check multiple jres
      expect(instanceClient.describeInstancesInRegion("us-east-1", "i-aaaa", "i-bbbb")).andReturn(
            Set.class.cast(ImmutableSet.of(Reservation.builder().region("us-east-1")
                  .instances(ImmutableSet.of(instance1, instance2)).build())));

      replay(client, instanceClient);

      PresentInstances fn = new PresentInstances(client);

      assertEquals(fn.apply(ImmutableSet.of(new RegionAndName("us-east-1", "i-aaaa"), new RegionAndName("us-east-1",
            "i-bbbb"))), ImmutableSet.of(instance1, instance2));

      verify(client, instanceClient);
   }

}
