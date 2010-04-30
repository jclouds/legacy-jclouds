/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import org.jclouds.aws.domain.Region;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.BlockDeviceMapping;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.RunningInstance;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.aws.ec2.xml.BooleanValueHandler;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.InstanceInitiatedShutdownBehaviorHandler;
import org.jclouds.aws.ec2.xml.InstanceStateChangeHandler;
import org.jclouds.aws.ec2.xml.InstanceTypeHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.StringValueHandler;
import org.jclouds.aws.ec2.xml.UnencodeStringValueHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code InstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.InstanceAsyncClientTest")
public class InstanceAsyncClientTest extends BaseEC2AsyncClientTest<InstanceAsyncClient> {
   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               (String) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeInstances");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("terminateInstancesInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 44\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=TerminateInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               null, "ami-voo", 1, 1);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 76\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.EU_WEST_1, AvailabilityZone.EU_WEST_1A, "ami-voo", 1, 5,
               new RunInstancesOptions().withKernelId("kernelId").enableMonitoring()
                        .withSecurityGroups("group1", "group2"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 164\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&Monitoring.Enabled=true&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=eu-west-1a");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testStopInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("stopInstancesInRegion", String.class,
               boolean.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               true, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 50\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=StopInstances&Force=true&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRebootInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("rebootInstancesInRegion", String.class,
               Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=RebootInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testStartInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("startInstancesInRegion", String.class,
               Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 40\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=StartInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetUserDataForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getUserDataForInstanceInRegion",
               String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 83\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=userData&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, UnencodeStringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetRootDeviceNameForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getRootDeviceNameForInstanceInRegion",
               String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 89\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=rootDeviceName&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("getRamdiskForInstanceInRegion",
               String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 82\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=ramdisk&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetDisableApiTerminationForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "isApiTerminationDisabledForInstanceInRegion", String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 96\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=disableApiTermination&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BooleanValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("getKernelForInstanceInRegion",
               String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 81\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=kernel&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetInstanceTypeForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getInstanceTypeForInstanceInRegion",
               String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 87\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=instanceType&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceTypeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetInstanceInitiatedShutdownBehaviorForInstanceInRegion()
            throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "getInstanceInitiatedShutdownBehaviorForInstanceInRegion", String.class,
               String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 108\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=instanceInitiatedShutdownBehavior&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceInitiatedShutdownBehaviorHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testGetBlockDeviceMappingForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "getBlockDeviceMappingForInstanceInRegion", String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 93\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeInstanceAttribute&Attribute=blockDeviceMapping&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetUserDataForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setUserDataForInstanceInRegion",
               String.class, String.class, Array.newInstance(byte.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "test".getBytes());

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 100\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=userData&Value=dGVzdA%3D%3D&InstanceId=1");
      filter.filter(httpMethod);// ensure encoding worked properly
      assertPayloadEquals(
               httpMethod,
               "Action=ModifyInstanceAttribute&Attribute=userData&InstanceId=1&Signature=LyanxPcmESLrkIIFu9RX2yGN1rSQmyF489LYcoszbFE%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Value=dGVzdA%3D%3D&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setRamdiskForInstanceInRegion",
               String.class, String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "test");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 91\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=ramdisk&Value=test&InstanceId=1");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setKernelForInstanceInRegion",
               String.class, String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", "test");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 90\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=kernel&Value=test&InstanceId=1");
      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "setApiTerminationDisabledForInstanceInRegion", String.class, String.class,
               boolean.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", true);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 105\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=disableApiTermination&Value=true&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setInstanceTypeForInstanceInRegion",
               String.class, String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", InstanceType.C1_MEDIUM);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 101\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=instanceType&Value=c1.medium&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion()
            throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "setInstanceInitiatedShutdownBehaviorForInstanceInRegion", String.class,
               String.class, InstanceInitiatedShutdownBehavior.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", InstanceInitiatedShutdownBehavior.TERMINATE);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 122\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=instanceInitiatedShutdownBehavior&Value=terminate&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "setBlockDeviceMappingForInstanceInRegion", String.class, String.class,
               BlockDeviceMapping.class);

      BlockDeviceMapping blockDeviceMapping = new BlockDeviceMapping();
      blockDeviceMapping.addEbsBlockDevice("/dev/sda1", new RunningInstance.EbsBlockDevice(
               "vol-test1", true));
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method, null,
               "1", blockDeviceMapping);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 62\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&InstanceId=1&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true");
      filter.filter(httpMethod);// ensure encoding worked properly
      assertPayloadEquals(
               httpMethod,
               "Action=ModifyInstanceAttribute&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&InstanceId=1&Signature=ME8%2FNeH5Zs3%2FY4otsKB0Q09mipVwoEkroUEChQ%2FMZao%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>>() {
      };
   }
}
