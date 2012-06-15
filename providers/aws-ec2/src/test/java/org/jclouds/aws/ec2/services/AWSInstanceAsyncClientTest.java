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
package org.jclouds.aws.ec2.services;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Map;

import org.jclouds.aws.ec2.options.AWSRunInstancesOptions;
import org.jclouds.aws.ec2.xml.AWSDescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.AWSRunInstancesResponseHandler;
import org.jclouds.ec2.domain.BlockDevice;
import org.jclouds.ec2.domain.InstanceType;
import org.jclouds.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.ec2.options.RunInstancesOptions;
import org.jclouds.ec2.xml.BlockDeviceMappingHandler;
import org.jclouds.ec2.xml.BooleanValueHandler;
import org.jclouds.ec2.xml.InstanceInitiatedShutdownBehaviorHandler;
import org.jclouds.ec2.xml.InstanceStateChangeHandler;
import org.jclouds.ec2.xml.InstanceTypeHandler;
import org.jclouds.ec2.xml.StringValueHandler;
import org.jclouds.ec2.xml.UnencodeStringValueHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AWSInstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AWSInstanceAsyncClientTest")
public class AWSInstanceAsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSInstanceAsyncClient> {
   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances", "application/x-www-form-urlencoded",
            false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSDescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSDescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("terminateInstancesInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=TerminateInstances&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testRunInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
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
      assertSaxResponseParserClassEquals(method, AWSRunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
            String.class, int.class, int.class, Array.newInstance(RunInstancesOptions.class, 0).getClass());
      HttpRequest request = processor.createRequest(
            method,
            "us-east-1",
            "us-east-1a",
            "ami-voo",
            1,
            5,
            new AWSRunInstancesOptions().withKernelId("kernelId").enableMonitoring()
                  .withSecurityGroups("group1", "group2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      try {
         assertPayloadEquals(
               request,
               "Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&Monitoring.Enabled=true&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=us-east-1a",
               "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(
               request,
               "Action=RunInstances&ImageId=ami-voo&MaxCount=5&MinCount=1&KernelId=kernelId&Monitoring.Enabled=true&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=us-east-1a",
               "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSRunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testStopInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("stopInstancesInRegion", String.class, boolean.class,
            Array.newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, true, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=StopInstances&Force=true&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRebootInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("rebootInstancesInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=RebootInstances&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testStartInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("startInstancesInRegion", String.class,
            Array.newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=StartInstances&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceStateChangeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getUserDataForInstanceInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=userData&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, UnencodeStringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRootDeviceNameForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getRootDeviceNameForInstanceInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=rootDeviceName&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getRamdiskForInstanceInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=ramdisk&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetDisableApiTerminationForInstanceInRegion() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("isApiTerminationDisabledForInstanceInRegion",
            String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=disableApiTermination&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BooleanValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class
            .getMethod("getKernelForInstanceInRegion", String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstanceAttribute&Attribute=kernel&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, StringValueHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getInstanceTypeForInstanceInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=instanceType&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, InstanceTypeHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getInstanceInitiatedShutdownBehaviorForInstanceInRegion",
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
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testGetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("getBlockDeviceMappingForInstanceInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "1");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=DescribeInstanceAttribute&Attribute=blockDeviceMapping&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, BlockDeviceMappingHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setUserDataForInstanceInRegion", String.class,
            String.class, Array.newInstance(byte.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "test".getBytes());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=ModifyInstanceAttribute&Attribute=userData&Value=dGVzdA%3D%3D&InstanceId=1",
            "application/x-www-form-urlencoded", false);
      filter.filter(request);// ensure encoding worked properly
      assertPayloadEquals(
            request,
            "Action=ModifyInstanceAttribute&Attribute=userData&InstanceId=1&Signature=nArJlrtyAfgqDnXxv%2B2y0r9O%2BIfUcitpQMjfQR0oeRM%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Value=dGVzdA%3D%3D&Version=2011-05-15&AWSAccessKeyId=identity",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setRamdiskForInstanceInRegion", String.class,
            String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1", "test");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=ModifyInstanceAttribute&Attribute=ramdisk&Value=test&InstanceId=1",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setKernelForInstanceInRegion", String.class,
            String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1", "test");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=ModifyInstanceAttribute&Attribute=kernel&Value=test&InstanceId=1",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setApiTerminationDisabledForInstanceInRegion",
            String.class, String.class, boolean.class);
      HttpRequest request = processor.createRequest(method, null, "1", true);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=ModifyInstanceAttribute&Attribute=disableApiTermination&Value=true&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setInstanceTypeForInstanceInRegion", String.class,
            String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "1", InstanceType.C1_MEDIUM);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=ModifyInstanceAttribute&Attribute=instanceType&Value=c1.medium&InstanceId=1",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
         NoSuchMethodException, IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setInstanceInitiatedShutdownBehaviorForInstanceInRegion",
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
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
         IOException {
      Method method = AWSInstanceAsyncClient.class.getMethod("setBlockDeviceMappingForInstanceInRegion", String.class,
            String.class, Map.class);

      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("/dev/sda1", new BlockDevice("vol-test1", true));
      HttpRequest request = processor.createRequest(method, null, "1", mapping);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=ModifyInstanceAttribute&InstanceId=1&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true",
            "application/x-www-form-urlencoded", false);
      filter.filter(request);// ensure encoding worked properly
      assertPayloadEquals(
            request,
            "Action=ModifyInstanceAttribute&BlockDeviceMapping.1.DeviceName=%2Fdev%2Fsda1&BlockDeviceMapping.1.Ebs.DeleteOnTermination=true&BlockDeviceMapping.1.Ebs.VolumeId=vol-test1&InstanceId=1&Signature=qqJpPk8UmhPY9Jica0JSADEZiY3eHf9WETm%2B5tLT0NE%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2011-05-15&AWSAccessKeyId=identity",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AWSInstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AWSInstanceAsyncClient>>() {
      };
   }

}
