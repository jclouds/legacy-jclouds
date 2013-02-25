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

import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.internal.BaseIAMApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "InstanceProfileApiLiveTest")
public class InstanceProfileApiLiveTest extends BaseIAMApiLiveTest {

   static void checkInstanceProfile(InstanceProfile instanceProfile) {
      checkNotNull(instanceProfile.getArn(), "Arn cannot be null for InstanceProfile %s", instanceProfile);
      checkNotNull(instanceProfile.getId(), "Id cannot be null for InstanceProfile %s", instanceProfile);
      checkNotNull(instanceProfile.getName(), "Name cannot be null for InstanceProfile %s", instanceProfile);
      checkNotNull(instanceProfile.getPath(), "Path cannot be null for InstanceProfile %s", instanceProfile);
      checkNotNull(instanceProfile.getRoles(), "Roles cannot be null for InstanceProfile %s", instanceProfile);
      checkNotNull(instanceProfile.getCreateDate(),
            "CreateDate cannot be null for a InstanceProfile InstanceProfile %s", instanceProfile);
      for (Role role : instanceProfile.getRoles()) {
         RoleApiLiveTest.checkRole(role);
      }
   }

   @Test
   protected void testListInstanceProfiles() {
      ImmutableList<InstanceProfile> instanceProfiles = api().list().concat().toImmutableList();
      getAnonymousLogger().info("instanceProfiles: " + instanceProfiles.size());

      for (InstanceProfile instanceProfile : instanceProfiles) {
         checkInstanceProfile(instanceProfile);
         assertEquals(api().get(instanceProfile.getName()), instanceProfile);
         ImmutableSet<InstanceProfile> instanceProfilesAtPath = api().listPathPrefix(instanceProfile.getPath())
               .concat().toImmutableSet();
         assertTrue(instanceProfilesAtPath.contains(instanceProfile), instanceProfile + " not in "
               + instanceProfilesAtPath);
      }
   }

   @Test
   public void testGetInstanceProfileWhenNotFound() {
      assertNull(api().get("AAAAAAAAAAAAAAAA"));
   }

   @Test
   public void testDeleteInstanceProfileWhenNotFound() {
      api().delete("AAAAAAAAAAAAAAAA");
   }

   String name = System.getProperty("user.name").replace('.', '-') + ".instanceProfile.iamtest.jclouds.org.";

   @Test
   public void testCreateInstanceProfile() {
      InstanceProfile newInstanceProfile = api().create(name);
      getAnonymousLogger().info("created instanceProfile: " + newInstanceProfile);
      checkInstanceProfile(newInstanceProfile);
      assertEquals(newInstanceProfile.getName(), name);
   }

   @Test(dependsOnMethods = "testCreateInstanceProfile")
   public void testAddRoleRemoveRoleFromInstanceProfile() {
      try {
         createRoleWithPolicy(name);
         api().addRole(name, name);
         InstanceProfile updated = api().get(name);
         RoleApiLiveTest.checkRole(updated.getRoles().get(0));

         api().removeRole(name, name);
         updated = api().get(name);
         assertEquals(updated.getRoles(), ImmutableList.of());
      } finally {
         tearDownRoleWithPolicy(name);
      }
   }

   @Test(dependsOnMethods = "testAddRoleRemoveRoleFromInstanceProfile")
   public void testDeleteInstanceProfile() {
      api().delete(name);
      assertNull(api().get(name));
   }

   protected InstanceProfileApi api() {
      return context.getApi().getInstanceProfileApi();
   }

   void createRoleWithPolicy(String roleName) {
      context.getApi().getRoleApi().createWithPolicy(roleName, RoleApiLiveTest.assumeRolePolicy);
      context.getApi().getPolicyApiForRole(roleName).create("S3Access", RolePolicyApiLiveTest.s3Policy);
   }

   void tearDownRoleWithPolicy(String roleName) {
      context.getApi().getPolicyApiForRole(roleName).delete("S3Access");
      context.getApi().getRoleApi().delete(roleName);
   }
}
