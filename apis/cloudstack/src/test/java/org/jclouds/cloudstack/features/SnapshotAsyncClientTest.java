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
package org.jclouds.cloudstack.features;

import static org.jclouds.reflect.Reflection2.method;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.domain.SnapshotPolicySchedule;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.CreateSnapshotOptions;
import org.jclouds.cloudstack.options.ListSnapshotPoliciesOptions;
import org.jclouds.cloudstack.options.ListSnapshotsOptions;
import org.jclouds.cloudstack.util.SnapshotPolicySchedules;
import org.jclouds.fallbacks.MapHttp4xxCodesToExceptions;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ParseFirstJsonValueNamed;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.internal.GeneratedHttpRequest;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.Invokable;
/**
 * Tests the behaviour of SnapshotAsyncClient.
 * 
 * @see SnapshotAsyncClient
 * @author Richard Downer
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "SnapshotAsyncClientTest")
public class SnapshotAsyncClientTest extends BaseCloudStackAsyncClientTest<SnapshotAsyncClient> {

   public void testCreateSnapshot() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "createSnapshot", String.class, CreateSnapshotOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createSnapshot&volumeid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCreateSnapshotOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "createSnapshot", String.class, CreateSnapshotOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5, CreateSnapshotOptions.Builder.accountInDomain("acc", "7").policyId("9")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createSnapshot&volumeid=5&account=acc&domainid=7&policyid=9 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshots() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "listSnapshots", ListSnapshotsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshots&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testGetSnapshot() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "getSnapshot", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(5));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshots&listAll=true&id=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest,
            Functions.compose(IdentityFunction.INSTANCE, IdentityFunction.INSTANCE).getClass());
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshotsOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "listSnapshots", ListSnapshotsOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ListSnapshotsOptions.Builder.accountInDomain("acc", "7").id("5").interval(Snapshot.Interval.MONTHLY).isRecursive(true).keyword("fred").name("fred's snapshot").snapshotType(Snapshot.Type.RECURRING).volumeId("11")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshots&listAll=true&account=acc&domainid=7&id=5&intervaltype=MONTHLY&isrecursive=true&keyword=fred&name=fred%27s%20snapshot&snapshottype=RECURRING&volumeid=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ParseFirstJsonValueNamed.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteSnapshot() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "deleteSnapshot", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(14));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteSnapshot&id=14 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   HttpRequest extractIso = HttpRequest.builder().method("GET")
                                       .endpoint("http://localhost:8080/client/api")
                                       .addQueryParam("response", "json")
                                       .addQueryParam("command", "createSnapshotPolicy")
                                       .addQueryParam("maxsnaps", "10")
                                       .addQueryParam("timezone", "UTC")
                                       .addQueryParam("volumeid", "12")
                                       .addQueryParam("intervaltype", "MONTHLY")
                                       .addQueryParam("schedule", "07%3A06%3A05").build();

   public void testCreateSnapshotPolicy() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "createSnapshotPolicy", SnapshotPolicySchedule.class, String.class, String.class, String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(SnapshotPolicySchedules.monthly(5, 6, 7), 10, "UTC", 12));

      assertRequestLineEquals(httpRequest,extractIso.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testDeleteSnapshotPolicy() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "deleteSnapshotPolicy", String.class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(7));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteSnapshotPolicies&id=7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteSnapshotPolicies() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "deleteSnapshotPolicies", Iterable.class);
      Iterable<String> ids = ImmutableSet.of("3", "5", "7");
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(ids));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteSnapshotPolicies&ids=3,5,7 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshotPolicies() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "listSnapshotPolicies", String.class, ListSnapshotPoliciesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(10));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshotPolicies&listAll=true&volumeid=10 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshotPoliciesOptions() throws NoSuchMethodException {
      Invokable<?, ?> method = method(SnapshotAsyncClient.class, "listSnapshotPolicies", String.class, ListSnapshotPoliciesOptions[].class);
      GeneratedHttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(10, ListSnapshotPoliciesOptions.Builder.accountInDomain("fred", "4").keyword("bob")));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshotPolicies&listAll=true&volumeid=10&account=fred&domainid=4&keyword=bob HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }
}
