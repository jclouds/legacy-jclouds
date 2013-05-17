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

import static org.jclouds.ultradns.ws.domain.ResourceRecord.rrBuilder;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail;
import org.jclouds.ultradns.ws.domain.ResourceRecordDetail.Builder;
import org.jclouds.ultradns.ws.xml.ResourceRecordListHandler;
import org.testng.annotations.Test;

import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetResourceRecordsOfResourceRecordResponseTest")
public class GetResourceRecordsOfResourceRecordResponseTest extends BaseHandlerTest {
   SimpleDateFormatDateService dateService = new SimpleDateFormatDateService();

   @Test
   public void test() {
      InputStream is = getClass().getResourceAsStream("/records.xml");

      FluentIterable<ResourceRecordDetail> expected = expected();

      ResourceRecordListHandler handler = injector.getInstance(ResourceRecordListHandler.class);
      FluentIterable<ResourceRecordDetail> result = factory.create(handler).parse(is);

      assertEquals(result.toList().toString(), expected.toList().toString());
   }

   public FluentIterable<ResourceRecordDetail> expected() {
      Builder builder = ResourceRecordDetail.builder().zoneId("0000000000000001").zoneName("jclouds.org.");
      ImmutableList<ResourceRecordDetail> records = ImmutableList.<ResourceRecordDetail> builder()
      .add(builder.guid("04023A2507B6468F")
                  .created(dateService.iso8601DateParse("2010-10-02T16:57:16.000Z"))
                  .modified(dateService.iso8601DateParse("2011-09-27T23:49:21.000Z"))
                  .record(rrBuilder().type(1).name("www.jclouds.org.").ttl(3600).rdata("1.2.3.4")).build())
      .add(builder.guid("0B0338C2023F7969")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns2.ultradns.net.")).build())
      .add(builder.guid("0B0338C2023F7968")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns1.ultradns.net.")).build())
      .add(builder.guid("0B0338C2023F796B")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns4.ultradns.org.")).build())
      .add(builder.guid("0B0338C2023F7983")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2011-09-27T23:49:22.000Z"))
                  .record(rrBuilder().type(6).name("jclouds.org.").ttl(3600).rdata(Splitter.on(' ').split(
                               "pdns2.ultradns.net. admin.jclouds.org. 2011092701 10800 3600 604800 86400"))).build())
      .add(builder.guid("0B0338C2023F796E")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2011-09-27T23:49:22.000Z"))
                  .record(rrBuilder().type(1).name("jclouds.org.").ttl(3600).rdata("1.2.3.4")).build())
      .add(builder.guid("0B0338C2023F796C")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns5.ultradns.info.")).build())
      .add(builder.guid("0B0338C2023F796D")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns6.ultradns.co.uk.")).build())
      .add(builder.guid("0B0338C2023F796A")
                  .created(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .modified(dateService.iso8601DateParse("2009-10-12T12:02:23.000Z"))
                  .record(rrBuilder().type(2).name("jclouds.org.").ttl(86400).rdata("pdns3.ultradns.org.")).build())
      .build();
      return FluentIterable.from(records);
   }

}
