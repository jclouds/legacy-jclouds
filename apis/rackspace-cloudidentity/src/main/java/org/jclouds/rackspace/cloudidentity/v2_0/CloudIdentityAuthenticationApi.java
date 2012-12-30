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
package org.jclouds.rackspace.cloudidentity.v2_0;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.rackspace.cloudidentity.v2_0.domain.ApiKeyCredentials;

/**
 * Provides synchronous access to the KeyStone Service API.
 * <p/>
 * 
 * @see AuthenticationAsyncApi
 * @see <a href="http://docs.openstack.org/api/openstack-identity-service/2.0/content/Service_API_Api_Operations.html"
 *      />
 * @author Adrian Cole
 */
public interface CloudIdentityAuthenticationApi extends AuthenticationApi {

   /**
    * Authenticate to generate a token.
    * 
    * @return access with token
    */
   Access authenticateWithTenantNameAndCredentials(@Nullable String tenantId, ApiKeyCredentials apiKeyCredentials);

   /**
    * Authenticate to generate a token.
    * 
    * @return access with token
    */
   Access authenticateWithTenantIdAndCredentials(@Nullable String tenantId, ApiKeyCredentials apiKeyCredentials);

}
