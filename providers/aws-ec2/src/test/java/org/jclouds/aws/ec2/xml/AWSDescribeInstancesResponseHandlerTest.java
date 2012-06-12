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
package org.jclouds.aws.ec2.xml;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.Set;

import org.jclouds.aws.ec2.domain.AWSRunningInstance;
import org.jclouds.aws.ec2.domain.MonitoringState;
import org.jclouds.date.DateService;
import org.jclouds.ec2.domain.Attachment;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.Hypervisor;
import org.jclouds.ec2.domain.InstanceState;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Reservation;
import org.jclouds.ec2.domain.RootDeviceType;
import org.jclouds.ec2.domain.RunningInstance;
import org.jclouds.ec2.xml.BaseEC2HandlerTest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.config.SaxParserModule;
import org.jclouds.location.Region;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AWSDescribeInstancesResponseHandler}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AWSDescribeInstancesResponseHandlerTest")
public class AWSDescribeInstancesResponseHandlerTest extends BaseEC2HandlerTest {

   private DateService dateService;

   @BeforeTest
   @Override
   protected void setUpInjector() {
      super.setUpInjector();
      dateService = injector.getInstance(DateService.class);
      assert dateService != null;
   }

   public void testWhenRunning() throws UnknownHostException {

      Set<Reservation<AWSRunningInstance>> contents = ImmutableSet.of(
              new Reservation<AWSRunningInstance>(defaultRegion, ImmutableSet.of("adriancole.ec2ingress"), ImmutableSet.of(
                      new AWSRunningInstance.Builder()
                          .region(defaultRegion)
                          .groupId("adriancole.ec2ingress")
                          .amiLaunchIndex("0")
                          .dnsName("ec2-174-129-81-68.compute-1.amazonaws.com")
                          .imageId("ami-82e4b5c7")
                          .instanceId("i-0799056f")
                          .instanceState(InstanceState.RUNNING)
                          .rawState("running")
                          .instanceType(InstanceType.M1_SMALL)
                          .ipAddress("174.129.81.68")
                          .kernelId("aki-a71cf9ce")
                          .keyName("adriancole.ec21")
                          .launchTime(dateService.iso8601DateParse("2009-11-09T03:00:34.000Z"))
                          .monitoringState(MonitoringState.DISABLED)
                          .availabilityZone("us-east-1c")
                          .virtualizationType("paravirtual")
                          .privateDnsName("ip-10-243-42-70.ec2.internal")
                          .privateIpAddress("10.243.42.70")
                          .ramdiskId("ari-a51cf9cc")
                          .rootDeviceType(RootDeviceType.INSTANCE_STORE)
                          .hypervisor(Hypervisor.XEN)
                          .build()),
                  "993194456877", null, "r-a3c508cb"));

      Set<Reservation<? extends RunningInstance>> result = parseAWSRunningInstances("/describe_instances_running.xml");

      assertEquals(result.toString(), contents.toString());
   }

