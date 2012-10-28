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
package org.jclouds.oauth.domain;

import com.google.inject.ImplementedBy;
import org.jclouds.http.HttpRequest;
import org.jclouds.oauth.json.JWTTokenRequestFormat;

import java.util.Set;

/**
 * Formats a TokenRequest for a specific format (e.g. JWT token)
 *
 * @author David Alves
 */
@ImplementedBy(JWTTokenRequestFormat.class)
public interface TokenRequestFormat {

   public <R extends HttpRequest> R formatRequest(R httpRequest, TokenRequest tokenRequest);

   public String getTypeName();

   public Set<String> requiredClaimSet();
}
