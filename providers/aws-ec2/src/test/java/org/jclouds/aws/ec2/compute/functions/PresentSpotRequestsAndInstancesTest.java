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
package org.jclouds.aws.ec2.compute.functions;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.aws.ec2.AWSEC2Client;
import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.aws.ec2.services.AWSInstanceClient;
import org.jclouds.aws.ec2.services.SpotInstanceClient;
import org.jclouds.ec2.compute.domain.RegionAndName;
import org.jclouds.ec2.domain.Reservation;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit")
public class PresentSpotRequestsAndInstancesTest {
   AWSRunningInstance instance1 = createMock(AWSRunningInstance.class);
   AWSRunningInstance instance2 = createMock(AWSRunningInstance.class);

   @SuppressWarnings("unchecked")
   @Test
   public void testWhenInstancesPresentSingleCall() {

      AWSEC2Client client = createMock(AWSEC2Client.class);
      AWSInstanceClient instanceClient = createMock(AWSInstanceClient.class);
      Function<SpotInstanceRequest, AWSRunningInstance> converter = createMock(Function.class);

      expect(client.getInstanceServices()).andReturn(instanceClient);
      
      // avoid imatcher fail.  if you change this, be sure to check multiple jres
      expect(instanceClient.describeInstancesInRegion("us-east-1", "i-aaaa", "i-bbbb")).andReturn(
            Set.class.cast(ImmutableSet.of(Reservation.<AWSRunningInstance> builder().region("us-east-1")
                  .instances(ImmutableSet.of(instance1, instance2)).build())));

      replay(client, instanceClient, converter);

      PresentSpotRequestsAndInstances fn = new PresentSpotRequestsAndInstances(client, converter);

      assertEquals(fn.apply(ImmutableSet.of(new RegionAndName("us-east-1", "i-aaaa"), new RegionAndName("us-east-1",
            "i-bbbb"))), ImmutableSet.of(instance1, instance2));

      verify(client, instanceClient, converter);
   }

   SpotInstanceRequest spot1 = createMock(SpotInstanceRequest.class);
   SpotInstanceRequest spot2 = createMock(SpotInstanceRequest.class);

   @Test
   public void testWhenSpotsPresentSingleCall() {

      Function<SpotInstanceRequest, AWSRunningInstance> converter = Functions.forMap(ImmutableMap.of(spot1, instance1,
            spot2, instance2));

      AWSEC2Client client = createMock(AWSEC2Client.class);
      SpotInstanceClient spotClient = createMock(SpotInstanceClient.class);

      expect(client.getSpotInstanceServices()).andReturn(spotClient);
      expect(spotClient.describeSpotInstanceRequestsInRegion("us-east-1", "sir-aaaa", "sir-bbbb")).andReturn(
            ImmutableSet.of(spot1, spot2));

      replay(client, spotClient);

      PresentSpotRequestsAndInstances fn = new PresentSpotRequestsAndInstances(client, converter);

      assertEquals(fn.apply(ImmutableSet.of(new RegionAndName("us-east-1", "sir-aaaa"), new RegionAndName("us-east-1",
            "sir-bbbb"))), ImmutableSet.of(instance1, instance2));

      verify(client, spotClient);
   }
}
