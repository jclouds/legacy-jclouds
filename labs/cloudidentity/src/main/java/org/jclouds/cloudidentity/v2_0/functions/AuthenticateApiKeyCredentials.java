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
package org.jclouds.cloudidentity.v2_0.functions;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.cloudidentity.v2_0.CloudIdentityAuthenticationClient;
import org.jclouds.cloudidentity.v2_0.config.CloudIdentityCredentialTypes;
import org.jclouds.cloudidentity.v2_0.domain.ApiKeyCredentials;
import org.jclouds.openstack.keystone.v2_0.config.CredentialType;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.internal.BaseAuthenticator;

/**
 * 
 * @author Adrian Cole
 * @see <a
 *      href="http://docs.rackspace.com/servers/api/v2/cs-devguide/content/curl_auth.html">docs</a>
 */
@CredentialType(CloudIdentityCredentialTypes.API_KEY_CREDENTIALS)
@Singleton
public class AuthenticateApiKeyCredentials extends BaseAuthenticator<ApiKeyCredentials> {
   protected final CloudIdentityAuthenticationClient client;

   @Inject
   public AuthenticateApiKeyCredentials(CloudIdentityAuthenticationClient client) {
      this.client = client;
   }

   @Override
   protected Access authenticateWithTenantNameOrNull(String tenantId, ApiKeyCredentials apiKeyCredentials) {
      return client.authenticateWithTenantNameAndCredentials(tenantId, apiKeyCredentials);
   }

   @Override
   protected Access authenticateWithTenantId(String tenantId, ApiKeyCredentials apiKeyCredentials) {
      return client.authenticateWithTenantIdAndCredentials(tenantId, apiKeyCredentials);
   }

   @Override
   public ApiKeyCredentials createCredentials(String identity, String credential) {
      return ApiKeyCredentials.createWithUsernameAndApiKey(identity, credential);
   }

   @Override
   public String toString() {
      return "authenticateApiKeyCredentials()";
   }
}