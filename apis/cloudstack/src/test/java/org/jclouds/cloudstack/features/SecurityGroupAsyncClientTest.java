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
package org.jclouds.cloudstack.features;

import java.io.IOException;
import java.lang.reflect.Method;

import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.AccountInDomainOptions;
import org.jclouds.cloudstack.options.ListSecurityGroupsOptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import com.google.inject.TypeLiteral;

/**
 * Tests behavior of {@code SecurityGroupAsyncClient}
 * 
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SecurityGroupAsyncClientTest")
public class SecurityGroupAsyncClientTest extends BaseCloudStackAsyncClientTest<SecurityGroupAsyncClient> {

   public void testListSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("listSecurityGroups", ListSecurityGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testListSecurityGroupsOptions() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("listSecurityGroups", ListSecurityGroupsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListSecurityGroupsOptions.Builder.virtualMachineId("4")
            .domainId("5").id("6"));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups&listAll=true&virtualmachineid=4&domainid=5&id=6 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("getSecurityGroup", String.class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSecurityGroups&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
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

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest authorizeSecurityGroupIngress3 = HttpRequest.builder().method("GET")
                                                           .endpoint("http://localhost:8080/client/api")
                                                           .addQueryParam("response", "json")
                                                           .addQueryParam("command", "authorizeSecurityGroupIngress")
                                                           .addQueryParam("securitygroupid", "2")
                                                           .addQueryParam("protocol", "tcp")
                                                           .addQueryParam("startport", "22")
                                                           .addQueryParam("endport", "22")
                                                           .addQueryParam("cidrlist", "1.1.1.1/24,1.2.2.2/16").build();

   public void testAuthorizeIngressPortsToCIDRs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressPortsToCIDRs", String.class,
            String.class, int.class, int.class, Iterable.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, "tcp", 22, 22,
            ImmutableSet.of("1.1.1.1/24", "1.2.2.2/16"));

      assertRequestLineEquals(httpRequest, authorizeSecurityGroupIngress3.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest authorizeSecurityGroupIngress4 = HttpRequest.builder().method("GET")
                                                           .endpoint("http://localhost:8080/client/api")
                                                           .addQueryParam("response", "json")
                                                           .addQueryParam("command", "authorizeSecurityGroupIngress")
                                                           .addQueryParam("securitygroupid", "2")
                                                           .addQueryParam("protocol", "tcp")
                                                           .addQueryParam("startport", "22")
                                                           .addQueryParam("endport", "22")
                                                           .addQueryParam("usersecuritygrouplist%5B0%5D.account", "adrian")
                                                           .addQueryParam("usersecuritygrouplist%5B0%5D.group", "group1")
                                                           .addQueryParam("usersecuritygrouplist%5B1%5D.account", "adrian")
                                                           .addQueryParam("usersecuritygrouplist%5B1%5D.group", "group2")
                                                           .addQueryParam("usersecuritygrouplist%5B2%5D.account", "bob")
                                                           .addQueryParam("usersecuritygrouplist%5B2%5D.group", "group1").build();

   public void testAuthorizeIngressPortsToSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressPortsToSecurityGroups", String.class,
            String.class, int.class, int.class, Multimap.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, "tcp", 22, 22,
            ImmutableMultimap.of("adrian", "group1", "adrian", "group2", "bob", "group1"));

      assertRequestLineEquals(httpRequest, authorizeSecurityGroupIngress4.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest authorizeSecurityGroupIngress1 = HttpRequest.builder().method("GET")
                                                           .endpoint("http://localhost:8080/client/api")
                                                           .addQueryParam("response", "json")
                                                           .addQueryParam("command", "authorizeSecurityGroupIngress")
                                                           .addQueryParam("protocol", "ICMP")
                                                           .addQueryParam("securitygroupid", "2")
                                                           .addQueryParam("icmpcode", "22")
                                                           .addQueryParam("icmptype", "22")
                                                           .addQueryParam("cidrlist", "1.1.1.1/24,1.2.2.2/16").build();

   public void testAuthorizeIngressICMPToCIDRs() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressICMPToCIDRs", String.class , int.class,
            int.class, Iterable.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, 22, 22, ImmutableSet.of("1.1.1.1/24", "1.2.2.2/16"));

      assertRequestLineEquals(httpRequest, authorizeSecurityGroupIngress1.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   HttpRequest authorizeSecurityGroupIngress2 = HttpRequest.builder().method("GET")
                                                           .endpoint("http://localhost:8080/client/api")
                                                           .addQueryParam("response", "json")
                                                           .addQueryParam("command", "authorizeSecurityGroupIngress")
                                                           .addQueryParam("protocol", "ICMP")
                                                           .addQueryParam("securitygroupid", "2")
                                                           .addQueryParam("icmpcode", "22")
                                                           .addQueryParam("icmptype", "22")
                                                           .addQueryParam("usersecuritygrouplist%5B0%5D.account", "adrian")
                                                           .addQueryParam("usersecuritygrouplist%5B0%5D.group", "group1")
                                                           .addQueryParam("usersecuritygrouplist%5B1%5D.account", "adrian")
                                                           .addQueryParam("usersecuritygrouplist%5B1%5D.group", "group2")
                                                           .addQueryParam("usersecuritygrouplist%5B2%5D.account", "bob")
                                                           .addQueryParam("usersecuritygrouplist%5B2%5D.group", "group1").build();

   public void testAuthorizeIngressICMPToSecurityGroups() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("authorizeIngressICMPToSecurityGroups", String.class,
            int.class, int.class, Multimap.class, AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 2, 22, 22,
            ImmutableMultimap.of("adrian", "group1", "adrian", "group2", "bob", "group1"));

      assertRequestLineEquals(httpRequest, authorizeSecurityGroupIngress2.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);

   }

   public void testRevokeIngressRule() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("revokeIngressRule", String.class,
            AccountInDomainOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5,
            AccountInDomainOptions.Builder.accountInDomain("adrian", "1"));

      assertRequestLineEquals(
            httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=revokeSecurityGroupIngress&id=5&account=adrian&domainid=1 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testDeleteSecurityGroup() throws SecurityException, NoSuchMethodException, IOException {
      Method method = SecurityGroupAsyncClient.class.getMethod("deleteSecurityGroup", String.class);
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
