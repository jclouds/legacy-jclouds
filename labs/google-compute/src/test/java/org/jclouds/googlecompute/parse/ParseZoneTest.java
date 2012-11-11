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
import org.jclouds.googlecompute.domain.Zone;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseZoneTest extends BaseGoogleComputeParseTest<Zone> {

   @Override
   public String resource() {
      return "/zone_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Zone expected() {
      return Zone.builder()
              .id("13020128040171887099")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-10-19T16:42:54.131"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/zones/us-central2-a"))
              .name("us-central2-a")
              .description("us-central2-a")
              .status(Zone.Status.DOWN)
              .addMaintenanceWindow(Zone.MaintenanceWindow.builder()
                      .name("2012-11-10-planned-outage")
                      .description("maintenance zone")
                      .beginTime(new SimpleDateFormatDateService().iso8601DateParse("2012-11-10T20:00:00.000"))
                      .endTime(new SimpleDateFormatDateService().iso8601DateParse("2012-12-02T20:00:00.000"))
                      .build())
              .build();
   }
}
