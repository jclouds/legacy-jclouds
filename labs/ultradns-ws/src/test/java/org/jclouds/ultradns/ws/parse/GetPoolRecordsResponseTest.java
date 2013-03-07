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
import org.jclouds.ultradns.ws.domain.PoolRecord;
import org.jclouds.ultradns.ws.xml.PoolRecordListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetPoolRecordsResponseTest")
public class GetPoolRecordsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/poolrecords.xml");

      FluentIterable<PoolRecord> expected = expected();

      PoolRecordListHandler handler = injector.getInstance(PoolRecordListHandler.class);
      FluentIterable<PoolRecord> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<PoolRecord> expected() {
      return FluentIterable.from(ImmutableSet.<PoolRecord> builder()
                           .add(PoolRecord.builder()
                                          .poolId("0603399D0413BC46")
                                          .id("0603399D0413BC47")
                                          .description("SiteBacker pool via API")
                                          .type("A")
                                          .pointsTo("172.16.8.1").build())
                           .add(PoolRecord.builder()
                                          .poolId("0603399D0413BC46")
                                          .id("060339A30416430C")
                                          .description("SiteBacker pool via API")
                                          .type("A")
                                          .pointsTo("172.16.8.2").build()).build());
   }
}
