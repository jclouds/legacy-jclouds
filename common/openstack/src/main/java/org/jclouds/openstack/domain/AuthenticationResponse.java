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
package org.jclouds.openstack.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.net.URI;
import java.util.Map;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;

public class AuthenticationResponse {
   private final String authToken;
   private final Map<String, URI> services;

   public AuthenticationResponse(String authToken, Map<String, URI> services) {
      this.authToken = checkNotNull(authToken, "authToken");
      this.services = ImmutableMap.copyOf(checkNotNull(services, "services"));
   }

   public Map<String, URI> getServices() {
      return services;
   }
   
   public String getAuthToken() {
      return authToken;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(authToken, services);
   }

   @Override
   public boolean equals(Object that) {
      if (that == null)
         return false;
      return Objects.equal(this.toString(), that.toString());
   }

   @Override
   public String toString() {
      return Objects.toStringHelper(this).add("authToken", authToken).add("services", services).toString();
   }

}
