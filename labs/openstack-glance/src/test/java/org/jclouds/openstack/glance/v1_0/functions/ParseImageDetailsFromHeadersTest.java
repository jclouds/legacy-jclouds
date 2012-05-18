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
package org.jclouds.openstack.glance.v1_0.functions;

import static org.testng.Assert.assertEquals;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.HttpResponse;
import org.jclouds.openstack.glance.v1_0.domain.ContainerFormat;
import org.jclouds.openstack.glance.v1_0.domain.DiskFormat;
import org.jclouds.openstack.glance.v1_0.domain.Image;
import org.jclouds.openstack.glance.v1_0.domain.ImageDetails;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseImageDetailsFromHeadersTest")
public class ParseImageDetailsFromHeadersTest {

   ParseImageDetailsFromHeaders fn = new ParseImageDetailsFromHeaders(new SimpleDateFormatDateService());

   public HttpResponse response = HttpResponse.builder()
                                       .message("HTTP/1.1 200 OK")
                                       .statusCode(200)
                                       .headers(ImmutableMultimap.<String,String>builder()
                                                .put("X-Image-Meta-Id", "fcc451d0-f6e4-4824-ad8f-70ec12326d07")
                                                .put("X-Image-Meta-Deleted", "False")
                                                .put("X-Image-Meta-Container_format", "bare")
                                                .put("X-Image-Meta-Checksum", "233afa7b8809d840679b5f0d36d7350a")
                                                .put("X-Image-Meta-Protected", "False")
                                                .put("X-Image-Meta-Min_disk", "0")
                                                .put("X-Image-Meta-Created_at", "2012-05-18T18:06:44")
                                                .put("X-Image-Meta-Size", "65645798")
                                                .put("X-Image-Meta-Status", "active")
                                                .put("X-Image-Meta-Is_public", "True")
                                                .put("X-Image-Meta-Min_ram", "0")
                                                .put("X-Image-Meta-Owner", "5821675")
                                                .put("X-Image-Meta-Updated_at", "2012-05-18T18:42:58")
                                                .put("X-Image-Meta-Disk_format", "raw")
                                                .put("X-Image-Meta-Name", "debian")
                                                .put("Location", "http://HOST/v1/images/fcc451d0-f6e4-4824-ad8f-70ec12326d07")
                                                .put("Etag", "233afa7b8809d840679b5f0d36d7350a")
                                                .build())
                                       .build();

   public void test() {
      assertEquals(fn.apply(response).toString(), expected().toString());
   }

   public ImageDetails expected() {
      return ImageDetails.builder()
                        .id("fcc451d0-f6e4-4824-ad8f-70ec12326d07")
                        .name("debian")
                        .containerFormat(ContainerFormat.BARE)
                        .diskFormat(DiskFormat.RAW)
                        .checksum("233afa7b8809d840679b5f0d36d7350a")
                        .size(65645798l)
                        .status(Image.Status.ACTIVE)
                        .owner("5821675")
                        .isPublic(true)
                        .createdAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-18T18:06:44"))
                        .updatedAt(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-05-18T18:42:58"))
                        .build();
   }
}
