/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
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

package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValue;
import org.jclouds.http.functions.UnwrapOnlyNestedJsonValueInSet;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "SecurityGroupAsyncClientTest")
public class SecurityGroupAsyncClientTest extends BaseCloudStackAsyncClientTest<SecurityGroupAsyncClient> {

   public void testListSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("listSecurityGroups", ListSecurityGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListSecurityGroupsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("listSecurityGroups", ListSecurityGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListSecurityGroupsOptions.Builder.virtualMachineId(4)
               .domainId(5).id(6));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups&virtualmachineid=4&domainid=5&id=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("getSecurityGroup", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValueInSet.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("createSecurityGroup", String.class);
      HttpRequest httpRequest = processor.createRequest(method, "goo");

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=createSecurityGroup&name=goo HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testAuthorizeIngressPortsToCIDRs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressPortsToCIDRs", long.class,
               String.class, int.class, int.class, Iterable.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, "tcp", 22, 22, ImmutableSet.of("1.1.1.1/24",
               "1.2.2.2/16"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=authorizeSecurityGroupIngress&securitygroupid=2&startport=22&protocol=tcp&endport=22&cidrlist=1.1.1.1%2F24%2C1.2.2.2%2F16 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testAuthorizeIngressPortsToSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressPortsToSecurityGroups", long.class,
               String.class, int.class, int.class, Multimap.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, "tcp", 22, 22, ImmutableMultimap.of("adrian",
               "group1", "adrian", "group2", "bob", "group1"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=authorizeSecurityGroupIngress&securitygroupid=2&startport=22&protocol=tcp&endport=22&usersecuritygrouplist%5B0%5D.account=adrian&usersecuritygrouplist%5B0%5D.group=group1&usersecuritygrouplist%5B1%5D.account=adrian&usersecuritygrouplist%5B1%5D.group=group2&usersecuritygrouplist%5B2%5D.account=bob&usersecuritygrouplist%5B2%5D.group=group1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testAuthorizeIngressICMPToCIDRs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressICMPToCIDRs", long.class, int.class,
               int.class, Iterable.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, 22, 22, ImmutableSet.of("1.1.1.1/24", "1.2.2.2/16"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=authorizeSecurityGroupIngress&protocol=ICMP&securitygroupid=2&icmptype=22&icmpcode=22&cidrlist=1.1.1.1%2F24%2C1.2.2.2%2F16 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testAuthorizeIngressICMPToSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressICMPToSecurityGroups", long.class,
               int.class, int.class, Multimap.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, 22, 22, ImmutableMultimap.of("adrian", "group1",
               "adrian", "group2", "bob", "group1"));

      assertRequestLineEquals(
               httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=authorizeSecurityGroupIngress&protocol=ICMP&securitygroupid=2&icmptype=22&icmpcode=22&usersecuritygrouplist%5B0%5D.account=adrian&usersecuritygrouplist%5B0%5D.group=group1&usersecuritygrouplist%5B1%5D.account=adrian&usersecuritygrouplist%5B1%5D.group=group2&usersecuritygrouplist%5B2%5D.account=bob&usersecuritygrouplist%5B2%5D.group=group1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRevokeIngressRule() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("revokeIngressRule", long.class,
               AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5, AccountInDomainOptions.Builder.accountInDomain(
               "adrian", 1));

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=revokeSecurityGroupIngress&id=5&account=adrian&domainid=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyNestedJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("deleteSecurityGroup", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
               "GET http://localhost:8080/client/api?response=json&command=deleteSecurityGroup&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SecurityGroupAsyncClient>>() {
      };
   }
}
