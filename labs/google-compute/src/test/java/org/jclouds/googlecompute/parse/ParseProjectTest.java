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

import com.google.common.collect.ImmutableMap;
import org.jclouds.googlecompute.domain.Project;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;
import java.util.Date;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseProjectTest extends BaseGoogleComputeParseTest<Project> {

   @Override
   public String resource() {
      return "/project.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Project expected() {
      return Project.builder()
              .id("13024414184846275913")
              .creationTimestamp(new Date(Long.parseLong("1351109596252")))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject"))
              .name("myproject")
              .description("")
              .commonInstanceMetadata(
                      ImmutableMap.<String, String>builder()
                              .put("propA", "valueA")
                              .put("propB", "valueB")
                              .build())
              .addQuota("INSTANCES", 0, 8)
              .addQuota("CPUS", 0, 8)
              .addQuota("EPHEMERAL_ADDRESSES", 0, 8)
              .addQuota("DISKS", 0, 8)
              .addQuota("DISKS_TOTAL_GB", 0, 100)
              .addQuota("SNAPSHOTS", 0, 1000)
              .addQuota("NETWORKS", 1, 5)
              .addQuota("FIREWALLS", 2, 100)
              .addQuota("IMAGES", 0, 100)
              .build();
   }
}
