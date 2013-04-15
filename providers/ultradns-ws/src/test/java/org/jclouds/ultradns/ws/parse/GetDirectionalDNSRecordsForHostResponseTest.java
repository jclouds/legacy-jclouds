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

import static org.jclouds.ultradns.ws.domain.IdAndName.fromIdAndName;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
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
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000001")
                                                       .geolocationGroup(fromIdAndName("C000000000000001", "southamerica"))
                                                       .record(DirectionalRecord.drBuilder()
                                                                                .type("CNAME")
                                                                                .ttl(300)
                                                                                .noResponseRecord(false)
                                                                                .rdata("southamerica.geo.jclouds.org.").build()).build())
                           .add(DirectionalRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000002")
                                                       .group(fromIdAndName("B000000000000001", "All Non-Configured Regions"))
                                                       .record(DirectionalRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(500)
                                                                                .noResponseRecord(false)
                                                                                .rdata("1.1.1.2").build()).build())
                           .add(DirectionalRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000003")
                                                       .geolocationGroup(fromIdAndName("C000000000000002", "antarctica-unsupported"))
                                                       .record(DirectionalRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(0)
                                                                                .noResponseRecord(true)
                                                                                .rdata("No Data Response").build()).build())
                           .add(DirectionalRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000004")
                                                       .geolocationGroup(fromIdAndName("C000000000000003", "alazona"))
                                                       .record(DirectionalRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(86400) // default
                                                                                .noResponseRecord(false)
                                                                                .rdata("1.1.1.1").build()).build())
                           .build());
   }

}