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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.Zone;
import org.jclouds.ultradns.ws.domain.Zone.DNSSECStatus;
import org.jclouds.ultradns.ws.xml.ZoneListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetZonesOfAccountResponseTest")
public class GetZonesOfAccountResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/zones.xml");

      FluentIterable<Zone> expected = expected();

      ZoneListHandler handler = injector.getInstance(ZoneListHandler.class);
      FluentIterable<Zone> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<Zone> expected() {
      return FluentIterable.from(ImmutableSet.<Zone> builder()
                           .add(Zone.builder()
                                    .name("jclouds.org.")
                                    .typeCode(1)
                                    .accountId("AAAAAAAAAAAAAAAA")
                                    .ownerId("EEEEEEEEEEEEEEEE")
                                    .id("0000000000000001")
                                    .dnssecStatus(DNSSECStatus.UNSIGNED).build())
                           .add(Zone.builder()
                                    .name("0.1.2.3.4.5.6.7.ip6.arpa.")
                                    .typeCode(1)
                                    .accountId("AAAAAAAAAAAAAAAA")
                                    .ownerId("EEEEEEEEEEEEEEEE")
                                    .id("0000000000000002")
                                    .dnssecStatus(DNSSECStatus.UNSIGNED).build())
                           .build());
   }

}
