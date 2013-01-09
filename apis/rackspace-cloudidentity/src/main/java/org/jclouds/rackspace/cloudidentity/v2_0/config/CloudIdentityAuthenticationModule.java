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
package org.jclouds.rackspace.cloudidentity.v2_0.config;

import static org.jclouds.rest.config.BinderUtils.bindHttpApi;

import java.util.Map;

import org.jclouds.domain.Credentials;
import org.jclouds.openstack.keystone.v2_0.AuthenticationAsyncApi;
import org.jclouds.openstack.keystone.v2_0.AuthenticationApi;
import org.jclouds.openstack.keystone.v2_0.config.CredentialTypes;
import org.jclouds.openstack.keystone.v2_0.config.KeystoneAuthenticationModule;
import org.jclouds.openstack.keystone.v2_0.domain.Access;
import org.jclouds.openstack.keystone.v2_0.functions.AuthenticatePasswordCredentials;
import org.jclouds.rackspace.cloudidentity.v2_0.CloudIdentityAuthenticationAsyncApi;
import org.jclouds.rackspace.cloudidentity.v2_0.CloudIdentityAuthenticationApi;
import org.jclouds.rackspace.cloudidentity.v2_0.functions.AuthenticateApiKeyCredentials;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Injector;
import com.google.inject.Scopes;

/**
 * 
 * @author Adrian Cole
 */
public class CloudIdentityAuthenticationModule extends KeystoneAuthenticationModule {

   @Override
   protected void bindAuthenticationApi() {
      // AuthenticationApi is used directly for filters and retry handlers, so let's bind it explicitly
      bindHttpApi(binder(), CloudIdentityAuthenticationApi.class,
            CloudIdentityAuthenticationAsyncApi.class);
      bind(AuthenticationApi.class).to(CloudIdentityAuthenticationApi.class).in(Scopes.SINGLETON);
      bind(AuthenticationAsyncApi.class).to(CloudIdentityAuthenticationAsyncApi.class).in(Scopes.SINGLETON);
   }

   @Override
   protected Map<String, Function<Credentials, Access>> authenticationMethods(Injector i) {
      Builder<Function<Credentials, Access>> fns = ImmutableSet.<Function<Credentials, Access>> builder();
      fns.add(i.getInstance(AuthenticatePasswordCredentials.class));
      fns.add(i.getInstance(AuthenticateApiKeyCredentials.class));
      return CredentialTypes.indexByCredentialType(fns.build());
   }

}
