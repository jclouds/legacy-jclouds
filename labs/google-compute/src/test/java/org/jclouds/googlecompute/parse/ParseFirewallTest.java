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
import org.jclouds.googlecompute.domain.Firewall;
import org.jclouds.googlecompute.internal.BaseGoogleComputeParseTest;
import org.testng.annotations.Test;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;
import java.net.URI;

import static org.jclouds.googlecompute.domain.Firewall.Rule.IPProtocol;

/**
 * @author David Alves
 */
@Test(groups = "unit")
public class ParseFirewallTest extends BaseGoogleComputeParseTest<Firewall> {

   @Override
   public String resource() {
      return "/firewall_get.json";
   }

   @Override
   @Consumes(MediaType.APPLICATION_JSON)
   public Firewall expected() {
      return Firewall.builder()
              .id("12862241031274216284")
              .creationTimestamp(new SimpleDateFormatDateService().iso8601DateParse("2012-04-13T03:05:02.855"))
              .selfLink(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/firewalls/default" +
                      "-allow-internal"))
              .name("default-allow-internal")
              .description("Internal traffic from default allowed")
              .network(URI.create("https://www.googleapis.com/compute/v1beta13/projects/google/networks/default"))
              .addSourceRange("10.0.0.0/8")
              .addAllowed(Firewall.Rule.builder()
                      .IPProtocol(IPProtocol.TCP)
                      .addPortRange(1, 65535).build())
              .addAllowed(Firewall.Rule.builder()
                      .IPProtocol(IPProtocol.UDP)
                      .addPortRange(1, 65535).build())
              .addAllowed(Firewall.Rule.builder()
                      .IPProtocol(IPProtocol.ICMP).build())
              .build();

   }
}
