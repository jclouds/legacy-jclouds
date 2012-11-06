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
package org.jclouds.scriptbuilder.domain.chef;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;

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
      Role role = Role.builder().name("foo").installRecipe("apache2").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[\"recipe[apache2]\"]}");
   }

   public void testToJsonStringWithMultipleRecipes() {
      Role role = Role.builder().name("foo").installRecipe("apache2").installRecipe("git").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"recipe[apache2]\",\"recipe[git]\"]}");
   }

   public void testToJsonStringWithMultipleRecipesInList() {
      Role role = Role.builder().name("foo").installRecipes(ImmutableList.of("apache2", "git")).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"recipe[apache2]\",\"recipe[git]\"]}");
   }

   public void testToJsonStringWithSingleRole() {
      Role role = Role.builder().name("foo").installRole("webserver").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\",\"run_list\":[\"role[webserver]\"]}");
   }

   public void testToJsonStringWithMultipleRoles() {
      Role role = Role.builder().name("foo").installRole("webserver").installRole("firewall").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"role[webserver]\",\"role[firewall]\"]}");
   }

   public void testToJsonStringWithMultipleRolesInList() {
      Role role = Role.builder().name("foo").installRoles(ImmutableList.of("webserver", "firewall")).build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"role[webserver]\",\"role[firewall]\"]}");
   }

   public void testToJsonStringWithRolesAndRecipes() {
      Role role = Role.builder().name("foo").installRole("webserver").installRecipe("git").build();
      assertEquals(role.toJsonString(),
            "{\"name\": \"foo\",\"description\":\"\",\"default_attributes\":{},\"override_attributes\":{},"
                  + "\"json_class\":\"Chef::Role\",\"chef_type\":\"role\","
                  + "\"run_list\":[\"role[webserver]\",\"recipe[git]\"]}");
   }
}
