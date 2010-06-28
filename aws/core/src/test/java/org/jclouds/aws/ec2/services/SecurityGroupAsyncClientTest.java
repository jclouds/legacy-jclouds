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

import org.jclouds.aws.ec2.domain.IpProtocol;
import org.jclouds.aws.ec2.domain.UserIdGroupPair;
import org.jclouds.aws.ec2.functions.ReturnVoidOnGroupNotFound;
import org.jclouds.aws.ec2.xml.DescribeSecurityGroupsResponseHandler;
import org.jclouds.http.functions.CloseContentAndReturn;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.SecurityGroupAsyncClientTest")
public class SecurityGroupAsyncClientTest extends BaseEC2AsyncClientTest<SecurityGroupAsyncClient> {

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("deleteSecurityGroupInRegion",
               String.class, String.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "name");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 60\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DeleteSecurityGroup&GroupName=name");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnGroupNotFound.class);

      checkFilters(httpMethod);
   }

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("createSecurityGroupInRegion",
               String.class, String.class, String.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "name", "description");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 89\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=CreateSecurityGroup&GroupDescription=description&GroupName=name");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSecurityGroups() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               (String) null);

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 48\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod, "Version=2009-11-30&Action=DescribeSecurityGroups");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testDescribeSecurityGroupsArgs() throws SecurityException, NoSuchMethodException,
            IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("describeSecurityGroupsInRegion",
               String.class, Array.newInstance(String.class, 0).getClass());
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "1", "2");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 48\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(httpMethod,
               "Version=2009-11-30&Action=DescribeSecurityGroups&GroupName.1=1&GroupName.2=2");

      assertResponseParserClassEquals(method, httpMethod, ParseSax.class);
      assertSaxResponseParserClassEquals(method, DescribeSecurityGroupsResponseHandler.class);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAuthorizeSecurityGroupIngressGroup() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod(
               "authorizeSecurityGroupIngressInRegion", String.class, String.class,
               UserIdGroupPair.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "group", new UserIdGroupPair("sourceUser", "sourceGroup"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 71\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=AuthorizeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testAuthorizeSecurityGroupIngressCidr() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod(
               "authorizeSecurityGroupIngressInRegion", String.class, String.class,
               IpProtocol.class, int.class, int.class, String.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 131\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=AuthorizeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&FromPort=6000&ToPort=7000");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRevokeSecurityGroupIngressGroup() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod(
               "revokeSecurityGroupIngressInRegion", String.class, String.class,
               UserIdGroupPair.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "group", new UserIdGroupPair("sourceUser", "sourceGroup"));

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 68\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RevokeSecurityGroupIngress&GroupName=group&SourceSecurityGroupOwnerId=sourceUser&SourceSecurityGroupName=sourceGroup");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   public void testRevokeSecurityGroupIngressCidr() throws SecurityException,
            NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod(
               "revokeSecurityGroupIngressInRegion", String.class, String.class, IpProtocol.class,
               int.class, int.class, String.class);
      GeneratedHttpRequest<SecurityGroupAsyncClient> httpMethod = processor.createRequest(method,
               null, "group", IpProtocol.TCP, 6000, 7000, "0.0.0.0/0");

      assertRequestLineEquals(httpMethod, "POST https://ec2.us-east-1.amazonaws.com/ HTTP/1.1");
      assertHeadersEqual(httpMethod,
               "Content-Length: 128\nContent-Type: application/x-www-form-urlencoded\nHost: ec2.us-east-1.amazonaws.com\n");
      assertPayloadEquals(
               httpMethod,
               "Version=2009-11-30&Action=RevokeSecurityGroupIngress&CidrIp=0.0.0.0%2F0&IpProtocol=tcp&GroupName=group&FromPort=6000&ToPort=7000");

      assertResponseParserClassEquals(method, httpMethod, CloseContentAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, null);

      checkFilters(httpMethod);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>>() {
      };
   }

}
