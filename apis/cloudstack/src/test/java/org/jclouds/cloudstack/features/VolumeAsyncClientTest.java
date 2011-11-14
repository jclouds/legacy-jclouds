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

import com.google.inject.TypeLiteral;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.http.HttpRequest;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.rest.internal.RestAnnotationProcessor;
import org.testng.annotations.Test;

/**
 * Tests behavior of {@code EventAsyncClient}
 *
 * @author Vijay Kiran
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during
// surefire
@Test(groups = "unit", testName = "VolumeAsyncClientTest")
public class VolumeAsyncClientTest extends BaseCloudStackAsyncClientTest<VolumeAsyncClient> {

   protected String prefix = System.getProperty("user.name");

   public void testListVolumes() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VolumeAsyncClient.class.getMethod("listVolumes", ListVolumesOptions[].class);
      HttpRequest httpRequest = processor.createRequest(method);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=listVolumes HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnEmptySetOnNotFoundOr404.class);

      checkFilters(httpRequest);

   }

   public void testCreateVolumeWithSnapshot() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VolumeAsyncClient.class.getMethod("createVolumeWithSnapshot", String.class, Long.class);
      HttpRequest httpRequest = processor.createRequest(method, prefix + "-jclouds-volume", 999L);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createVolume&name="
                  + prefix +"-jclouds-volume&snapshotid=999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);
      checkFilters(httpRequest);

   }

   public void testCreateVolumeWithDiskOffering() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VolumeAsyncClient.class.getMethod("createVolumeWithDiskOfferingInZone",
                                                         String.class, Long.class, Long.class);
      HttpRequest httpRequest = processor.createRequest(method, prefix + "-jclouds-volume", 999L, 111L);

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=createVolume&name="
                  + prefix +"-jclouds-volume&zoneid=111&diskofferingid=999 HTTP/1.1");
      assertNonPayloadHeadersEqual(httpRequest, "Accept: application/json\n");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnNullOnNotFoundOr404.class);
      checkFilters(httpRequest);

   }

   public void testDeleteVolume() throws SecurityException, NoSuchMethodException, IOException {
      Method method = VolumeAsyncClient.class.getMethod("deleteVolume", Long.class);
      HttpRequest httpRequest = processor.createRequest(method, prefix + "-jclouds-volume");

      assertRequestLineEquals(httpRequest,
            "GET http://localhost:8080/client/api?response=json&command=deleteVolume&id="
                  + prefix +"-jclouds-volume HTTP/1.1");
      assertPayloadEquals(httpRequest, null, null, false);

      assertSaxResponseParserClassEquals(method, null);
      assertExceptionParserClassEquals(method, ReturnVoidOnNotFoundOr404.class);
      checkFilters(httpRequest);

   }




   @Override
   protected TypeLiteral<RestAnnotationProcessor<VolumeAsyncClient>> createTypeLiteral() {
      return new TypeLiteral<RestAnnotationProcessor<VolumeAsyncClient>>() {
      };
   }
}
