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
package org.jclouds.glesys.compute.functions;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;

import java.net.URI;

import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.glesys.compute.internal.BaseGleSYSComputeServiceExpectTest;
import org.jclouds.glesys.features.ServerClientExpectTest;
import org.jclouds.http.HttpRequest;
import org.jclouds.http.HttpResponse;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;

/**
 * TODO
 * 
 */
@Test(groups = "unit", testName = "ServerDetailsToNodeMetadataTest")
public class ServerDetailsToNodeMetadataTest extends BaseGleSYSComputeServiceExpectTest {

   @Test
   public void testServerDetailsRequest() {

      ServerDetailsToNodeMetadata toTest = injectorForKnownArgumentsAndConstantPassword(
            ImmutableMap
                  .<HttpRequest, HttpResponse> builder()
                  .put(HttpRequest
                        .builder()
                        .method("POST")
                        .endpoint(URI.create("https://api.glesys.com/server/details/format/json"))
                        .headers(
                              ImmutableMultimap.<String, String> builder().put("Accept", "application/json")
                                    .put("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==").build())
                        .payload(
                              newUrlEncodedFormPayload(ImmutableMultimap.<String, String> builder()
                                    .put("serverid", "xm3276891").build())).build(),
                        HttpResponse
                              .builder()
                              .statusCode(200)
                              .payload(payloadFromResource("/server_details.json"))
                              .build()).build()

      ).getInstance(ServerDetailsToNodeMetadata.class);

      assertEquals(
            toTest.apply(ServerClientExpectTest.expectedServerDetails()),
            new NodeMetadataBuilder()
                  .ids("xm3276891")
                  .name("glesys-s-6dd")
                  .hostname("glesys-s-6dd")
                  .group("glesys-s")
                  .imageId("Ubuntu 11.04 x64")
                  .operatingSystem(
                        OperatingSystem.builder().name("Ubuntu 11.04 x64").family(OsFamily.UBUNTU).version("11.04")
                              .is64Bit(true).description("Ubuntu 11.04 x64").build())
                  .publicAddresses(ImmutableSet.of("109.74.10.45"))
                  .hardware(
                        new HardwareBuilder().ids("xm3276891").ram(512)
                              .processors(ImmutableList.of(new Processor(1, 1.0)))
                              .volumes(ImmutableList.<Volume> of(new VolumeImpl(5f, true, true))).hypervisor("Xen")
                              .build()).status(Status.RUNNING).build());
   }
}
