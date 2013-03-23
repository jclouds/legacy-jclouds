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
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord;
import org.jclouds.ultradns.ws.domain.TrafficControllerPoolRecord.Status;
import org.jclouds.ultradns.ws.xml.TrafficControllerPoolRecordListHandler;
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

      FluentIterable<TrafficControllerPoolRecord> expected = expected();

      TrafficControllerPoolRecordListHandler handler = injector.getInstance(TrafficControllerPoolRecordListHandler.class);
      FluentIterable<TrafficControllerPoolRecord> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<TrafficControllerPoolRecord> expected() {
      return FluentIterable.from(ImmutableSet.<TrafficControllerPoolRecord> builder()
                           .add(TrafficControllerPoolRecord.builder()
                                                           .id("0000000000000001")
                                                           .poolId("0000000000000001")
                                                           .pointsTo("canary.jclouds.org.")
                                                           .weight(2)
                                                           .priority(2)
                                                           .type("CNAME")
                                                           .forceAnswer("Normal")
                                                           .probingEnabled(true)
                                                           .status(Status.OK)
                                                           .serving(true)
                                                           .description("canary app").build())
                           .add(TrafficControllerPoolRecord.builder()
                                                           .id("0000000000000002")
                                                           .poolId("0000000000000001")
                                                           .pointsTo("prod.jclouds.org.")
                                                           .weight(98)
                                                           .priority(1)
                                                           .type("CNAME")
                                                           .forceAnswer("Normal")
                                                           .probingEnabled(true)
                                                           .status(Status.OK)
                                                           .serving(true)
                                                           .description("prod app").build())
                           .build());
   }

}