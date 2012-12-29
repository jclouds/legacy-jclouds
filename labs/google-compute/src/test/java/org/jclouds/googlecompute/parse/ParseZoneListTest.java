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
import org.jclouds.googlecompute.domain.ListPage;
import org.jclouds.googlecompute.domain.Resource;
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
public class ParseZoneListTest extends BaseGoogleComputeParseTest<ListPage<Zone>> {

   @Override
   public String resource() {
      return "/zone_list.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public ListPage<Zone> expected() {
      return ListPage.<Zone>builder()
              .kind(Resource.Kind.ZONE_LIST)
              .id("projects/google/zones")
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/zones"))
              .items(ImmutableSet.of(
                      new ParseZoneTest().expected()
                      , Zone.builder()
                      .id("13024414164050619686")
                      .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse
                              ("2012-10-24T20:13:19.271"))
                      .selfLink(URI.create("https://www.googleapis" +
                              ".com/compute/v1beta13/projects/google/zones/us-east1-a"))
                      .name("us-east1-a")
                      .description("us-east1-a")
                      .status(Zone.Status.UP)
                      .addMaintenanceWindow(Zone.MaintenanceWindow.builder()
                              .name("2013-02-17-planned-outage")
                              .description("maintenance zone")
                              .beginTime(new SimpleDateFormatDateService().iso8601DateParse
                                      ("2013-02-17T08:00:00.000"))
                              .endTime(new SimpleDateFormatDateService().iso8601DateParse
                                      ("2013-03-03T08:00:00.000"))
                              .build())
                      .build()))
              .build();
   }
}
