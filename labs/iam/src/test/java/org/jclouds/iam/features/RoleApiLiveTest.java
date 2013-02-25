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
package org.jclouds.iam.features;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.logging.Logger.getAnonymousLogger;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

import org.jclouds.iam.domain.Role;
import org.jclouds.iam.internal.BaseIAMApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "RoleApiLiveTest")
public class RoleApiLiveTest extends BaseIAMApiLiveTest {

   private void checkRole(Role role) {
      checkNotNull(role.getArn(), "Arn cannot be null for Role %s", role);
      checkNotNull(role.getId(), "Id cannot be null for Role %s", role);
      checkNotNull(role.getName(), "Name cannot be null for Role %s", role);
      checkNotNull(role.getPath(), "Path cannot be null for Role %s", role);
      checkNotNull(role.getAssumeRolePolicy(), "AssumeRolePolicy cannot be null for Role %s", role);
      checkNotNull(role.getCreateDate(), "CreateDate cannot be null for a Role Role %s", role);
   }

   @Test
   protected void testListRoles() {
      ImmutableList<Role> roles = api().list().concat().toImmutableList();
      getAnonymousLogger().info("roles: " + roles.size());

      for (Role role : roles) {
         checkRole(role);
         assertEquals(api().get(role.getName()), role);
         ImmutableSet<Role> rolesAtPath = api().listPathPrefix(role.getPath()).concat().toImmutableSet();
         assertTrue(rolesAtPath.contains(role), role + " not in " + rolesAtPath);
      }
   }

   @Test
   public void testGetRoleWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testDeleteRoleWhenNotFound() {
      api().delete("AAAAAAAAAAAAAAAA");
   }

   String policy = "{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}";

   @Test
   public void testCreateAndDeleteRole() {
      String name = System.getProperty("user.name").replace('.', '-') + ".role.iamtest.jclouds.org.";
      Role newRole; 
      try {
         newRole = api().createWithPolicy(name, policy);
         getAnonymousLogger().info("created role: " + newRole);
         checkRole(newRole);
      } finally {
         api().delete(name);
         assertNull(api().get(name));
      }
   }

   protected RoleApi api() {
      return context.getApi().getRoleApi();
   }
}
