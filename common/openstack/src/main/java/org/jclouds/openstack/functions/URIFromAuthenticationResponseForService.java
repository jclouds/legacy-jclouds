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
package org.jclouds.openstack.functions;

import java.net.URI;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.openstack.domain.AuthenticationResponse;

import com.google.common.base.Supplier;
import com.google.inject.assistedinject.Assisted;

@Singleton
public class URIFromAuthenticationResponseForService implements Supplier<URI> {
   public static interface Factory {
      URIFromAuthenticationResponseForService create(String service);
   }

   private final Supplier<AuthenticationResponse> auth;
   private final String service;

   @Inject
   public URIFromAuthenticationResponseForService(Supplier<AuthenticationResponse> auth, @Assisted String service) {
      this.auth = auth;
      this.service = service;
   }

   @Override
   public URI get() {
      return auth.get().getServices().get(service);
   }

   @Override
   public String toString() {
      return "getURIForService(" + service + ")";
   }
}