   public void testWhenRunningLatest() throws UnknownHostException {
      Set<Reservation<AWSRunningInstance>> contents = ImmutableSet.of(new Reservation<AWSRunningInstance>(
            defaultRegion, ImmutableSet.of("jclouds#ec2-s#us-east-1"), ImmutableSet.of(
                  new AWSRunningInstance.Builder()
                        .region(defaultRegion)
                        .instanceId("i-911444f0")
                        .imageId("ami-63be790a")
                        .instanceState(InstanceState.RUNNING)
                        .rawState("running")
                        .privateDnsName("ip-10-212-81-7.ec2.internal")
                        .dnsName("ec2-174-129-173-155.compute-1.amazonaws.com")
                        .keyName("jclouds#zkclustertest#us-east-1#23")
                        .amiLaunchIndex("0")
                        .instanceType("t1.micro")
                        .launchTime(dateService.iso8601DateParse("2011-08-16T13:40:50.000Z"))
                        .availabilityZone("us-east-1c")
                        .kernelId("aki-427d952b")
                        .monitoringState(MonitoringState.DISABLED)
                        .privateIpAddress("10.212.81.7")
                        .ipAddress("174.129.173.155")
                        .securityGroupIdToName("sg-ef052b86", "jclouds#zkclustertest#us-east-1")
                        .tag("Name", "ec2-o")
                        .tag("Empty", "")
                        .rootDeviceType(RootDeviceType.EBS)
                        .rootDeviceName("/dev/sda1")
                        .device(
                              "/dev/sda1",
                              new BlockDevice("vol-5829fc32", Attachment.Status.ATTACHED, dateService
                                    .iso8601DateParse("2011-08-16T13:41:19.000Z"), true))
                        .hypervisor(Hypervisor.XEN)
                        .virtualizationType("paravirtual").build(),//
                  new AWSRunningInstance.Builder()
                        .region(defaultRegion)
                        .instanceId("i-931444f2")
                        .imageId("ami-63be790a")
                        .instanceState(InstanceState.RUNNING)
                        .rawState("running")
                        .privateDnsName("ip-10-212-185-8.ec2.internal")
                        .dnsName("ec2-50-19-207-248.compute-1.amazonaws.com")
                        .keyName("jclouds#zkclustertest#us-east-1#23")
                        .amiLaunchIndex("0")
                        .instanceType("t1.micro")
                        .launchTime(dateService.iso8601DateParse("2011-08-16T13:40:50.000Z"))
                        .availabilityZone("us-east-1c")
                        .kernelId("aki-427d952b")
                        .monitoringState(MonitoringState.DISABLED)
                        .privateIpAddress("10.212.185.8")
                        .ipAddress("50.19.207.248")
                        .securityGroupIdToNames(
                              ImmutableMap.<String, String> of("sg-ef052b86", "jclouds#zkclustertest#us-east-1"))
                        .rootDeviceType(RootDeviceType.EBS)
                        .rootDeviceName("/dev/sda1")
                        .device(
                              "/dev/sda1",
                              new BlockDevice("vol-5029fc3a", Attachment.Status.ATTACHED, dateService
                                    .iso8601DateParse("2011-08-16T13:41:19.000Z"), true))
                        .hypervisor(Hypervisor.XEN)
                        .virtualizationType("paravirtual").build()), defaultRegion, defaultRegion, defaultRegion));

      Set<Reservation<? extends RunningInstance>> result = parseAWSRunningInstances("/describe_instances_latest.xml");

      assertEquals(result.toString(), contents.toString());
   }

   public void testParseNoNPE() {
      parseAWSRunningInstances("/describe_instances_1.xml");
      parseAWSRunningInstances("/describe_instances_2.xml");
      parseAWSRunningInstances("/describe_instances_3.xml");
   }

   public void testApplyInputStream() {
      Set<Reservation<AWSRunningInstance>> contents = ImmutableSet.of(new Reservation<AWSRunningInstance>(
            defaultRegion, ImmutableSet.of("default"), ImmutableSet.of(
                  new AWSRunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("23")
                        .dnsName("ec2-72-44-33-4.compute-1.amazonaws.com").imageId("ami-6ea54007")
                        .instanceId("i-28a64341").instanceState(InstanceState.RUNNING).rawState("running")
                        .instanceType(InstanceType.M1_LARGE).kernelId("aki-ba3adfd3").keyName("example-key-name")
                        .launchTime(dateService.iso8601DateParse("2007-08-07T11:54:42.000Z"))
                        .monitoringState(MonitoringState.DISABLED).availabilityZone("us-east-1b")
                        .virtualizationType("paravirtual").privateDnsName("10-251-50-132.ec2.internal")
                        .productCode("774F4FF8").ramdiskId("ari-badbad00")
                        .hypervisor(Hypervisor.XEN)
                        .rootDeviceType(RootDeviceType.INSTANCE_STORE).build(),
                  new AWSRunningInstance.Builder().region(defaultRegion).groupId("default").amiLaunchIndex("23")
                        .dnsName("ec2-72-44-33-6.compute-1.amazonaws.com").imageId("ami-6ea54007")
                        .instanceId("i-28a64435").instanceState(InstanceState.RUNNING).rawState("running")
                        .instanceType(InstanceType.M1_LARGE).kernelId("aki-ba3adfd3").keyName("example-key-name")
                        .launchTime(dateService.iso8601DateParse("2007-08-07T11:54:42.000Z"))
                        .monitoringState(MonitoringState.DISABLED).availabilityZone("us-east-1b")
                        .virtualizationType("paravirtual").privateDnsName("10-251-50-134.ec2.internal")
                        .productCode("774F4FF8").ramdiskId("ari-badbad00")
                        .hypervisor(Hypervisor.XEN)
                        .rootDeviceType(RootDeviceType.INSTANCE_STORE).build()), "UYY3TLBUXIEON5NQVUUX6OMPWBZIQNFM",
            null, "r-44a5402d"));

      Set<Reservation<? extends RunningInstance>> result = parseAWSRunningInstances("/describe_instances.xml");

      assertEquals(result.toString(), contents.toString());
   }

