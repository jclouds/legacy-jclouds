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

import static org.jclouds.ultradns.ws.domain.IdAndName.create;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecord;
import org.jclouds.ultradns.ws.domain.DirectionalPoolRecordDetail;
import org.jclouds.ultradns.ws.xml.DirectionalPoolRecordDetailListHandler;
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

      FluentIterable<DirectionalPoolRecordDetail> expected = expected();

      DirectionalPoolRecordDetailListHandler handler = injector.getInstance(DirectionalPoolRecordDetailListHandler.class);
      FluentIterable<DirectionalPoolRecordDetail> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<DirectionalPoolRecordDetail> expected() {
      return FluentIterable.from(ImmutableSet.<DirectionalPoolRecordDetail> builder()
                           .add(DirectionalPoolRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000001")
                                                       .geolocationGroup(create("C000000000000001", "southamerica"))
                                                       .record(DirectionalPoolRecord.drBuilder()
                                                                                .type("CNAME")
                                                                                .ttl(300)
                                                                                .noResponseRecord(false)
                                                                                .rdata("southamerica.geo.jclouds.org.").build()).build())
                           .add(DirectionalPoolRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000002")
                                                       .group(create("B000000000000001", "All Non-Configured Regions"))
                                                       .record(DirectionalPoolRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(500)
                                                                                .noResponseRecord(false)
                                                                                .rdata("1.1.1.2").build()).build())
                           .add(DirectionalPoolRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000003")
                                                       .geolocationGroup(create("C000000000000002", "antarctica-unsupported"))
                                                       .record(DirectionalPoolRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(0)
                                                                                .noResponseRecord(true)
                                                                                .rdata("No Data Response").build()).build())
                           .add(DirectionalPoolRecordDetail.builder()
                                                       .zoneName("geo.jclouds.org.")
                                                       .name("www.geo.jclouds.org.")
                                                       .id("A000000000000004")
                                                       .geolocationGroup(create("C000000000000003", "alazona"))
                                                       .record(DirectionalPoolRecord.drBuilder()
                                                                                .type("A")
                                                                                .ttl(86400) // default
                                                                                .noResponseRecord(false)
                                                                                .rdata("1.1.1.1").build()).build())
                           .build());
   }

}
