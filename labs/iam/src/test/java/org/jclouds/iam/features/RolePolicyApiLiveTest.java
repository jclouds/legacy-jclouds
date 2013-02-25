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

import org.jclouds.iam.domain.Policy;
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.internal.BaseIAMApiLiveTest;
import org.testng.annotations.Test;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "RolePolicyApiLiveTest")
public class RolePolicyApiLiveTest extends BaseIAMApiLiveTest {

   private void checkPolicy(Policy policy) {
      checkNotNull(policy.getOwner(), "Owner cannot be null for Policy %s", policy);
      checkNotNull(policy.getName(), "Name cannot be null for Policy %s", policy);
      checkNotNull(policy.getDocument(), "Document cannot be null for Policy %s", policy);
   }

   @Test
   protected void testListRolePolicies() {
      for (Role role : context.getApi().getRoleApi().list().concat()) {
         for (String policy : api(role.getName()).list().concat()) {
            checkPolicy(api(role.getName()).get(policy));
         }
      }
   }

   String assumeRolePolicy = "{\"Version\":\"2008-10-17\",\"Statement\":[{\"Sid\":\"\",\"Effect\":\"Allow\",\"Principal\":{\"Service\":\"ec2.amazonaws.com\"},\"Action\":\"sts:AssumeRole\"}]}";
   String s3Policy = "{\"Statement\":[{\"Effect\":\"Allow\",\"Action\":\"s3:*\",\"Resource\":\"*\"}]}";

   @Test
   public void testCreateAndDeleteRolePolicy() {
      String roleName = System.getProperty("user.name").replace('.', '-') + ".role_policy.iamtest.jclouds.org.";
      Role newRole;
      try {
         newRole = context.getApi().getRoleApi().createWithPolicy(roleName, assumeRolePolicy);
         getAnonymousLogger().info("created role: " + newRole);
         api(roleName).create("S3Access", s3Policy);
         Policy newPolicy = api(roleName).get("S3Access");
         getAnonymousLogger().info("created policy: " + newPolicy);
         checkPolicy(newPolicy);
         assertEquals(newPolicy.getDocument(), s3Policy);
         api(roleName).delete("S3Access");
         assertNull(api(roleName).get("S3Access"));
      } finally {
         api(roleName).delete("S3Access");
         context.getApi().getRoleApi().delete(roleName);
      }
   }

   protected RolePolicyApi api(String role) {
      return context.getApi().getPolicyApiForRole(role);
   }
}
