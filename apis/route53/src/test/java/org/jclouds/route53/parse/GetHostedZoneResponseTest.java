/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.route53.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.route53.domain.HostedZone;
import org.jclouds.route53.domain.HostedZoneAndNameServers;
import org.jclouds.route53.xml.GetHostedZoneResponseHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
// NOTE:without testName, this will not call @Before* and fail w/NPE during surefire
@Test(groups = "unit", testName = "GetHostedZoneResponseTest")
public class GetHostedZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/hosted_zone.xml");

      HostedZoneAndNameServers expected = expected();

      GetHostedZoneResponseHandler handler = injector.getInstance(GetHostedZoneResponseHandler.class);
      HostedZoneAndNameServers result = factory.create(handler).parse(is);

      assertEquals(result, expected);
   }

   public HostedZoneAndNameServers expected() {
      return HostedZoneAndNameServers.create(HostedZone.builder()
                                           .id("Z21DW1QVGID6NG")
                                           .name("example.com.")
                                           .callerReference("a_unique_reference")
                                           .comment("Migrate an existing domain to Route 53").build(),
                                       ImmutableList.<String> builder()
                                                    .add("ns-1638.awsdns-12.co.uk")
                                                    .add("ns-144.awsdns-18.com")
                                                    .add("ns-781.awsdns-33.net")
                                                    .add("ns-1478.awsdns-56.org").build());
   }
}
