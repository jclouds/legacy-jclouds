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
package org.jclouds.aws.ec2.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.util.Set;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.aws.ec2.xml.AWSDescribeInstancesResponseHandler;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code AWSDescribeInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AWSDescribeInstancesResponseHandlerTest")
public class DescribeInstancesResponseTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void test() {
      InputStream is = getClass().getResourceAsStream("/describe_instances_pending.xml");

      Set<Reservation<AWSRunningInstance>> expected = expected();

      AWSDescribeInstancesResponseHandler handler = injector.getInstance(AWSDescribeInstancesResponseHandler.class);
      Set<Reservation<? extends RunningInstance>> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());

   }

   public Set<Reservation<AWSRunningInstance>> expected() {
      return ImmutableSet.of(Reservation.<AWSRunningInstance>builder()
                         .region(defaultRegion)
                         .reservationId("r-3f056a58")
                         .ownerId("095072994936")
//                                             <groupId>sg-f788299f</groupId>
                         .groupName("launchpad_sec_group")
//                                             <groupId>sg-7e512116</groupId>
                         .groupName("jclouds#4c858090-f66c-4225-aa57-6fcaa42198ae")
                         .instance(AWSRunningInstance.builder()
                                  .region(defaultRegion)
                                  .instanceId("i-32451248")
                                  .imageId("ami-bf8131d6")
                                  .rawState("pending")
                                  .instanceState(InstanceState.PENDING)
                                  .privateDnsName("ip-10-194-149-220.ec2.internal")
                                  .dnsName("ec2-23-20-17-42.compute-1.amazonaws.com")
                                  .keyName("jclouds#4c858090-f66c-4225-aa57-6fcaa42198ae#105")
                                  .amiLaunchIndex("0")
                                  .instanceType("c1.medium")
                                  .launchTime(dateService.iso8601DateParse("2012-09-14T20:01:34.000Z"))
                                  .availabilityZone("us-east-1d")
//                                  .tenancy("default")
                                  .kernelId("aki-825ea7eb")
                                  .monitoringState(MonitoringState.DISABLED)
                                  .privateIpAddress("10.194.149.220")
                                  .ipAddress("23.20.17.42")
                                  .securityGroupIdToName("sg-f788299f", "launchpad_sec_group")
                                  .securityGroupIdToName("sg-7e512116", "jclouds#4c858090-f66c-4225-aa57-6fcaa42198ae")
//                                  .architecture("x86_64")
                                  .rootDeviceType(RootDeviceType.EBS)
                                  .rootDeviceName("/dev/sda1")
                                  .device("/dev/sda1", new BlockDevice("vol-b2beb3c9", Attachment.Status.ATTACHING, dateService.iso8601DateParse("2012-09-14T20:01:37.000Z"), true))
                                  .virtualizationType("paravirtual")
                                  .tag("Name", "4c858090-f66c-4225-aa57-6fcaa42198ae-32451248")
                                  .hypervisor(Hypervisor.XEN)
                                  .build()).build());
   }
   
}
