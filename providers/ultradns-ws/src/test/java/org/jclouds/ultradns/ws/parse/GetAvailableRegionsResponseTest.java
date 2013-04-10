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
import java.util.Map;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.Region;
import org.jclouds.ultradns.ws.xml.RegionListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetAvailableRegionsResponseTest")
public class GetAvailableRegionsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/regions.xml");

      Map<Integer, Region> expected = expected();

      RegionListHandler handler = injector.getInstance(RegionListHandler.class);
      Map<Integer, Region> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public Map<Integer, Region> expected() {
      return ImmutableMap.<Integer, Region> builder()
                         .put(14, Region.builder()
                                        .name("Anonymous Proxy (A1)")
                                        .addTerritoryName("Anonymous Proxy").build())
                         .put(3, Region.builder()
                                       .name("Antarctica")
                                       .territoryNames(ImmutableSet.<String> builder()
                                                                   .add("Antarctica")
                                                                   .add("Bouvet Island")
                                                                   .add("French Southern Territories").build())
                                       .build())
                         .build();
   }
}
