/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.ec2.services;

import static org.jclouds.reflect.Reflection2.method;

import java.io.IOException;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.reflect.Invokable;
/**
 * Tests behavior of {@code SecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SecurityGroupAsyncClientTest")
public class SecurityGroupAsyncClientTest extends BaseEC2AsyncClientTest<SecurityGroupAsyncClient> {

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "deleteSecurityGroupInRegion", String.class,
            String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteSecurityGroup&GroupName=name",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   HttpRequest createSecurityGroup = HttpRequest.builder().method("POST")
                                                .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                .addFormParam("Action", "CreateSecurityGroup")
                                                .addFormParam("GroupDescription", "description")
                                                .addFormParam("GroupName", "name")
                                                .addFormParam("Signature", "F3o0gnZcX9sWrtDUhVwi3k5GY2JKLP0Dhi6CcEqK2vE%3D")
                                                .addFormParam("SignatureMethod", "HmacSHA256")
                                                .addFormParam("SignatureVersion", "2")
                                                .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                .addFormParam("Version", "2010-06-15")
                                                .addFormParam("AWSAccessKeyId", "identity").build();

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "createSecurityGroupInRegion", String.class,
            String.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "name", "description"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, createSecurityGroup.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "describeSecurityGroupsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList((String) null));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSecurityGroups",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSecurityGroupsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "describeSecurityGroupsInRegion", String.class,
            String[].class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "1", "2"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSecurityGroups&GroupName.1=1&GroupName.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAuthorizeSecurityGroupIngressGroup() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "authorizeSecurityGroupIngressInRegion", String.class,
            String.class, UserIdGroupPair.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "group", new UserIdGroupPair("sourceUser",
            "sourceGroup")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=AuthorizeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest authorizeSecurityGroupIngressCidr = HttpRequest.builder().method("POST")
                                                              .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                              .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                              .addFormParam("Action", "AuthorizeSecurityGroupIngress")
                                                              .addFormParam("CidrIp", "0.0.0.0/0")
                                                              .addFormParam("FromPort", "6000")
                                                              .addFormParam("GroupName", "group")
                                                              .addFormParam("IpProtocol", "tcp")
                                                              .addFormParam("Signature", "6NQega9YUGDxdwk3Y0Hv71u/lHi%2B0D6qMCJLpJVD/aI%3D")
                                                              .addFormParam("SignatureMethod", "HmacSHA256")
                                                              .addFormParam("SignatureVersion", "2")
                                                              .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                              .addFormParam("ToPort", "7000")
                                                              .addFormParam("Version", "2010-06-15")
                                                              .addFormParam("AWSAccessKeyId", "identity").build();

   public void testAuthorizeSecurityGroupIngressCidr() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "authorizeSecurityGroupIngressInRegion", String.class,
            String.class, IpProtocol.class, int.class, int.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertPayloadEquals(request, authorizeSecurityGroupIngressCidr.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevokeSecurityGroupIngressGroup() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "revokeSecurityGroupIngressInRegion", String.class,
            String.class, UserIdGroupPair.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "group", new UserIdGroupPair("sourceUser",
            "sourceGroup")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RevokeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }

   HttpRequest revokeSecurityGroupIngressCidr = HttpRequest.builder().method("POST")
                                                           .endpoint("https://ec2.us-east-1.amazonaws.com/")
                                                           .addHeader("Host", "ec2.us-east-1.amazonaws.com")
                                                           .addFormParam("Action", "RevokeSecurityGroupIngress")
                                                           .addFormParam("CidrIp", "0.0.0.0/0")
                                                           .addFormParam("FromPort", "6000")
                                                           .addFormParam("GroupName", "group")
                                                           .addFormParam("IpProtocol", "tcp")
                                                           .addFormParam("Signature", "WPlDYXI8P6Ip4F2JIEP3lWrVlP/7gxbZvlshKYlrvxk%3D")
                                                           .addFormParam("SignatureMethod", "HmacSHA256")
                                                           .addFormParam("SignatureVersion", "2")
                                                           .addFormParam("Timestamp", "2009-11-08T15%3A54%3A08.897Z")
                                                           .addFormParam("ToPort", "7000")
                                                           .addFormParam("Version", "2010-06-15")
                                                           .addFormParam("AWSAccessKeyId", "identity").build();

   public void testRevokeSecurityGroupIngressCidr() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = method(SecurityGroupAsyncClient.class, "revokeSecurityGroupIngressInRegion", String.class,
            String.class, IpProtocol.class, int.class, int.class, String.class);
      GeneratedHttpRequest request = processor.createRequest(method, Lists.<Object> newArrayList(null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0"));

      request = (GeneratedHttpRequest) request.getFilters().get(0).filter(request);
      
      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, revokeSecurityGroupIngressCidr.getPayload().getRawContent().toString(),
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, null);

      checkFilters(request);
   }
}
