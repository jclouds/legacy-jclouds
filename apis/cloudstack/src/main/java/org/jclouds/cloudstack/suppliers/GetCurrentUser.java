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
package org.jclouds.cloudstack.suppliers;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.NoSuchElementException;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.jclouds.cloudstack.CloudStackClient;
import org.jclouds.cloudstack.domain.User;
import org.jclouds.cloudstack.predicates.UserPredicates;
import org.jclouds.logging.Logger;
import org.jclouds.rest.annotations.Identity;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class GetCurrentUser implements Supplier<User> {

   @Resource
   protected Logger logger = Logger.NULL;

   private final CloudStackClient client;
   private final String identity;

   @Inject
   public GetCurrentUser(CloudStackClient client, @Identity String identity) {
      this.client = checkNotNull(client, "client");
      this.identity = checkNotNull(identity, "identity");
   }

   @Override
   public User get() {
      Iterable<User> users = Iterables.concat(client.getAccountClient().listAccounts());
      Predicate<User> apiKeyMatches = UserPredicates.apiKeyEquals(identity);
      User currentUser = null;
      try {
         currentUser = Iterables.find(users, apiKeyMatches);
      } catch (NoSuchElementException e) {
         throw new NoSuchElementException(String.format("none of the following users match %s: %s", apiKeyMatches,
               users));
      }

      return currentUser;
   }
}
