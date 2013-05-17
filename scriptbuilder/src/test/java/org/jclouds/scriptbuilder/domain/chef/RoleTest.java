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
package org.jclouds.scriptbuilder.domain.chef;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * Unit tests for the {@link Role} class.
 * 
 * @author Ignasi Barrera
 */
@Test(groups = "unit", testName = "RoleTest")
public class RoleTest {

   public void testToJsonStringWithOnlyName() {
      Role role = Role.builder().name("foo").build();
      assertEquals(role.toJsonString(), "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},"
            + "\"override_attributes\":{},\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[]}");
   }

   public void testToJsonStringWithDescription() {
      Role role = Role.builder().name("foo").description("Foo role").build();
      assertEquals(role.toJsonString(), "{\"name\": \"foo\",\"description\":\"Foo role\",\"default_attributes\":{},"
            + "\"override_attributes\":{},\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[]}");
   }

   public void testToJsonStringWithDefaultAttributes() {
      Role role = Role.builder().name("foo").jsonDefaultAttributes("{\"foo\":\"bar\"}").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{\"foo\":\"bar\"},"
                  + "\"override_attributes\":{},\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[]}");
   }

   public void testToJsonStringWithOverrideAttributes() {
      Role role = Role.builder().name("foo").jsonOverrideAttributes("{\"foo\":\"bar\"}").build();
      assertEquals(role.toJsonString(), "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},"
            + "\"override_attributes\":{\"foo\":\"bar\"},\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
            + "\"run_list\":[]}");
   }

   public void testToJsonStringWithSingleRecipe() {
      RunList runlist = RunList.builder().recipe("apache2").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[\"recipe[apache2]\"]}");
   }

   public void testToJsonStringWithMultipleRecipes() {
      RunList runlist = RunList.builder().recipe("apache2").recipe("git").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"recipe[apache2]\",\"recipe[git]\"]}");
   }

   public void testToJsonStringWithSingleRole() {
      RunList runlist = RunList.builder().role("webserver").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[\"role[webserver]\"]}");
   }

   public void testToJsonStringWithMultipleRoles() {
      RunList runlist = RunList.builder().role("webserver").role("firewall").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"role[webserver]\",\"role[firewall]\"]}");
   }

   public void testToJsonStringWithRolesAndRecipes() {
      RunList runlist = RunList.builder().role("webserver").recipe("git").build();
      Role role = Role.builder().name("foo").runlist(runlist).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"role[webserver]\",\"recipe[git]\"]}");
   }
}
