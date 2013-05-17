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
package org.jclouds.glesys.compute.functions;

import static org.jclouds.io.Payloads.newUrlEncodedFormPayload;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

import org.jclouds.compute.domain.HardwareBuilder;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.domain.OperatingSystem;
import org.jclouds.compute.domain.OsFamily;
import org.jclouds.compute.domain.Processor;
import org.jclouds.compute.domain.Volume;
import org.jclouds.compute.domain.internal.VolumeImpl;
import org.jclouds.glesys.compute.internal.BaseGleSYSComputeServiceExpectTest;
import org.jclouds.glesys.features.ServerApiExpectTest;
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
                        .endpoint("https://api.glesys.com/server/details/format/json")
                        .addHeader("Accept", "application/json")
                        .addHeader("Authorization", "Basic aWRlbnRpdHk6Y3JlZGVudGlhbA==")
                        .payload(
                              newUrlEncodedFormPayload(ImmutableMultimap.<String, String> builder()
                                    .put("serverid", "xm3276891").build())).build(),
                        HttpResponse
                              .builder()
                              .statusCode(200)
                              .payload(payloadFromResource("/server_details.json"))
                              .build()).build()

      ).getInstance(ServerDetailsToNodeMetadata.class);

      NodeMetadata actual = toTest.apply(ServerApiExpectTest.expectedServerDetails());
      assertNotNull(actual);

      assertEquals(
            actual.toString(),
            new NodeMetadataBuilder()
                  .ids("vz1840356")
                  .name("glesys-s")
                  .hostname("glesys-s")
                  .group("glesys")
                  .imageId("Ubuntu 10.04 LTS 32-bit")
                  .operatingSystem(
                        OperatingSystem.builder().name("Ubuntu 10.04 LTS 32-bit").family(OsFamily.UBUNTU).version("10.04")
                              .is64Bit(false).description("Ubuntu 10.04 LTS 32-bit").build())
                  .publicAddresses(ImmutableSet.of("31.192.231.254"))
                  .hardware(
                        new HardwareBuilder().ids("vz1840356").ram(512)
                              .processors(ImmutableList.of(new Processor(1, 1.0)))
                              .volumes(ImmutableList.<Volume> of(new VolumeImpl(5f, true, true))).hypervisor("OpenVZ")
                              .build()).status(Status.RUNNING).build().toString());
   }
}
