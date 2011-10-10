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
package org.jclouds.aws.ec2.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.LaunchSpecification;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.domain.SpotInstanceRequest;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.ec2.domain.InstanceState;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code SpotInstanceRequestToAWSRunningInstance}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SpotInstanceRequestToAWSRunningInstanceTest")
public class SpotInstanceRequestToAWSRunningInstanceTest {

   public void testConvert() {

      SpotInstanceRequest input = SpotInstanceRequest
            .builder()
            .region("us-east-1")
            .id("sir-228e6406")
            .spotPrice(0.001f)
            .type(SpotInstanceRequest.Type.ONE_TIME)
            .state(SpotInstanceRequest.State.OPEN)
            .launchSpecification(
                  LaunchSpecification.builder().imageId("ami-595a0a1c").securityGroupName("default")
                        .instanceType("m1.large").mapNewVolumeToDevice("/dev/sda1", 1, true)
                        .mapEBSSnapshotToDevice("/dev/sda2", "snap-1ea27576", 1, true)
                        .mapEphemeralDeviceToDevice("/dev/sda3", "vre1").monitoringEnabled(false).build())
            .createTime(new SimpleDateFormatDateService().iso8601DateParse("2011-03-08T03:30:36.000Z"))
            .productDescription("Linux/UNIX")
            .tag("foo", "bar")
            .tag("empty", "")
            .build();

      assertEquals(
            new SpotInstanceRequestToAWSRunningInstance().apply(input),
            AWSRunningInstance.builder().region("us-east-1").instanceId("sir-228e6406")
                  .spotInstanceRequestId("sir-228e6406").instanceState(InstanceState.PENDING).imageId("ami-595a0a1c")
                  .groupId("default").instanceType("m1.large")
                  .tag("foo", "bar")
                  .tag("empty", "")
                  .monitoringState(MonitoringState.PENDING).build());
   }

   public void testConvertWhenNotOpenReturnsNull() {

      assertEquals(
            new SpotInstanceRequestToAWSRunningInstance().apply(SpotInstanceRequest.builder().region("us-east-1")
                  .id("sir-228e6406").type(SpotInstanceRequest.Type.ONE_TIME).state(SpotInstanceRequest.State.ACTIVE)
                  .build()), null);

      assertEquals(
            new SpotInstanceRequestToAWSRunningInstance().apply(SpotInstanceRequest.builder().region("us-east-1")
                  .id("sir-228e6406").type(SpotInstanceRequest.Type.ONE_TIME)
                  .state(SpotInstanceRequest.State.CANCELLED).build()), null);

      assertEquals(
            new SpotInstanceRequestToAWSRunningInstance().apply(SpotInstanceRequest.builder().region("us-east-1")
                  .id("sir-228e6406").type(SpotInstanceRequest.Type.ONE_TIME)
                  .state(SpotInstanceRequest.State.UNRECOGNIZED).build()), null);
   }
}
