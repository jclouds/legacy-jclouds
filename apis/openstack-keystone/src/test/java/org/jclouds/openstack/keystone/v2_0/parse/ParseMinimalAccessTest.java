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
package org.jclouds.openstack.keystone.v2_0.parse;

import javax.ws.rs.Consumes;
import javax.ws.rs.core.MediaType;

import org.jclouds.date.internal.SimpleDateFormatDateService;
import org.jclouds.json.BaseItemParserTest;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.rest.annotations.SelectJson;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ParseMinimalAccessTest")
public class ParseMinimalAccessTest extends BaseItemParserTest<Access> {

   @Override
   public String resource() {
      return "/access_minimal.json";
   }

   @Override
   @SelectJson("access")
   @Consumes(MediaType.APPLICATION_JSON)
   public Access expected() {
      return Access.builder()
                   .token(Token.builder()
                               .expires(new SimpleDateFormatDateService().iso8601SecondsDateParse("2012-12-02T01:44:54Z"))
                               .id("5afc3adea6654e758b4a9cf01bafe507").build())
                   .user(User.builder()
                             .id("bf45fd7586c2410c980c651b918aa850")
                             .name("nova")
//                             .username("nova") TODO: add optional username field!
                             .build()).build();
   }

}
