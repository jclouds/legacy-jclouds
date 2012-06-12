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
package org.jclouds.openstack.keystone.v2_0.functions.internal;

import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import org.jclouds.openstack.keystone.v2_0.domain.Access;

import com.google.common.base.Function;

public abstract class BaseAuthenticator<C> implements Function<Credentials, Access> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Override
   public Access apply(Credentials input) {
      String tenantId = null;
      String usernameOrAccessKey = input.identity;
      if (input.identity.indexOf(':') == -1) {
         logger.debug("Identity %s does not match format tenantName:accessKey", input.identity);
      } else {
         tenantId = input.identity.substring(0, input.identity.indexOf(':'));
         usernameOrAccessKey = input.identity.substring(input.identity.indexOf(':') + 1);
      }
      String passwordOrSecretKey = input.credential;

      C creds = createCredentials(usernameOrAccessKey, passwordOrSecretKey);
      Access access;
      if (tenantId != null && tenantId.matches("^[0-9]+$")) {
         access = authenticateWithTenantId(tenantId, creds);
      } else {
         access = authenticateWithTenantNameOrNull(tenantId, creds);
      }
      return access;
   }

   public abstract C createCredentials(String identity, String credential);

   protected abstract Access authenticateWithTenantId(String tenantId, C apiAccessKeyCredentials);

   protected abstract Access authenticateWithTenantNameOrNull(String tenantId, C apiAccessKeyCredentials);

}