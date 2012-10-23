/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.domain.enterprise;

import static org.jclouds.abiquo.predicates.enterprise.UserPredicates.nick;
import static org.jclouds.abiquo.util.Assert.assertHasError;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import javax.ws.rs.core.Response.Status;

import org.jclouds.abiquo.domain.exception.AbiquoException;
import org.jclouds.abiquo.internal.BaseAbiquoApiLiveApiTest;
import org.jclouds.abiquo.predicates.enterprise.UserPredicates;
import org.testng.annotations.Test;

import com.abiquo.server.core.enterprise.UserDto;
import com.google.common.collect.Iterables;

/**
 * Live integration tests for the {@link User} domain class.
 * 
 * @author Francesc Montserrat
 */
@Test(groups = "api", testName = "UserLiveApiTest")
public class UserLiveApiTest extends BaseAbiquoApiLiveApiTest {

   public void testUpdate() {
      String username = env.user.getName();
      env.user.setName("Manolo");
      env.user.update();

      // Recover the updated user
      UserDto updated = env.enterpriseApi.getUser(env.enterprise.unwrap(), env.user.getId());

      assertEquals(updated.getName(), "Manolo");

      env.user.setName(username);
      env.user.update();
   }

   public void testCreateRepeated() {
      User repeated = User.Builder.fromUser(env.user).build();

      try {
         repeated.save();
         fail("Should not be able to create users with the same nick");
      } catch (AbiquoException ex) {
         assertHasError(ex, Status.CONFLICT, "USER-4");
      }
   }

   public void testChangeRoleAndUpdate() {
      env.user.setRole(env.anotherRole);
      env.user.update();

      Role role2 = env.enterprise.findUser(UserPredicates.nick(env.user.getNick())).getRole();

      assertEquals(env.anotherRole.getId(), role2.getId());
      assertEquals(role2.getName(), "Another role");

      env.user.setRole(env.role);
      env.user.update();
   }

   public void testListUser() {
      Iterable<User> users = env.enterprise.listUsers();
      assertEquals(Iterables.size(users), 2);

      users = env.enterprise.listUsers(nick(env.user.getNick()));
      assertEquals(Iterables.size(users), 1);

      users = env.enterprise.listUsers(nick(env.user.getName() + "FAIL"));
      assertEquals(Iterables.size(users), 0);
   }

   public void testGetCurrentUser() {
      User user = env.context.getAdministrationService().getCurrentUser();
      assertNotNull(user);
      assertEquals(user.getNick(), env.context.getApiContext().getIdentity());
   }
}
