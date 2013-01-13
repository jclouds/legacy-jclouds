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
import org.jclouds.googlecompute.domain.Network;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

/**
 * @author David Alves
 */
public class ParseNetworkTest extends BaseGoogleComputeParseTest<Network> {

   @Override
   public String resource() {
      return "/network_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Network expected() {
      return Network.builder()
              .id("13024414170909937976")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-10-24T20:13:19.967"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/myproject/networks/default"))
              .name("default")
              .description("Default network for the project")
              .IPv4Range("10.240.0.0/16")
              .gatewayIPv4("10.240.0.1")
              .build();
   }
}
