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

import static org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord.createCNAME;
import static org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail.Status.OK;
import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecordDetail;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolRecordDetailListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetTCPoolRecordsResponseTest")
public class GetTCPoolRecordsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/tcrecords.xml");

      FluentIterable<TrafficControllerPoolRecordDetail> expected = expected();

      TrafficControllerPoolRecordDetailListHandler handler = injector.getInstance(TrafficControllerPoolRecordDetailListHandler.class);
      FluentIterable<TrafficControllerPoolRecordDetail> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<TrafficControllerPoolRecordDetail> expected() {
      return FluentIterable.from(ImmutableSet.<TrafficControllerPoolRecordDetail> builder()
                           .add(TrafficControllerPoolRecordDetail.builder()
                                                           .id("0000000000000001")
                                                           .poolId("0000000000000001")
                                                           .record(createCNAME("canary.jclouds.org."))
                                                           .weight(2)
                                                           .priority(2)
                                                           .forceAnswer("Normal")
                                                           .probingEnabled(true)
                                                           .status(OK)
                                                           .serving(true)
                                                           .description("canary app").build())
                           .add(TrafficControllerPoolRecordDetail.builder()
                                                           .id("0000000000000002")
                                                           .poolId("0000000000000001")
                                                           .record(createCNAME("prod.jclouds.org."))
                                                           .weight(98)
                                                           .priority(1)
                                                           .forceAnswer("Normal")
                                                           .probingEnabled(true)
                                                           .status(OK)
                                                           .serving(true)
                                                           .description("prod app").build())
                           .build());
   }

}
