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

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;
import java.util.Map;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
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
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.Invokable;
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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "describeInstancesInRegion", String.class, String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances", "application/x-www-form-urlencoded",
            false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSDescribeInstancesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "describeInstancesInRegion", String.class, String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSDescribeInstancesResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "terminateInstancesInRegion", String.class, String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "runInstancesInRegion", String.class, String.class,
            String.class, int.class, int.class, RunInstancesOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, null, "ami-voo", 1, 1));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=1",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSRunInstancesResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "runInstancesInRegion", String.class, String.class,
            String.class, int.class, int.class, RunInstancesOptions[].class);
      GeneratedHttpRequest request = processor.createRequest(
            method,
            Lists.<Object> newArrayList("us-east-1",
            "us-east-1a",
            "ami-voo",
            1,
            5,
            new AWSRunInstancesOptions().withKernelId("kernelId").enableMonitoring()
                  .withSecurityGroups("group1", "group2")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&Monitoring.Enabled=true&SecurityGroup.1=group1&SecurityGroup.2=group2&Placement.AvailabilityZone=us-east-1a",
            "application/x-www-form-urlencoded", false);
      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AWSRunInstancesResponseHandler.class);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testStopInstances() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "stopInstancesInRegion", String.class, boolean.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, true, "1", "2"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "rebootInstancesInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "startInstancesInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getUserDataForInstanceInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getRootDeviceNameForInstanceInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getRamdiskForInstanceInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "isApiTerminationDisabledForInstanceInRegion",
            String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getKernelForInstanceInRegion", String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getInstanceTypeForInstanceInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getInstanceInitiatedShutdownBehaviorForInstanceInRegion",
            String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "getBlockDeviceMappingForInstanceInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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

   HttpRequest setUserDataForInstance = HttpRequest.builder().method("POST")
                                                   .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                   .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                   .addFormParam("Action", "ModifyInstanceAttribute")
                                                   .addFormParam("Attribute", "userData")
                                                   .addFormParam("InstanceId", "1")
                                                   .addFormParam("Value", "dGVzdA%3D%3D").build();

   public void testSetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setUserDataForInstanceInRegion", String.class, String.class,
               byte[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test".getBytes()));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setUserDataForInstance).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest setRamdiskForInstance = HttpRequest.builder().method("POST")
                                                  .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                  .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                  .addFormParam("Action", "ModifyInstanceAttribute")
                                                  .addFormParam("Attribute", "ramdisk")
                                                  .addFormParam("InstanceId", "1")
                                                  .addFormParam("Value", "test").build();

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setRamdiskForInstanceInRegion", String.class, String.class,
               String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setRamdiskForInstance).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest setKernelForInstance = HttpRequest.builder().method("POST")
                                                 .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                 .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                 .addFormParam("Action", "ModifyInstanceAttribute")
                                                 .addFormParam("Attribute", "kernel")
                                                 .addFormParam("InstanceId", "1")
                                                 .addFormParam("Value", "test").build();

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setKernelForInstanceInRegion", String.class, String.class,
               String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setKernelForInstance).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest setApiTerminationDisabled = HttpRequest.builder().method("POST")
                                                      .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                      .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                      .addFormParam("Action", "ModifyInstanceAttribute")
                                                      .addFormParam("Attribute", "disableApiTermination")
                                                      .addFormParam("InstanceId", "1")
                                                      .addFormParam("Value", "true").build();

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setApiTerminationDisabledForInstanceInRegion", String.class,
               String.class, boolean.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", true));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setApiTerminationDisabled).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest instanceTypeForInstance = HttpRequest.builder().method("POST")
                                                    .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                    .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                    .addFormParam("Action", "ModifyInstanceAttribute")
                                                    .addFormParam("Attribute", "instanceType")
                                                    .addFormParam("InstanceId", "1")
                                                    .addFormParam("Value", "c1.medium").build();

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setInstanceTypeForInstanceInRegion", String.class,
               String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", InstanceType.C1_MEDIUM));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(instanceTypeForInstance).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest setInstanceInitiatedShutdownBehavior = HttpRequest.builder().method("POST")
                                                                 .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                                 .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                                 .addFormParam("Action", "ModifyInstanceAttribute")
                                                                 .addFormParam("Attribute", "instanceInitiatedShutdownBehavior")
                                                                 .addFormParam("InstanceId", "1")
                                                                 .addFormParam("Value", "terminate").build();

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setInstanceInitiatedShutdownBehaviorForInstanceInRegion",
               String.class, String.class, InstanceInitiatedShutdownBehavior.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", InstanceInitiatedShutdownBehavior.TERMINATE));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setInstanceInitiatedShutdownBehavior).getPayload().getRawContent()
            .toString(), "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest setBlockDeviceMapping = HttpRequest.builder().method("POST")
                                                           .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                           .addFormParam("Action", "ModifyInstanceAttribute")
                                                           .addFormParam("BlockDeviceMapping.1.DeviceName", "/dev/sda1")
                                                           .addFormParam("BlockDeviceMapping.1.Ebs.DeleteOnTermination", "true")
                                                           .addFormParam("BlockDeviceMapping.1.Ebs.VolumeId", "vol-test1")
                                                           .addFormParam("InstanceId", "1").build();

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
         IOException {
      Invokable<?, ?> method = method(AWSInstanceAsyncClient.class, "setBlockDeviceMappingForInstanceInRegion", String.class,
            String.class, Map.class);

      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("/dev/sda1", new BlockDevice("vol-test1", true));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", mapping));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, filter.filter(setBlockDeviceMapping).getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