   public void testEBS() throws UnknownHostException {

      Set<Reservation<AWSRunningInstance>> contents = ImmutableSet.of(new Reservation<AWSRunningInstance>(
            defaultRegion, ImmutableSet.of("adriancole.ec2ebsingress"), ImmutableSet
                  .of(new AWSRunningInstance.Builder()
                        .region(defaultRegion)
                        .groupId("adriancole.ec2ebsingress")
                        .amiLaunchIndex("0")
                        .dnsName("ec2-75-101-203-146.compute-1.amazonaws.com")
                        .imageId("ami-849875ed")
                        .instanceId("i-e564438d")
                        .instanceState(InstanceState.RUNNING)
                        .rawState("running")
                        .instanceType(InstanceType.M1_SMALL)
                        .ipAddress("75.101.203.146")
                        .kernelId("aki-a71cf9ce")
                        .keyName("adriancole.ec2ebs1")
                        .launchTime(dateService.iso8601DateParse("2009-12-30T04:06:23.000Z"))
                        .monitoringState(MonitoringState.DISABLED)
                        .availabilityZone("us-east-1b")
                        .placementGroup("placement")
                        .virtualizationType("hvm")
                        .privateDnsName("domU-12-31-39-09-CE-53.compute-1.internal")
                        .privateIpAddress("10.210.209.157")
                        .ramdiskId("ari-a51cf9cc")
                        .hypervisor(Hypervisor.XEN)
                        .rootDeviceType(RootDeviceType.EBS)
                        .rootDeviceName("/dev/sda1")
                        .hypervisor(Hypervisor.XEN)
                        .device(
                              "/dev/sda1",
                              new BlockDevice("vol-dc6ca8b5", Attachment.Status.ATTACHED, dateService
                                    .iso8601DateParse("2009-12-30T04:06:29.000Z"), true)).build()), "993194456877",
            null, "r-596dd731"));

      Set<Reservation<? extends RunningInstance>> result = parseAWSRunningInstances("/describe_instances_ebs.xml");

      assertEquals(result.toString(), contents.toString());
   }

   static ParseSax<Set<Reservation<? extends RunningInstance>>> createParser() {
      Injector injector = Guice.createInjector(new SaxParserModule(), new AbstractModule() {

         @Override
         protected void configure() {
            bind(new TypeLiteral<Supplier<String>>(){}).annotatedWith(Region.class).toInstance(Suppliers.ofInstance("us-east-1"));
            bind(RunningInstance.Builder.class).to(AWSRunningInstance.Builder.class);
         }

      });
      ParseSax<Set<Reservation<? extends RunningInstance>>> parser = (ParseSax<Set<Reservation<? extends RunningInstance>>>) injector
            .getInstance(ParseSax.Factory.class)
            .create(injector.getInstance(AWSDescribeInstancesResponseHandler.class));
      return parser;
   }

   public static Set<Reservation<? extends RunningInstance>> parseAWSRunningInstances(String resource) {
      InputStream is = AWSDescribeInstancesResponseHandlerTest.class.getResourceAsStream(resource);
      return createParser().parse(is);
   }
}
