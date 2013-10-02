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
package org.jclouds.openstack.keystone.v2_0.functions.internal;

import static org.testng.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;

import org.jclouds.json.config.GsonModule;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneParserModule;
import org.jclouds.openstack.keystone.v2_0.domain.User;
import org.jclouds.openstack.keystone.v2_0.functions.internal.ParseUsers.Users;
import org.jclouds.util.Strings2;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Guice;

/**
 * 
 * @author Adrian Cole
 */
@Test(testName = "ParseUsersTest")
public class ParseUsersTest {

   private Gson gson = Guice.createInjector(new GsonModule(), new KeystoneParserModule()).getInstance(Gson.class);
   private Type usersMapType = new TypeToken<Map<String, Set<? extends User>>>() {
      private static final long serialVersionUID = 1L;
   }.getType();

   Set<User> expectedUsers = ImmutableSet.of(
         User.builder().name("nova").id("e021dfd758eb44a89f1c57c8ef3be8e2").build(),
         User.builder().name("glance").id("3f6c1c9ba993495ead7d2eb2192e284f").build(),
         User.builder().name("demo").id("667b2e1420604df8b67cd8ea57d4ee64").build(),
         User.builder().name("admin").id("2b9b606181634ae9ac86fd95a8bc2cde").build());

   public void testParseUsersInMap() throws JsonSyntaxException, IOException {
      String json = Strings2.toStringAndClose(getClass().getResourceAsStream("/user_list.json"));
      Map<String, Set<? extends User>> users = gson.fromJson(json, usersMapType);
      assertEquals(users.get("users"), expectedUsers);
   }

   public void testParseUsers() throws JsonSyntaxException, IOException {
      String json = Strings2.toStringAndClose(getClass().getResourceAsStream("/user_list.json"));
      Users users = gson.fromJson(json, Users.class);
      assertEquals(users.toSet(), expectedUsers);
   }
}
