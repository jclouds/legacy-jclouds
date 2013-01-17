/*
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

package org.jclouds.googlecompute.parse;

import com.google.common.collect.ImmutableSet;
import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecompute.domain.Image;
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Resource;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseImageListTest extends BaseGoogleComputeParseTest<ListPage<Image>> {

   @Override
   public String resource() {
      return "/image_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Image> expected() {
      return ListPage.<Image>builder()
              .kind(Resource.Kind.IMAGE_LIST)
              .id("projects/google/images")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/images"))
              .items(ImmutableSet.of(Image.builder()
                      .id("12941197498378735318")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-07-16T22:16:13.468"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta13/projects/google/images/centos-6-2-v20120326"))
                      .name("centos-6-2-v20120326")
                      .description("DEPRECATED. CentOS 6.2 image; Created Mon, 26 Mar 2012 21:19:09 +0000")
                      .sourceType("RAW")
                      .preferredKernel(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta13/projects/google/kernels/gce-20120326"))
                      .rawDisk(
                              Image.RawDisk.builder()
                                      .source("")
                                      .containerType("TAR")
                                      .build()
                      ).build()

              ))
              .build();
   }
}