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
import org.jclouds.ultradns.ws.domain.IdAndName;
import org.jclouds.ultradns.ws.xml.RegionListHandler;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetAvailableRegionsResponseTest")
public class GetAvailableRegionsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/regions.xml");

      Multimap<IdAndName, String> expected = expected();

      RegionListHandler handler = injector.getInstance(RegionListHandler.class);
      Multimap<IdAndName, String> result = factory.create(handler).parse(is);

      assertEquals(result.toString(), expected.toString());
   }

   public Multimap<IdAndName, String> expected() {
      return ImmutableMultimap.<IdAndName, String> builder()
                         .put(create("14", "Anonymous Proxy (A1)"), "Anonymous Proxy")
                         .putAll(create("3", "Antarctica"), ImmutableSet.<String> builder()
                                                                               .add("Antarctica")
                                                                               .add("Bouvet Island")
                                                                               .add("French Southern Territories")
                                                                               .build())
                         .build();
   }
}
