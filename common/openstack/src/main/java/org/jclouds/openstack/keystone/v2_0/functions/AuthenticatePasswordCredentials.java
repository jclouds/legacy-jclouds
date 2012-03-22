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
package org.jclouds.openstack.keystone.v2_0.functions;
import javax.inject.Inject;

import org.jclouds.domain.Credentials;
import org.jclouds.openstack.keystone.v2_0.ServiceClient;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.PasswordCredentials;
import org.jclouds.rest.AuthorizationException;

import com.google.common.base.Function;

public class AuthenticatePasswordCredentials implements Function<Credentials, Access> {
   private final ServiceClient client;

   @Inject
   public AuthenticatePasswordCredentials(ServiceClient client) {
      this.client = client;
   }

   @Override
   public Access apply(Credentials input) {
      if (input.identity.indexOf(':') == -1) {
         throw new AuthorizationException(String.format("Identity %s does not match format tenantId:username",
                  input.identity), null);
      }
      
      String tenantId = input.identity.substring(0, input.identity.indexOf(':'));
      String usernameOrAccessKey = input.identity.substring(input.identity.indexOf(':') + 1);
      String passwordOrSecretKey = input.credential;

      PasswordCredentials passwordCredentials = PasswordCredentials.createWithUsernameAndPassword(usernameOrAccessKey,
               passwordOrSecretKey);
      return client.authenticateTenantWithCredentials(tenantId, passwordCredentials);
   }

   @Override
   public String toString() {
      return "authenticatePasswordCredentials()";
   }
}