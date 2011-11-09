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

import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.domain.Snapshot;
import org.jclouds.cloudstack.options.CreateSnapshotOptions;
import org.jclouds.cloudstack.options.ListSnapshotsOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.functions.ReleasePayloadAndReturn;
import org.jclouds.http.functions.UnwrapOnlyJsonValue;
import org.jclouds.rest.functions.MapHttp4xxCodesToExceptions;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

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
      Method method = SnapshotAsyncClient.class.getMethod("createSnapshot", long.class, CreateSnapshotOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createSnapshot&volumeid=5 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testCreateSnapshotOptions() throws NoSuchMethodException {
      Method method = SnapshotAsyncClient.class.getMethod("createSnapshot", long.class, CreateSnapshotOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, 5, CreateSnapshotOptions.Builder.accountInDomain("acc", 7).policyId(9));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createSnapshot&volumeid=5&account=acc&domainid=7&policyid=9 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, MapHttp4xxCodesToExceptions.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshots() throws NoSuchMethodException {
      Method method = SnapshotAsyncClient.class.getMethod("listSnapshots", ListSnapshotsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshots HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testListSnapshotsOptions() throws NoSuchMethodException {
      Method method = SnapshotAsyncClient.class.getMethod("listSnapshots", ListSnapshotsOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method, ListSnapshotsOptions.Builder.accountInDomain("acc", 7).id(5).intervalType(Snapshot.SnapshotIntervalType.MONTHLY).isRecursive(true).keyword("fred").name("fred's snapshot").snapshotType(Snapshot.SnapshotType.RECURRING).volumeId(11));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listSnapshots&account=acc&domainid=7&id=5&intervaltype=MONTHLY&isrecursive=true&keyword=fred&name=fred%27s%20snapshot&snapshottype=RECURRING&volumeid=11 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, UnwrapOnlyJsonValue.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   public void testDeleteSnapshot() throws NoSuchMethodException {
      Method method = SnapshotAsyncClient.class.getMethod("deleteSnapshot", long.class);
      HttpRequest httpRequest = processor.createRequest(method, 14);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteSnapshot&id=14 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertResponseParserClassEquals(method, httpRequest, ReleasePayloadAndReturn.class);
      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);

      checkFilters(httpRequest);
   }

   @Override
   protected TypeLiteral<RestAnnotationProcessor<SnapshotAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<SnapshotAsyncClient>>() {
      };
   }
}
