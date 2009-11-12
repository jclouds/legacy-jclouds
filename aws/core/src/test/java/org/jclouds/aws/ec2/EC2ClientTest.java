/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.aws.ec2;

import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.executableBy;
import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.URI;

import org.jclouds.aws.ec2.domain.ImageAttribute;
import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.filters.FormSigner;
import org.jclouds.aws.ec2.functions.ReturnVoidOnGroupNotFound;
import org.jclouds.aws.ec2.options.DescribeImagesOptions;
import org.jclouds.aws.ec2.options.RunInstancesOptions;
import org.jclouds.aws.ec2.xml.AllocateAddressResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeAddressesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeImagesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeKeyPairsResponseHandler;
import org.jclouds.aws.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.aws.ec2.xml.KeyPairResponseHandler;
import org.jclouds.aws.ec2.xml.RunInstancesResponseHandler;
import org.jclouds.aws.ec2.xml.TerminateInstancesResponseHandler;
import org.jclouds.aws.reference.AWSConstants;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReturnStringIf200;
import org.jclouds.http.functions.ReturnVoidIf2xx;
import org.jclouds.logging.Logger;
import org.jclouds.logging.Logger.LoggerFactory;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.jclouds.util.Jsr330;
import org.jclouds.util.TimeStamp;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code EC2Client}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.EC2ClientTest")
public class EC2ClientTest extends RestClientTest<EC2Client> {

