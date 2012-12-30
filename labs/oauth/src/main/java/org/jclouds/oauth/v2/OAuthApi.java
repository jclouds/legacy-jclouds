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
package org.jclouds.oauth.v2;

import org.jclouds.oauth.v2.domain.Token;
import org.jclouds.oauth.v2.domain.TokenRequest;
import org.jclouds.rest.AuthorizationException;

/**
 * Provides synchronous access to OAuth.
 * <p/>
 *
 * @author David Alves
 * @see OAuthAsyncApi
 */
public interface OAuthApi {

   /**
    * Authenticates/Authorizes access to a resource defined in TokenRequest against an OAuth v2
    * authentication/authorization server.
    *
    * @param tokenRequest specified the principal and the required permissions
    * @return a Token object with the token required to access the resource along with its expiration time
    * @throws AuthorizationException if the principal cannot be authenticated or has no permissions for the specifed
    *                                resources.
    */
   public Token authenticate(TokenRequest tokenRequest) throws AuthorizationException;

}
