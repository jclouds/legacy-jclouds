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
import org.jclouds.googlecompute.domain.Disk;
import org.jclouds.googlecompute.domain.PagedList;
import org.jclouds.googlecompute.domain.Resource;
import org.jclouds.json.BaseItemParserTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseDiskListTest extends BaseItemParserTest<PagedList<Disk>> {

   @Override
   public String resource() {
      return "/disk_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public PagedList<Disk> expected() {
      return PagedList.<Disk>builder()
              .kind(Resource.Kind.DISK_LIST)
              .id("projects/myproject/disks")
              .selfLink("https://www.googleapis.com/compute/v1beta13/projects/myproject/disks")
              .items(ImmutableSet.of(Disk.builder()
                      .id("13050421646334304115")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-11-25T01:38:48.306"))
                      .selfLink("https://www.googleapis.com/compute/v1beta13/projects/myproject/disks/testimage1")
                      .name("testimage1")
                      .sizeGb(1)
                      .zone("https://www.googleapis.com/compute/v1beta13/projects/myproject/zones/us-central1-a")
                      .status("READY")
                      .build())
              ).build();
   }
}
