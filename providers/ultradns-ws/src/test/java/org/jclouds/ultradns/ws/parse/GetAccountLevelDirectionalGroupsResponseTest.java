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
import org.jclouds.ultradns.ws.domain.AccountLevelGroup;
import org.jclouds.ultradns.ws.domain.DirectionalPool.Type;
import org.jclouds.ultradns.ws.xml.AccountLevelGroupsHandler;
import org.testng.annotations.Test;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

/**
 * @author Adrian Cole
 */
@Test(testName = "GetAccountLevelDirectionalGroupsResponseTest")
public class GetAccountLevelDirectionalGroupsResponseTest extends BaseHandlerTest {

   public void test() {
      InputStream is = getClass().getResourceAsStream("/accountlevelgroups.xml");

      FluentIterable<AccountLevelGroup> expected = expected();

      AccountLevelGroupsHandler handler = injector.getInstance(AccountLevelGroupsHandler.class);
      FluentIterable<AccountLevelGroup> result = factory.create(handler).parse(is);

      assertEquals(result.toSet().toString(), expected.toSet().toString());
   }

   public FluentIterable<AccountLevelGroup> expected() {
      return FluentIterable.from(ImmutableList.<AccountLevelGroup> builder()
                           .add(AccountLevelGroup.builder()
                                                 .id("000000000000000A")
                                                 .name("ASIA")
                                                 .type(Type.GEOLOCATION)
                                                 .recordCount(0).build())
                           .add(AccountLevelGroup.builder()
                                                 .id("000000000000000B")
                                                 .name("EU")
                                                 .type(Type.GEOLOCATION)
                                                 .recordCount(3).build())
                           .add(AccountLevelGroup.builder()
                                                 .id("000000000000000C")
                                                 .name("LATAM")
                                                 .type(Type.GEOLOCATION)
                                                 .recordCount(1).build()).build());
   }
}
