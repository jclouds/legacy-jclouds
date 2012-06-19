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

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.jclouds.domain.Credentials;
import org.jclouds.logging.Logger;
import static org.jclouds.openstack.keystone.v2_0.config.KeystoneProperties.*;
import org.jclouds.openstack.keystone.v2_0.domain.Access;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class BaseAuthenticator<C> implements Function<Credentials, Access> {

   @Resource
   protected Logger logger = Logger.NULL;

   @Inject(optional = true)
   @Named(TENANT_NAME)
   protected String defaultTenantName;

   @Inject(optional = true)
   @Named(TENANT_ID)
   protected String defaultTenantId;

   @Inject(optional = true)
   @Named(REQUIRES_TENANT)
   protected boolean requiresTenant;

   @PostConstruct
   public void checkPropertiesAreCompatible() {
      checkState(defaultTenantName == null || defaultTenantId == null, "you cannot specify both %s and %s",
            TENANT_NAME, TENANT_ID);
   }

   @Override
   public Access apply(Credentials input) {
      Optional<String> tenantName = Optional.fromNullable(defaultTenantName);
      Optional<String> tenantId = Optional.fromNullable(defaultTenantId);

      String usernameOrAccessKey = input.identity;

      if (!tenantName.isPresent() && input.identity.indexOf(':') != -1) {
         tenantName = Optional.of(input.identity.substring(0, input.identity.indexOf(':')));
         usernameOrAccessKey = input.identity.substring(input.identity.indexOf(':') + 1);
      }
      
      String passwordOrSecretKey = input.credential;

      C creds = createCredentials(usernameOrAccessKey, passwordOrSecretKey);

      Access access;
      if (tenantId.isPresent()) {
         access = authenticateWithTenantId(tenantId, creds);
      } else if (tenantName.isPresent()) {
         access = authenticateWithTenantName(tenantName, creds);
      } else if (!requiresTenant) {
         access = authenticateWithTenantName(tenantName, creds);
      } else {
         throw new IllegalArgumentException(
               String.format(
                     "current configuration is set to [%s]. Unless you set [%s] or [%s], you must prefix your identity with 'tenantName:'",
                     REQUIRES_TENANT, TENANT_NAME, TENANT_ID));
      }
      return access;
   }

   public abstract C createCredentials(String identity, String credential);

   protected abstract Access authenticateWithTenantId(Optional<String> tenantId, C apiAccessKeyCredentials);

   protected abstract Access authenticateWithTenantName(Optional<String> tenantId, C apiAccessKeyCredentials);

}