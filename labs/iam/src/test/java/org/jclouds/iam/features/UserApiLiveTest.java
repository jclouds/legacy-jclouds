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

import org.jclouds.iam.domain.User;
import org.jclouds.iam.internal.BaseIAMApiLiveTest;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "live", testName = "UserApiLiveTest")
public class UserApiLiveTest extends BaseIAMApiLiveTest {

   @Test
   protected void testGetCurrentUser() {
      User user = api().getCurrent();
      checkUser(user);
   }

   private void checkUser(User user) {
      checkNotNull(user.getArn(), "Arn cannot be null for User %s", user);
      checkNotNull(user.getId(), "Id cannot be null for User %s", user);
      checkNotNull(user.getName(), "While Name can be null for a User, its Optional wrapper cannot; user %s", user);
      checkNotNull(user.getPath(), "While Path can be null for a User, its Optional wrapper cannot; user %s", user);
      checkNotNull(user.getCreateDate(), "CreateDate cannot be null for a User User %s", user);
   }

   @Test
   protected void testListUsers() {
      ImmutableList<User> users = api().list().concat().toImmutableList();
      getAnonymousLogger().info("users: " + users.size());

      for (User user : users) {
         checkUser(user);
         assertEquals(api().get(user.getId()), user);
         if (user.getPath().isPresent())
            assertEquals(api().listPathPrefix(user.getPath().get()).toImmutableSet(), ImmutableSet.of(user));
      }
   }

   protected UserApi api() {
      return context.getApi().getUserApi();
   }
}
