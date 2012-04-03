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
import java.lang.reflect.Method;

import org.jclouds.aws.ec2.options.CreateSecurityGroupOptions;
import org.jclouds.aws.ec2.xml.CreateSecurityGroupResponseHandler;
import org.jclouds.ec2.domain.IpPermission;
import org.jclouds.ec2.domain.IpProtocol;
import org.jclouds.ec2.util.IpPermissions;
import org.jclouds.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code AWSSecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "AWSSecurityGroupAsyncClientTest")
public class AWSSecurityGroupAsyncClientTest extends BaseAWSEC2AsyncClientTest<AWSSecurityGroupAsyncClient> {
   public AWSSecurityGroupAsyncClientTest() {
      provider = "aws-ec2";
   }

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("deleteSecurityGroupInRegionById", String.class,
            String.class);
      HttpRequest request = processor.createRequest(method, null, "id");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DeleteSecurityGroup&GroupId=id",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testDescribeSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegionById", String.class,
            String[].class);
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
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegionById", String.class,
            String[].class);
      HttpRequest request = processor.createRequest(method, null, "1", "2");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request, "Action=DescribeSecurityGroups&GroupId.1=1&GroupId.2=2",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(request);
   }

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("createSecurityGroupInRegionAndReturnId",
            String.class, String.class, String.class, CreateSecurityGroupOptions[].class);
      HttpRequest request = processor.createRequest(method, null, "name", "description");

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(request,
            "Action=CreateSecurityGroup&GroupDescription=description&GroupName=name",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ParseSax.class);
      assertSaxResponseParserClassEquals(method, CreateSecurityGroupResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAuthorizeSecurityGroupIpPermission() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("authorizeSecurityGroupIngressInRegion",
            String.class, String.class, IpPermission.class);
      HttpRequest request = processor.createRequest(method, null, "group", IpPermissions.permitAnyProtocol());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=AuthorizeSecurityGroupIngress&GroupId=group&IpPermissions.0.IpProtocol=-1&IpPermissions.0.FromPort=1&IpPermissions.0.ToPort=65535&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0%2F0",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testAuthorizeSecurityGroupIpPermissions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("authorizeSecurityGroupIngressInRegion",
            String.class, String.class, Iterable.class);
      HttpRequest request = processor.createRequest(method, null, "group", ImmutableSet.<IpPermission> of(IpPermissions
            .permit(IpProtocol.TCP).originatingFromCidrBlock("1.1.1.1/32"), IpPermissions.permitICMP().type(8).andCode(0)
            .originatingFromSecurityGroupId("groupId")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=AuthorizeSecurityGroupIngress&GroupId=group&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=1&IpPermissions.0.ToPort=65535&IpPermissions.0.IpRanges.0.CidrIp=1.1.1.1%2F32&IpPermissions.1.IpProtocol=icmp&IpPermissions.1.FromPort=8&IpPermissions.1.ToPort=0&IpPermissions.1.Groups.0.GroupId=groupId",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevokeSecurityGroupIpPermission() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("revokeSecurityGroupIngressInRegion", String.class,
            String.class, IpPermission.class);
      HttpRequest request = processor.createRequest(method, null, "group", IpPermissions.permitAnyProtocol());

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RevokeSecurityGroupIngress&GroupId=group&IpPermissions.0.IpProtocol=-1&IpPermissions.0.FromPort=1&IpPermissions.0.ToPort=65535&IpPermissions.0.IpRanges.0.CidrIp=0.0.0.0%2F0",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   public void testRevokeSecurityGroupIpPermissions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = AWSSecurityGroupAsyncClient.class.getMethod("revokeSecurityGroupIngressInRegion", String.class,
            String.class, Iterable.class);
      HttpRequest request = processor.createRequest(method, null, "group", ImmutableSet.<IpPermission> of(IpPermissions
            .permit(IpProtocol.TCP).originatingFromCidrBlock("1.1.1.1/32"), IpPermissions.permitICMP().type(8).andCode(0)
            .originatingFromSecurityGroupId("groupId")));

      assertRequestLineEquals(request, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertNonPayloadHeadersEqual(request, "Host: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
            request,
            "Action=RevokeSecurityGroupIngress&GroupId=group&IpPermissions.0.IpProtocol=tcp&IpPermissions.0.FromPort=1&IpPermissions.0.ToPort=65535&IpPermissions.0.IpRanges.0.CidrIp=1.1.1.1%2F32&IpPermissions.1.IpProtocol=icmp&IpPermissions.1.FromPort=8&IpPermissions.1.ToPort=0&IpPermissions.1.Groups.0.GroupId=groupId",
            "application/x-www-form-urlencoded", false);

      assertResponseParserClassEquals(method, request, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(request);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<AWSSecurityGroupAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<AWSSecurityGroupAsyncClient>>() {
      };
   }

}
