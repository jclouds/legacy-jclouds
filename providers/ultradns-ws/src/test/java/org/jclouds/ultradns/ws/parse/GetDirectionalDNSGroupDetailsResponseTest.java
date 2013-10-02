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

import static org.testng.Assert.assertEquals;

import java.io.InputStream;

import org.jclouds.http.functions.BaseHandlerTest;
import org.jclouds.ultradns.ws.domain.DirectionalGroup;
import org.jclouds.ultradns.ws.xml.DirectionalGroupHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetDirectionalDNSGroupDetailsResponseTest")
public class GetDirectionalDNSGroupDetailsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/directionalgroup.xml");

      DirectionalGroup expected = expected();

      DirectionalGroupHandler handler = injector.getInstance(DirectionalGroupHandler.class);
      DirectionalGroup result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public DirectionalGroup expected() {
      return DirectionalGroup.builder()
                             .name("NON-EU")
                             .mapRegionToTerritory("Anonymous Proxy (A1)", "Anonymous Proxy")
                             .mapRegionToTerritory("Mexico", "Mexico")
                             .mapRegionToTerritories("Antarctica", ImmutableList.<String> builder()
                                                                   .add("Antarctica")
                                                                   .add("Bouvet Island")
                                                                   .add("French Southern Territories")
                                                                   .build()).build();
   }
}
