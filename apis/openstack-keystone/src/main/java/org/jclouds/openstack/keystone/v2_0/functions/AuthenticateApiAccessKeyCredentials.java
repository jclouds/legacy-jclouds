/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.keystone.v2_0.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.domain.ApiAccessKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.functions.internal.BaseAuthenticator;

import com.google.common.base.Optional;

@CredentialType(CredentialTypes.API_ACCESS_KEY_CREDENTIALS)
@Singleton
public class AuthenticateApiAccessKeyCredentials extends BaseAuthenticator<ApiAccessKeyCredentials> {
   protected final AuthenticationApi api;

   @Inject
   public AuthenticateApiAccessKeyCredentials(AuthenticationApi api) {
      this.api = api;
   }

   @Override
   protected Access authenticateWithTenantName(Optional<String> tenantName, ApiAccessKeyCredentials apiAccessKeyCredentials) {
      return api.authenticateWithTenantNameAndCredentials(tenantName.orNull(), apiAccessKeyCredentials);
   }

   @Override
   protected Access authenticateWithTenantId(Optional<String> tenantId, ApiAccessKeyCredentials apiAccessKeyCredentials) {
      return api.authenticateWithTenantIdAndCredentials(tenantId.orNull(), apiAccessKeyCredentials);
   }

   @Override
   public ApiAccessKeyCredentials createCredentials(String identity, String credential) {
      return ApiAccessKeyCredentials.createWithAccessKeyAndSecretKey(identity, credential);
   }

   @Override
   public String toString() {
      return "authenticateApiAccessKeyCredentials()";
   }
}
