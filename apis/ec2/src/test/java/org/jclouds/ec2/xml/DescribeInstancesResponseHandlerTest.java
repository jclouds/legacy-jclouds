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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableSet;
import static com.google.common.collect.Iterables.*;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code DescribeInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "DescribeInstancesResponseHandlerTest")
public class DescribeInstancesResponseHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testWhenRunning() throws UnknownHostException {

      Set<Reservation<RunningInstance>> contents = ImmutableSet.of(new Reservation<RunningInstance>(defaultRegion,
               ImmutableSet.of("adriancole.ec2ingress"), ImmutableSet.of(new RunningInstance.Builder().region(
                        defaultRegion).groupId("adriancole.ec2ingress").amiLaunchIndex("0").dnsName(
                        "ec2-174-129-81-68.compute-1.amazonaws.com").imageId("ami-82e4b5c7").instanceId("i-0799056f")
                        .instanceState(InstanceState.RUNNING).rawState("running").instanceType(InstanceType.M1_SMALL)
                        .ipAddress("174.129.81.68").kernelId("aki-a71cf9ce").keyName("adriancole.ec21").launchTime(
                                 dateService.iso8601DateParse("2009-11-09T03:00:34.000Z"))
                        // MonitoringState.DISABLED,
                        .availabilityZone("us-east-1c").virtualizationType("paravirtual").privateDnsName(
                                 "ip-10-243-42-70.ec2.internal").privateIpAddress("10.243.42.70").ramdiskId(
                                 "ari-a51cf9cc").rootDeviceType(RootDeviceType.INSTANCE_STORE).build()),
               "993194456877", null, "r-a3c508cb"));

      Set<Reservation<? extends RunningInstance>> result = parseRunningInstances("/describe_instances_running.xml");

      assertEquals(result, contents);
      assertEquals(get(get(result, 0), 0).getInstanceState(), InstanceState.RUNNING);
      assertEquals(get(get(result, 0), 0).getRawState(), "running");

   }

   public void testApplyInputStream() {
      Set<Reservation<RunningInstance>> contents = ImmutableSet.of(new Reservation<RunningInstance>(defaultRegion,
               ImmutableSet.of("default"), ImmutableSet.of(new RunningInstance.Builder().region(defaultRegion).groupId(
                        "default").amiLaunchIndex("23").dnsName("ec2-72-44-33-4.compute-1.amazonaws.com").imageId(
                        "ami-6ea54007").instanceId("i-28a64341").instanceState(InstanceState.RUNNING).rawState(
                        "running").instanceType(InstanceType.M1_LARGE).kernelId("aki-ba3adfd3").keyName(
                        "example-key-name").launchTime(dateService.iso8601DateParse("2007-08-07T11:54:42.000Z"))
               // MonitoringState.DISABLED,
                        .availabilityZone("us-east-1b").virtualizationType("paravirtual").privateDnsName(
                                 "10-251-50-132.ec2.internal")// product codes
                        // ImmutableSet.of("774F4FF8")
                        .ramdiskId("ari-badbad00").rootDeviceType(RootDeviceType.INSTANCE_STORE).build(),
                        new RunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("23")
                                 .dnsName("ec2-72-44-33-6.compute-1.amazonaws.com").imageId("ami-6ea54007").instanceId(
                                          "i-28a64435").instanceState(InstanceState.RUNNING).rawState("running")
                                 .instanceType(InstanceType.M1_LARGE).kernelId("aki-ba3adfd3").keyName(
                                          "example-key-name").launchTime(
                                          dateService.iso8601DateParse("2007-08-07T11:54:42.000Z"))
                                 // MonitoringState.DISABLED,
                                 .availabilityZone("us-east-1b").virtualizationType("paravirtual").privateDnsName(
                                          "10-251-50-134.ec2.internal")// product codes
                                 // ImmutableSet.of("774F4FF8")
                                 .ramdiskId("ari-badbad00").rootDeviceType(RootDeviceType.INSTANCE_STORE).build()),
               "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM", null, "r-44a5402d"));

      Set<Reservation<? extends RunningInstance>> result = parseRunningInstances("/describe_instances.xml");

      assertEquals(result, contents);
      assertEquals(get(get(result, 0), 0).getInstanceState(), InstanceState.RUNNING);
      assertEquals(get(get(result, 0), 0).getRawState(), "running");

   }

   public void testEBS() throws UnknownHostException {

      Set<Reservation<RunningInstance>> contents = ImmutableSet.of(new Reservation<RunningInstance>(defaultRegion,
               ImmutableSet.of("adriancole.ec2ebsingress"), ImmutableSet.of(new RunningInstance.Builder().region(
                        defaultRegion).groupId("adriancole.ec2ebsingress").amiLaunchIndex("0").dnsName(
                        "ec2-75-101-203-146.compute-1.amazonaws.com").imageId("ami-849875ed").instanceId("i-e564438d")
                        .instanceState(InstanceState.RUNNING).rawState("running").instanceType(InstanceType.M1_SMALL)
                        .ipAddress("75.101.203.146").kernelId("aki-a71cf9ce")
                        .keyName("adriancole.ec2ebs1")
                        .launchTime(dateService.iso8601DateParse("2009-12-30T04:06:23.000Z"))
                        // MonitoringState.DISABLED
                        .availabilityZone("us-east-1b")
                        // "placement"
                        .virtualizationType("hvm").privateDnsName("domU-12-31-39-09-CE-53.compute-1.internal")
                        .privateIpAddress("10.210.209.157").ramdiskId("ari-a51cf9cc")
                        .rootDeviceType(RootDeviceType.EBS).rootDeviceName("/dev/sda1").device(
                                 "/dev/sda1",
                                 new BlockDevice("vol-dc6ca8b5", Attachment.Status.ATTACHED, dateService
                                          .iso8601DateParse("2009-12-30T04:06:29.000Z"), true)).build()),
               "993194456877", null, "r-596dd731"));

      Set<Reservation<? extends RunningInstance>> result = parseRunningInstances("/describe_instances_ebs.xml");

      assertEquals(result, contents);
      assertEquals(get(get(result, 0), 0).getInstanceState(), InstanceState.RUNNING);
      assertEquals(get(get(result, 0), 0).getRawState(), "running");
   }

   static ParseSax<Set<Reservation<? extends RunningInstance>>> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>() {
            }).annotatedWith(Region.class).toInstance(Suppliers.ofInstance("us-east-1"));
         }

      });
      ParseSax<Set<Reservation<? extends RunningInstance>>> parser = (ParseSax<Set<Reservation<? extends RunningInstance>>>) injector
               .getInstance(ParseSax.Factory.class)
               .create(injector.getInstance(DescribeInstancesResponseHandler.class));
      return parser;
   }

   public static Set<Reservation<? extends RunningInstance>> parseRunningInstances(String resource) {
      InputStream is = DescribeInstancesResponseHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }
}
