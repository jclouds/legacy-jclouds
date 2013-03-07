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
import org.jclouds.ultradns.ws.domain.LBPool;
import org.jclouds.ultradns.ws.domain.LBPool.Type;
import org.jclouds.ultradns.ws.xml.LBPoolListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetLoadBalancingPoolsByZoneResponseTest")
public class GetLoadBalancingPoolsByZoneResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/lbpools.xml");

      FluentIterable<LBPool> expected = expected();

      LBPoolListHandler handler = injector.getInstance(LBPoolListHandler.class);
      FluentIterable<LBPool> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<LBPool> expected() {
      return FluentIterable.from(ImmutableList.<LBPool> builder()
                           .add(LBPool.builder()
                                      .zoneId("0000000000000001")
                                      .id("000000000000001")
                                      .name("us-west-1c.app.jclouds.org.")
                                      .type(Type.TC).build())
                           .add(LBPool.builder()
                                      .zoneId("0000000000000001")
                                      .id("000000000000002")
                                      .name("app-uswest1.jclouds.org.")
                                      .type(Type.RD)
                                      .responseMethod(Type.RR).build())
                           .add(LBPool.builder()
                                      .zoneId("0000000000000001")
                                      .id("000000000000003")
                                      .name("app-uswest2.jclouds.org.")
                                      .type(Type.RD)
                                      .responseMethod(Type.RR).build())
                           .add(LBPool.builder()
                                      .zoneId("0000000000000001")
                                      .id("000000000000004")
                                      .name("app-euwest.jclouds.org.")
                                      .type(Type.RD)
                                      .responseMethod(Type.RR).build()).build());
   }
}