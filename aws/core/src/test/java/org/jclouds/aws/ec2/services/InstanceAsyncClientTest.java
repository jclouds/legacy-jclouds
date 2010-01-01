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

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Map;

import javax.inject.Singleton;

import org.jclouds.aws.ec2.EC2;
import org.jclouds.aws.ec2.domain.AvailabilityZone;
import org.jclouds.aws.ec2.domain.InstanceType;
import org.jclouds.aws.ec2.domain.Region;
import org.jclouds.aws.ec2.domain.Volume.InstanceInitiatedShutdownBehavior;
import org.jclouds.aws.ec2.filters.FormSigner;
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
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.date.TimeStamp;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code InstanceAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.InstanceAsyncClientTest")
public class InstanceAsyncClientTest extends RestClientTest<InstanceAsyncClient> {
   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("describeInstancesInRegion",
               Region.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT);

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
               Region.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

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
               Region.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

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
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", Region.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, null, "ami-voo", 1, 1);

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
      Method method = InstanceAsyncClient.class.getMethod("runInstancesInRegion", Region.class,
               AvailabilityZone.class, String.class, int.class, int.class, Array.newInstance(
                        RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.EU_WEST_1, AvailabilityZone.EU_WEST_1A, "ami-voo", 1, 5,
               new RunInstancesOptions().withKernelId("kernelId").enableMonitoring());

      assertRequestLineEquals(httpMethod, "POST https://ec2.eu-west-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(
               httpMethod,
               "Content-Length: 118\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.eu-west-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RunInstances&ImageId=ami-voo&MinCount=1&MaxCount=5&KernelId=kernelId&Monitoring.Enabled=true&Placement.AvailabilityZone=eu-west-1a");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testStopInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("stopInstancesInRegion", Region.class,
               boolean.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, true, "1", "2");

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
      Method method = InstanceAsyncClient.class.getMethod("rebootInstancesInRegion", Region.class,
               Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=RebootInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testStartInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("startInstancesInRegion", Region.class,
               Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "2");

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
               Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               "isApiTerminationDisabledForInstanceInRegion", Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               "getInstanceInitiatedShutdownBehaviorForInstanceInRegion", Region.class,
               String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               "getBlockDeviceMappingForInstanceInRegion", Region.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1");

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
               Region.class, String.class, Array.newInstance(byte.class, 0).getClass());
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "test".getBytes());

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

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetRamdiskForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setRamdiskForInstanceInRegion",
               Region.class, String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "test");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 91\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=ramdisk&Value=test&InstanceId=1");
      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetKernelForInstanceInRegion() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = InstanceAsyncClient.class.getMethod("setKernelForInstanceInRegion",
               Region.class, String.class, String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "test");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 90\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=kernel&Value=test&InstanceId=1");
      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetApiTerminationDisabledForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "setApiTerminationDisabledForInstanceInRegion", Region.class, String.class,
               boolean.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", true);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 105\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=disableApiTermination&Value=true&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetInstanceTypeForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod("setInstanceTypeForInstanceInRegion",
               Region.class, String.class, InstanceType.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", InstanceType.C1_MEDIUM);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 101\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=instanceType&Value=c1.medium&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetInstanceInitiatedShutdownBehaviorForInstanceInRegion()
            throws SecurityException, NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class.getMethod(
               "setInstanceInitiatedShutdownBehaviorForInstanceInRegion", Region.class,
               String.class, InstanceInitiatedShutdownBehavior.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", InstanceInitiatedShutdownBehavior.TERMINATE);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 122\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=instanceInitiatedShutdownBehavior&Value=terminate&InstanceId=1");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testSetBlockDeviceMappingForInstanceInRegion() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = InstanceAsyncClient.class
               .getMethod("setBlockDeviceMappingForInstanceInRegion", Region.class, String.class,
                        String.class);
      GeneratedHttpRequest<InstanceAsyncClient> httpMethod = processor.createRequest(method,
               Region.DEFAULT, "1", "test");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 102\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=ModifyInstanceAttribute&Attribute=blockDeviceMapping&Value=test&InstanceId=1");
      filter.filter(httpMethod);// ensure encoding worked properly
      assertPayloadEquals(
               httpMethod,
               "Action=ModifyInstanceAttribute&Attribute=blockDeviceMapping&InstanceId=1&Signature=KNCKfLATSmpXGuIBpXOx3lBmHv9tyu17Cxrfi%2FTzQHE%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Value=test&Version=2009-11-30&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<InstanceAsyncClient> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<InstanceAsyncClient>>() {
      };
   }

   private FormSigner filter;

   @Override
   @BeforeTest
   protected void setupFactory() {
      super.setupFactory();
      this.filter = injector.getInstance(FormSigner.class);
   }

   @Override
   protected Module createModule() {
      return new AbstractModule() {
         @Override
         protected void configure() {
            bind(URI.class).annotatedWith(EC2.class).toInstance(
                     URI.create("https://ec2.amazonaws.com"));
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_ACCESSKEYID)).to(
                     "user");
            bindConstant().annotatedWith(Jsr330.named(AWSConstants.PROPERTY_AWS_SECRETACCESSKEY))
                     .to("key");
            bind(Logger.LoggerFactory.class).toInstance(new LoggerFactory() {
               public Logger getLogger(String category) {
                  return Logger.NULL;
               }
            });
         }

         @SuppressWarnings("unused")
         @Provides
         @TimeStamp
         String provide() {
            return "2009-11-08T15:54:08.897Z";
         }

         @SuppressWarnings("unused")
         @Singleton
         @Provides
         Map<Region, URI> provideMap() {
            return ImmutableMap.<Region, URI> of(Region.DEFAULT, URI.create("https://booya"),
                     Region.EU_WEST_1, URI.create("https://ec2.eu-west-1.amazonaws.com"),
                     Region.US_EAST_1, URI.create("https://ec2.us-east-1.amazonaws.com"),
                     Region.US_WEST_1, URI.create("https://ec2.us-west-1.amazonaws.com"));
         }
      };
   }
}
