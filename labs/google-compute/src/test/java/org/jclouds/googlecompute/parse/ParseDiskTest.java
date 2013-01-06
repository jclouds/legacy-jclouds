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

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseDiskTest extends BaseGoogleComputeParseTest<Disk> {

   @Override
   public String resource() {
      return "/disk_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Disk expected() {
      return Disk.builder()
              .id("13050421646334304115")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T01:38:48.306"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject/disks/testimage1"))
              .name("testimage1")
              .sizeGb(1)
              .zone(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject/zones/us-central1-a"))
              .status("READY")
              .build();
   }
}
