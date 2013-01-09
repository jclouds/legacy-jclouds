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
import com.google.common.reflect.Invokable;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("describeInstancesInRegion", String.class, String[].class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("terminateInstancesInRegion", String.class,
               String[].class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
               String.class, int.class, int.class, RunInstancesOptions[].class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, null, "ami-voo", 1, 1));

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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("runInstancesInRegion", String.class, String.class,
               String.class, int.class, int.class, RunInstancesOptions[].class));
      GeneratedHttpRequest request = processor.createRequest(method, ImmutableList.<Object> of("eu-west-1", "eu-west-1a", "ami-voo",
               1, 5, new RunInstancesOptions().withKernelId("kernelId").withSecurityGroups("group1", "group2")));

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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("stopInstancesInRegion", String.class, boolean.class,
               String[].class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("rebootInstancesInRegion", String.class, String[].class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("startInstancesInRegion", String.class, String[].class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getUserDataForInstanceInRegion", String.class, String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getRootDeviceNameForInstanceInRegion", String.class,
               String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getRamdiskForInstanceInRegion", String.class, String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("isApiTerminationDisabledForInstanceInRegion", String.class,
               String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getKernelForInstanceInRegion", String.class, String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getInstanceTypeForInstanceInRegion", String.class,
               String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getInstanceInitiatedShutdownBehaviorForInstanceInRegion",
               String.class, String.class));
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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getBlockDeviceMappingForInstanceInRegion", String.class,
               String.class));
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
                                                   .addFormParam("Signature", "LfUmzLM5DsACR5nQcEfGF5FPdznOwwhJ7tjhBWfHtGs%3D")
                                                   .addFormParam("SignatureMethod", "HmacSHA256")
                                                   .addFormParam("SignatureVersion", "2")
                                                   .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                   .addFormParam("Value", "dGVzdA%3D%3D")
                                                   .addFormParam("Version", "2010-06-15")
                                                   .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetUserDataForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setUserDataForInstanceInRegion", String.class, String.class,
               byte[].class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test".getBytes()));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, setUserDataForInstance.getPayload().getRawContent().toString(),
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
                                                  .addFormParam("Signature", "qx6NeVbihiYrKvi5Oe5LzMsGHTjS7%2BqoNhh2abt275g%3D")
                                                  .addFormParam("SignatureMethod", "HmacSHA256")
                                                  .addFormParam("SignatureVersion", "2")
                                                  .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                  .addFormParam("Value", "test")
                                                  .addFormParam("Version", "2010-06-15")
                                                  .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setRamdiskForInstanceInRegion", String.class, String.class,
               String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, setRamdiskForInstance.getPayload().getRawContent().toString(),
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
                                                 .addFormParam("Signature", "juSiuoiXJzTxj3q0LUW2528HzDyP4JAcKin%2BI4AuIT0%3D")
                                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                                 .addFormParam("SignatureVersion", "2")
                                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                 .addFormParam("Value", "test")
                                                 .addFormParam("Version", "2010-06-15")
                                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setKernelForInstanceInRegion", String.class, String.class,
               String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "test"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, setKernelForInstance.getPayload().getRawContent().toString(),
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
                                                      .addFormParam("Signature", "tiBMWWTi22BWeAjsRfuzVom0tQgsOBeYTkatMuWRrbg%3D")
                                                      .addFormParam("SignatureMethod", "HmacSHA256")
                                                      .addFormParam("SignatureVersion", "2")
                                                      .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                      .addFormParam("Value", "true")
                                                      .addFormParam("Version", "2010-06-15")
                                                      .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setApiTerminationDisabledForInstanceInRegion", String.class,
               String.class, boolean.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", true));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, setApiTerminationDisabled.getPayload().getRawContent().toString(),
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
                                                    .addFormParam("Signature", "XK%2BzQmQ0S57gXIgVRMqUkKunURN9TaCJD1YWiYMAOHo%3D")
                                                    .addFormParam("SignatureMethod", "HmacSHA256")
                                                    .addFormParam("SignatureVersion", "2")
                                                    .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                    .addFormParam("Value", "c1.medium")
                                                    .addFormParam("Version", "2010-06-15")
                                                    .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setInstanceTypeForInstanceInRegion", String.class,
               String.class, String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", InstanceType.C1_MEDIUM));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, instanceTypeForInstance.getPayload().getRawContent().toString(),
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
                                                                 .addFormParam("Signature", "s5xBMLd%2BXNVp44x7C6qVE58qBov//f6yvxoM757KcZU%3D")
                                                                 .addFormParam("SignatureMethod", "HmacSHA256")
                                                                 .addFormParam("SignatureVersion", "2")
                                                                 .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                                 .addFormParam("Value", "terminate")
                                                                 .addFormParam("Version", "2010-06-15")
                                                                 .addFormParam("AWSAccessKeyId", "identity").build();

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setInstanceInitiatedShutdownBehaviorForInstanceInRegion",
               String.class, String.class, InstanceInitiatedShutdownBehavior.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", InstanceInitiatedShutdownBehavior.TERMINATE));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, setInstanceInitiatedShutdownBehavior.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("setBlockDeviceMappingForInstanceInRegion", String.class,
               String.class, Map.class));

      Map<String, BlockDevice> mapping = Maps.newLinkedHashMap();
      mapping.put("/dev/sda1", new BlockDevice("vol-test1", true));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", mapping));

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
      Invokable<?, ?> method = Invokable.from(InstanceAsyncClient.class.getMethod("getConsoleOutputForInstanceInRegion", String.class, String.class));
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1"));

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
