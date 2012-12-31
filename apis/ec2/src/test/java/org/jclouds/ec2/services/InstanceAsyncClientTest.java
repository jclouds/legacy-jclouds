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
package org.jclouds.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.ec2.xml.BooleanValueHandler;
import org.jclouds.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.ec2.xml.GetConsoleOutputResponseHandler;
import org.jclouds.ec2.xml.InstanceInitiatedShutdownBehaviorHandler;
import org.jclouds.ec2.xml.InstanceStateChangeHandler;
import org.jclouds.ec2.xml.InstanceTypeHandler;
import org.jclouds.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.ec2.xml.StringValueHandler;
import org.jclouds.ec2.xml.UnencodeStringValueHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

/**
 * Tests behavior of {@code InstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "InstanceAsyncClientTest")
public class InstanceAsyncClientTest extends BaseEC2AsyncClientTest<InstanceAsyncClient> {
   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances", "application/x-www-form-urlencoded",
               false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("terminateInstancesInRegion", String.class, Array
               .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=TerminateInstances&InstanceId.1=1&InstanceId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRunInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
               String.class, int.class, int.class, Array.newInstance(RunInstancesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, null, "ami-voo", 1, 1);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      try {
         assertPayloadEquals(request, "Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=1",
                  "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(request, "Action=RunInstances&ImageId=ami-voo&MaxCount=1&MinCount=1",
                  "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
               String.class, int.class, int.class, Array.newInstance(RunInstancesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, "eu-west-1", "eu-west-1a", "ami-voo",
               1, 5, new RunInstancesOptions().withKernelId("kernelId").withSecurityGroups("group1", "group2"));

      assertRequestLineEquals(request, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.eu-west-1.amazonaws.com\n");
      try {
         assertPayloadEquals(
                  request,
                  "Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=eu-west-1a",
                  "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(
                  request,
                  "Action=RunInstances&ImageId=ami-voo&MaxCount=5&MinCount=1&KernelId=kernelId&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=eu-west-1a",
                  "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testStopInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("stopInstancesInRegion", String.class, boolean.class, Array
               .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, true, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=StopInstances&Force=true&InstanceId.1=1&InstanceId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRebootInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("rebootInstancesInRegion", String.class, Array.newInstance(
               String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=RebootInstances&InstanceId.1=1&InstanceId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testStartInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("startInstancesInRegion", String.class, Array.newInstance(
               String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=StartInstances&InstanceId.1=1&InstanceId.2=2",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getUserDataForInstanceInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=userData&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, UnencodeStringValueHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRootDeviceNameForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getRootDeviceNameForInstanceInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=rootDeviceName&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getRamdiskForInstanceInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=ramdisk&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetDisableApiTerminationForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("isApiTerminationDisabledForInstanceInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=disableApiTermination&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BooleanValueHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getKernelForInstanceInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstanceAttribute&Attribute=kernel&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getInstanceTypeForInstanceInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=instanceType&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceTypeHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getInstanceInitiatedShutdownBehaviorForInstanceInRegion",
               String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=DescribeInstanceAttribute&Attribute=instanceInitiatedShutdownBehavior&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceInitiatedShutdownBehaviorHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("getBlockDeviceMappingForInstanceInRegion", String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=DescribeInstanceAttribute&Attribute=blockDeviceMapping&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setUserDataForInstanceInRegion", String.class, String.class,
               Array.newInstance(byte.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "test".getBytes());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=ModifyInstanceAttribute&Attribute=userData&Value=dGVzdA%3D%3D&InstanceId=1",
               "application/x-www-form-urlencoded", false);
      filter.filter(request);// ensure encoding worked properly
      assertPayloadEquals(
               request,
               "Action=ModifyInstanceAttribute&Attribute=userData&InstanceId=1&Signature=LfUmzLM5DsACR5nQcEfGF5FPdznOwwhJ7tjhBWfHtGs%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Value=dGVzdA%3D%3D&Version=2010-06-15&AWSAccessKeyId=identity",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setRamdiskForInstanceInRegion", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1", "test");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=ModifyInstanceAttribute&Attribute=ramdisk&Value=test&InstanceId=1",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setKernelForInstanceInRegion", String.class, String.class,
               String.class);
      HttpRequest request = processor.createRequest(method, null, "1", "test");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=ModifyInstanceAttribute&Attribute=kernel&Value=test&InstanceId=1",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setApiTerminationDisabledForInstanceInRegion", String.class,
               String.class, boolean.class);
      HttpRequest request = processor.createRequest(method, null, "1", true);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=ModifyInstanceAttribute&Attribute=disableApiTermination&Value=true&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setInstanceTypeForInstanceInRegion", String.class,
               String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1", InstanceType.C1_MEDIUM);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=ModifyInstanceAttribute&Attribute=instanceType&Value=c1.medium&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setInstanceInitiatedShutdownBehaviorForInstanceInRegion",
               String.class, String.class, InstanceInitiatedShutdownBehavior.class);
      HttpRequest request = processor.createRequest(method, null, "1", InstanceInitiatedShutdownBehavior.TERMINATE);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=ModifyInstanceAttribute&Attribute=instanceInitiatedShutdownBehavior&Value=terminate&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setBlockDeviceMappingForInstanceInRegion", String.class,
               String.class, Map.class);

      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("/dev/sda1", new BlockDevice("vol-test1", true));
      HttpRequest request = processor.createRequest(method, null, "1", mapping);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               request,
               "Action=ModifyInstanceAttribute&InstanceId=1&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&BlockDeviceMapping.1.DeviceName=/dev/sda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true",
               "application/x-www-form-urlencoded", false);
      filter.filter(request);// ensure encoding worked properly
      assertPayloadEquals(
               request,
               "Action=ModifyInstanceAttribute&BlockDeviceMapping.1.DeviceName=/dev/sda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&InstanceId=1&Signature=RwY8lVPHSQxQkd5efUKccHdSTkN4OxMIMFiYAe3rrUE%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2010-06-15&AWSAccessKeyId=identity",
               "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetConsoleOutputForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("getConsoleOutputForInstanceInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
               "Action=GetConsoleOutput&InstanceId=1",
               "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, GetConsoleOutputResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