   public void testDescribeImages() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("describeImages", Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 40\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DescribeImages");
      filter.filter(httpMethod);
      assertEntityEquals(
               httpMethod,
               "Action=DescribeImages&Signature=wJmKTnrx5v3PSECZJjm%2BE1s8I%2FqHkVs2K1hJ6yxbpC0%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-08-15&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImagesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeImages", Array.newInstance(
               DescribeImagesOptions.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, executableBy(
               "me").ownedBy("fred", "nancy").imageIds("1", "2"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 107\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=DescribeImages&ExecutableBy=me&Owner.1=fred&Owner.2=nancy&ImageId.1=1&ImageId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeImagesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeImageAttribute() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeImageAttribute", String.class,
               ImageAttribute.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "imageId",
               ImageAttribute.BLOCK_DEVICE_MAPPING);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 93\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DescribeImageAttribute&ImageId=imageId&Attribute=blockDeviceMapping");
      filter.filter(httpMethod);
      assertEntityEquals(
               httpMethod,
               "Action=DescribeImageAttribute&Attribute=blockDeviceMapping&ImageId=imageId&Signature=IiyxXwoOmpLiPC%2BbfBdqJwE758bvbs8kXCL71FefStY%3D&SignatureMethod=HmacSHA256&SignatureVersion=2&Timestamp=2009-11-08T15%3A54%3A08.897Z&Version=2009-08-15&AWSAccessKeyId=user");

      assertResponseParserClassEquals(method, httpMethod, ReturnStringIf200.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("describeInstances", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DescribeInstances");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeInstancesArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeInstances", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DescribeInstances&InstanceId.1=1&InstanceId.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testTerminateInstances() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("terminateInstances", String.class, Array
               .newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 59\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=TerminateInstances&InstanceId.0=1&InstanceId.1=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, TerminateInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstances() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("runInstances", String.class, int.class, int.class,
               Array.newInstance(RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "ami-voo", 1, 1);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 76\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=RunInstances&ImageId=ami-voo&MaxCount=1&MinCount=1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRunInstancesOptions() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("runInstances", String.class, int.class, int.class,
               Array.newInstance(RunInstancesOptions.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "ami-voo", 1, 5,
               new RunInstancesOptions().withKernelId("kernelId").enableMonitoring());

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 118\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=RunInstances&ImageId=ami-voo&MaxCount=5&MinCount=1&KernelId=kernelId&Monitoring.Enabled=true");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, RunInstancesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testCreateKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("createKeyPair", String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "mykey");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 53\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=CreateKeyPair&KeyName=mykey");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, KeyPairResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDisassociateAddress() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("disassociateAddress", InetAddress.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, InetAddress
               .getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 64\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DisassociateAddress&PublicIp=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAssociateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class
               .getMethod("associateAddress", InetAddress.class, String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, InetAddress
               .getByAddress(new byte[] { 127, 0, 0, 1 }), "me");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 75\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=AssociateAddress&PublicIp=127.0.0.1&InstanceId=me");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testReleaseAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("releaseAddress", InetAddress.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, InetAddress
               .getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 59\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=ReleaseAddress&PublicIp=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeAddresses() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("describeAddresses", Array.newInstance(
               InetAddress.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, InetAddress
               .getByAddress(new byte[] { 127, 0, 0, 1 }));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 43\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DescribeAddresses&PublicIp.1=127.0.0.1");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeAddressesResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAllocateAddress() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("allocateAddress");
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 41\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=AllocateAddress");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, AllocateAddressResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteKeyPair() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("deleteKeyPair", String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "mykey");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 53\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DeleteKeyPair&KeyName=mykey");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeKeyPairs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("describeKeyPairs", Array.newInstance(String.class,
               0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 42\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DescribeKeyPairs");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeKeyPairsArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeKeyPairs", Array.newInstance(String.class,
               0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 42\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DescribeKeyPairs&KeyName.1=1&KeyName.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeKeyPairsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("deleteSecurityGroup", String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "name");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 60\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DeleteSecurityGroup&GroupName=name");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnGroupNotFound.class);

      checkFilters(httpMethod);
   }

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("createSecurityGroup", String.class, String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "name",
               "description");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 89\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=CreateSecurityGroup&GroupName=name&GroupDescription=description");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSecurityGroups() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeSecurityGroups", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method);

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 48\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod, "Version=2009-08-15&Action=DescribeSecurityGroups");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSecurityGroupsArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = EC2Client.class.getMethod("describeSecurityGroups", Array.newInstance(
               String.class, 0).getClass());
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 48\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(httpMethod,
               "Version=2009-08-15&Action=DescribeSecurityGroups&GroupName.1=1&GroupName.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAuthorizeSecurityGroupIngressGroup() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("authorizeSecurityGroupIngress", String.class,
               UserIdGroupPair.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "group",
               new UserIdGroupPair("sourceUser", "sourceGroup"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 71\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=AuthorizeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAuthorizeSecurityGroupIngressCidr() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("authorizeSecurityGroupIngress", String.class,
               IpProtocol.class, int.class, int.class, String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "group",
               IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 131\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=AuthorizeSecurityGroupIngress&GroupName=group&FromPort=6000&IpProtocol=tcp&ToPort=7000&CidrIp=0.0.0.0%2F0");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRevokeSecurityGroupIngressGroup() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("revokeSecurityGroupIngress", String.class,
               UserIdGroupPair.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "group",
               new UserIdGroupPair("sourceUser", "sourceGroup"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 68\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=RevokeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRevokeSecurityGroupIngressCidr() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = EC2Client.class.getMethod("revokeSecurityGroupIngress", String.class,
               IpProtocol.class, int.class, int.class, String.class);
      GeneratedHttpRequest<EC2Client> httpMethod = processor.createRequest(method, "group",
               IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(httpMethod, "POST https://ec2.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 128\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.amazonaws.com\n");
      assertEntityEquals(
               httpMethod,
               "Version=2009-08-15&Action=RevokeSecurityGroupIngress&GroupName=group&FromPort=6000&IpProtocol=tcp&ToPort=7000&CidrIp=0.0.0.0%2F0");

      assertResponseParserClassEquals(method, httpMethod, ReturnVoidIf2xx.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected void checkFilters(GeneratedHttpRequest<EC2Client> httpMethod) {
      assertEquals(httpMethod.getFilters().size(), 1);
      assertEquals(httpMethod.getFilters().get(0).getClass(), FormSigner.class);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<EC2Client>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<EC2Client>>() {
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
      };
   }
}
