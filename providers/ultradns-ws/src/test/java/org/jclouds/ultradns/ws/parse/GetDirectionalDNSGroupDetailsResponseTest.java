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
 * "AS IS" BASIS, WITHOUT WADirectionalANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.ultradns.ws.parse;

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.DirectionalGroupNameAndRegions;
import org.jclouds.ultradns.ws.domain.Region;
import org.jclouds.ultradns.ws.xml.DirectionalGroupNameAndRegionsHandler;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetDirectionalDNSGroupDetailsResponseTest")
public class GetDirectionalDNSGroupDetailsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/directionalgroup.xml");

      DirectionalGroupNameAndRegions expected = expected();

      DirectionalGroupNameAndRegionsHandler handler = injector.getInstance(DirectionalGroupNameAndRegionsHandler.class);
      DirectionalGroupNameAndRegions result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public DirectionalGroupNameAndRegions expected() {
      return DirectionalGroupNameAndRegions.builder()
                                           .name("NON-EU")
                                           .addRegion(Region.builder()
                                                            .name("Anonymous Proxy (A1)")
                                                            .addTerritoryName("Anonymous Proxy").build())
                                           .addRegion(Region.builder()
                                                            .name("Mexico")
                                                            .addTerritoryName("Mexico").build())
                                           .addRegion(Region.builder()
                                                            .name("Antarctica")
                                                            .addTerritoryName("Bouvet Island")
                                                            .addTerritoryName("French Southern Territories")
                                                            .addTerritoryName("Antarctica").build()).build();
   }
}