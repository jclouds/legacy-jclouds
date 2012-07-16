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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.internal.BaseIAMApiLiveTest;
import org.jclouds.iam.options.ListUsersOptions;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

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
      checkNotNull(user.getArn(), "Arn cannot be null for a User.");
      checkNotNull(user.getId(), "Id cannot be null for a User.");
      checkNotNull(user.getName(), "While Name can be null for a User, its Optional wrapper cannot.");
      checkNotNull(user.getPath(), "While Path can be null for a User, its Optional wrapper cannot.");
      checkNotNull(user.getCreateDate(), "CreateDate cannot be null for a User.");
   }

   @Test
   protected void testListUsers() {
      IterableWithMarker<User> response = api().list().get(0);
      
      for (User user : response) {
         checkUser(user);
      }
      
      if (Iterables.size(response) > 0) {
         User user = response.iterator().next();
         Assert.assertEquals(api().get(user.getName().get()), user);
      }

      // Test with a Marker, even if it's null
      response = api().list(ListUsersOptions.Builder.afterMarker(response.nextMarker().orNull()));
      for (User user : response) {
         checkUser(user);
      }
   }

   protected UserApi api() {
      return context.getApi().getUserApi();
   }
}
