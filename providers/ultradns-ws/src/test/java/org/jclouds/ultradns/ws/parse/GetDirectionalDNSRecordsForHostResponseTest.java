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
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.domain.DirectionalRecord;
import org.jclouds.ultradns.ws.domain.DirectionalRecordDetail;
import org.jclouds.ultradns.ws.xml.DirectionalRecordDetailListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetDirectionalDNSRecordsForHostResponseTest")
public class GetDirectionalDNSRecordsForHostResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/directionalrecords.xml");

      FluentIterable<DirectionalRecordDetail> expected = expected();

      DirectionalRecordDetailListHandler handler = injector.getInstance(DirectionalRecordDetailListHandler.class);
      FluentIterable<DirectionalRecordDetail> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<DirectionalRecordDetail> expected() {
      return FluentIterable.from(ImmutableSet.<DirectionalRecordDetail> builder()
                           .add(DirectionalRecordDetail.builder()
                                                       .zoneName("directional-example.com.")
                                                       .name("chaos.directional-example.com.")
                                                       .id("06093C2D10CB1CB1")
                                                       .geolocationGroup(DirectionalGroup.builder()
                                                                                         .id("06093C2D10CB1CB2")
                                                                                         .name("Geolocation field")
                                                                                         .build())
                                                       .sourceIpGroup(DirectionalGroup.builder()
                                                                                      .id("06093C2D10CB1CB4")
                                                                                      .name("172.16.1.0/24")
                                                                                      .build())
                                                       .record(DirectionalRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(60)
                                                                                .noResponseRecord(false)
                                                                                .rdata("172.16.1.1").build()).build())
                           .build());
   }

}