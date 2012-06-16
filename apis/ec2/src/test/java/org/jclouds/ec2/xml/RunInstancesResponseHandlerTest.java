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
package org.jclouds.ec2.xml;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * Tests behavior of {@code RunInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "RunInstancesResponseHandlerTest")
public class RunInstancesResponseHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testEC2() {

      InputStream is = getClass().getResourceAsStream("/run_instances.xml");

      Reservation<? extends RunningInstance> expected = new Reservation<RunningInstance>(defaultRegion, ImmutableSet
               .of("default"), ImmutableSet.of(

      new RunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("0")
               .imageId("ami-60a54009").instanceId("i-2ba64342").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z"))// MonitoringState.ENABLED,
               .availabilityZone("us-east-1b").build(),

      new RunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("1")
               .imageId("ami-60a54009").instanceId("i-2bc64242").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z"))// MonitoringState.ENABLED,
               .availabilityZone("us-east-1b").build(),

      new RunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("2")
               .imageId("ami-60a54009").instanceId("i-2be64332").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("example-key-name").launchTime(
                        dateService.iso8601DateParse("2007-08-07T11:51:50.000Z"))// MonitoringState.ENABLED,
               .availabilityZone("us-east-1b").build())

      , "AIDADH4IGTRXXKCD", null, "r-47a5402e");

      RunInstancesResponseHandler handler = injector.getInstance(RunInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Reservation<? extends RunningInstance> result = factory.create(handler).parse(is);
      assertEquals(result.toString(), expected.toString());
   }

   public void testCloudBridge() {

      InputStream is = getClass().getResourceAsStream("/run_instances_cloudbridge.xml");

      Reservation<? extends RunningInstance> expected = new Reservation<RunningInstance>(defaultRegion, ImmutableSet
               .of("default"), ImmutableSet.of(

      new RunningInstance.Builder().region(defaultRegion).groupId("jclouds#greenqloud-computeblock").amiLaunchIndex("0")
               .imageId("qmi-9ac92558").instanceId("i-01b0dac3").instanceState(InstanceState.PENDING).rawState(
                        "pending").instanceType(InstanceType.M1_SMALL).keyName("jclouds#greenqloud-computeblock#35")
                        .launchTime(dateService.iso8601DateParse("2012-06-15T19:06:35.000+00:00"))
                        .rootDeviceType(RootDeviceType.EBS).availabilityZone("is-1a").build())
      , "56eeacd9-c790-45c3-85f3-e4380b55e1d8<", null, "r-f847a6ca");

      RunInstancesResponseHandler handler = injector.getInstance(RunInstancesResponseHandler.class);
      addDefaultRegionToHandler(handler);
      Reservation<? extends RunningInstance> result = factory.create(handler).parse(is);
      assertEquals(result.toString(), expected.toString());
   }

   private void addDefaultRegionToHandler(ParseSax.HandlerWithResult<?> handler) {
      GeneratedHttpRequest<?> request = createMock(GeneratedHttpRequest.class);
      expect(request.getArgs()).andReturn(ImmutableList.<Object> of()).atLeastOnce();
      replay(request);
      handler.setContext(request);
   }
}
