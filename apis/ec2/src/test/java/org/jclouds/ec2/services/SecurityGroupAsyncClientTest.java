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

import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.domain.UserIdGroupPair;
import org.jclouds.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SecurityGroupAsyncClientTest")
public class SecurityGroupAsyncClientTest extends BaseEC2AsyncClientTest<SecurityGroupAsyncClient> {

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("deleteSecurityGroupInRegion", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "name");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteSecurityGroup&GroupName=name",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("createSecurityGroupInRegion", String.class,
            String.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "name", "description");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=CreateSecurityGroup&GroupDescription=description&GroupName=name",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testDescribeSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, (String) null);

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSecurityGroups",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSecurityGroupsArgs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegion", String.class, Array
            .newInstance(String.class, 0).getClass());
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSecurityGroups&GroupName.1=1&GroupName.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testAuthorizeSecurityGroupIngressGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeSecurityGroupIngressInRegion", String.class,
            String.class, UserIdGroupPair.class);
      HttpRequest request = processor.createRequest(method, null, "group", new UserIdGroupPair("sourceUser",
            "sourceGroup"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=AuthorizeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAuthorizeSecurityGroupIngressCidr() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeSecurityGroupIngressInRegion", String.class,
            String.class, IpProtocol.class, int.class, int.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      try {
         assertPayloadEquals(
               request,
               "Action=AuthorizeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&FromPort=6000&ToPort=7000",
               "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(
               request,
               "Action=AuthorizeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&ToPort=7000&FromPort=6000",
               "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevokeSecurityGroupIngressGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("revokeSecurityGroupIngressInRegion", String.class,
            String.class, UserIdGroupPair.class);
      HttpRequest request = processor.createRequest(method, null, "group", new UserIdGroupPair("sourceUser",
            "sourceGroup"));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RevokeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevokeSecurityGroupIngressCidr() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("revokeSecurityGroupIngressInRegion", String.class,
            String.class, IpProtocol.class, int.class, int.class, String.class);
      HttpRequest request = processor.createRequest(method, null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      try {
         assertPayloadEquals(
               request,
               "Action=RevokeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&FromPort=6000&ToPort=7000",
               "application/x-www-form-urlencoded", false);
      } catch (AssertionError e) {
         // mvn 3.0 osx 10.6.5 somehow sorts differently
         assertPayloadEquals(
               request,
               "Action=RevokeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&ToPort=7000&FromPort=6000",
               "application/x-www-form-urlencoded", false);
      }
      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>>() {
      };
   }

}
