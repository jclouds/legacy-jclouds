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

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.internal.BaseCloudStackAsyncClientTest;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.http.HttpRequest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.reflect.Invokable;

/**
 * Tests behavior of {@code EventAsyncClient}
 * 
 * @author Vijay Kiran
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VolumeAsyncClientTest")
public class VolumeAsyncClientTest extends BaseCloudStackAsyncClientTest<VolumeAsyncClient> {

   public void testListVolumes() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("listVolumes", ListVolumesOptions[].class));
      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.of());

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVolumes&listAll=true HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, EmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testGetVolume() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("getVolume", String.class));
      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(111L));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVolumes&listAll=true&id=111 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, NullOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   HttpRequest createVolumeFromSnapshot = HttpRequest.builder().method("GET")
                                                     .endpoint("http://localhost:8080/client/api")
                                                     .addQueryParam("response", "json")
                                                     .addQueryParam("command", "createVolume")
                                                     .addQueryParam("name", "jclouds-volume")
                                                     .addQueryParam("snapshotid", "999")
                                                     .addQueryParam("zoneid", "111").build();

   public void testCreateVolumeWithSnapshot() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("createVolumeFromSnapshotInZone", String.class, String.class,
            String.class));
      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("jclouds-volume", 999L, 111l));

      assertRequestLineEquals(httpRequest, createVolumeFromSnapshot.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      checkFilters(httpRequest);

   }

   HttpRequest createVolumeFromDiskOffering = HttpRequest.builder().method("GET")
                                                         .endpoint("http://localhost:8080/client/api")
                                                         .addQueryParam("response", "json")
                                                         .addQueryParam("command", "createVolume")
                                                         .addQueryParam("name", "jclouds-volume")
                                                         .addQueryParam("diskofferingid", "999")
                                                         .addQueryParam("zoneid", "111").build();

   public void testCreateVolumeFromDiskOffering() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("createVolumeFromDiskOfferingInZone", String.class, String.class,
            String.class));

      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of("jclouds-volume", 999L, 111L));

      assertRequestLineEquals(httpRequest, createVolumeFromDiskOffering.getRequestLine());
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      checkFilters(httpRequest);

   }

   public void testAttachVolume() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("attachVolume", String.class, String.class));

      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(111L, 999L));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=attachVolume&id=111&virtualmachineid=999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      checkFilters(httpRequest);

   }

   public void testDetachVolume() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("detachVolume", String.class));

      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(111L));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=detachVolume&id=111 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      checkFilters(httpRequest);

   }

   public void testDeleteVolume() throws SecurityException, NoSuchMethodException, IOException {
      Invokable<?, ?> method = Invokable.from(VolumeAsyncClient.class.getMethod("deleteVolume", String.class));
      HttpRequest httpRequest = processor.createRequest(method, ImmutableList.<Object> of(111L));

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteVolume&id=111 HTTP/1.1");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertFallbackClassEquals(method, VoidOnNotFoundOr404.class);
      checkFilters(httpRequest);

   }
}
