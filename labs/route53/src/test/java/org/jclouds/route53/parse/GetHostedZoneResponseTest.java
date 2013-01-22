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
package org.jclouds.route53.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.route53.domain.Zone;
import org.jclouds.route53.domain.ZoneAndNameServers;
import org.jclouds.route53.xml.GetHostedZoneResponseHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetHostedZoneResponseTest")
public class GetHostedZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/hosted_zone.xml");

      ZoneAndNameServers expected = expected();

      GetHostedZoneResponseHandler handler = injector.getInstance(GetHostedZoneResponseHandler.class);
      ZoneAndNameServers result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   public ZoneAndNameServers expected() {
      return ZoneAndNameServers.builder()
                               .addNameServer("ns-1638.awsdns-12.co.uk")
                               .addNameServer("ns-144.awsdns-18.com")
                               .addNameServer("ns-781.awsdns-33.net")
                               .addNameServer("ns-1478.awsdns-56.org")
                               .zone(Zone.builder()
                                         .id("/hostedzone/Z21DW1QVGID6NG")
                                         .name("example.com.")
                                         .callerReference("a_unique_reference")
                                         .comment("Migrate an existing domain to Route 53").build()).build();
   }
}
