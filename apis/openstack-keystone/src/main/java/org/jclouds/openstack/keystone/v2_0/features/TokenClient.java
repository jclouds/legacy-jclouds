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
package org.jclouds.openstack.keystone.v2_0.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.domain.Endpoint;
import org.jclouds.openstack.keystone.v2_0.domain.Token;
import org.jclouds.openstack.keystone.v2_0.domain.User;

import com.google.common.annotations.Beta;

/**
 * Provides synchronous access to the KeyStone Admin API.
 * <p/>
 *
 * @author Adam Lowe
 * @see TokenAsyncClient
 * @see <a href=
 *       "http://docs.openstack.org/api/openstack-identity-service/2.0/content/Token_Operations.html"
 *      />
 */
@Beta
@Timeout(duration = 30, timeUnit = TimeUnit.SECONDS)
public interface TokenClient {


   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   Token get(String token);
   
   /**
    * Validate a token and, if it is valid, return access information regarding the tenant (though not the service catalog)/
    *
    * @return the requested information
    */
   User getUserOfToken(String token);
   
   /**
    * Validate a token. This is a high-performance variant of the #getToken() call that does not return any further
    * information.
    *
    * @return true if the token is valid
    */
   boolean isValid(String token);

   /**
    * List all endpoints for a token
    * <p/>
    * NOTE: currently not working in openstack ( https://bugs.launchpad.net/keystone/+bug/988672 )
    *
    * @return the set of endpoints
    */
   Set<Endpoint> listEndpointsForToken(String token);

}