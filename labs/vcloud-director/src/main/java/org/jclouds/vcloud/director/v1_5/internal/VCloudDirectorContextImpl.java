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
package org.jclouds.vcloud.director.v1_5.internal;

import java.net.URI;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.domain.Credentials;
import org.jclouds.lifecycle.Closer;
import org.jclouds.location.Iso3166;
import org.jclouds.location.Provider;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.ApiVersion;
import org.jclouds.rest.annotations.BuildVersion;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorContext;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncClient;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

import com.google.common.base.Supplier;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author danikov
 */
@Singleton
public class VCloudDirectorContextImpl extends RestContextImpl<VCloudDirectorClient, VCloudDirectorAsyncClient> implements
      VCloudDirectorContext {
   private final RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext;

   @Inject
   VCloudDirectorContextImpl(Closer closer, Map<String, Credentials> credentialStore, Utils utils, Injector injector,
         TypeLiteral<VCloudDirectorClient> syncApi, TypeLiteral<VCloudDirectorAsyncClient> asyncApi, 
         @Provider Supplier<URI> endpoint, @Provider String provider,
         @Identity String identity, @ApiVersion String apiVersion, @BuildVersion String buildVersion,
         @Iso3166 Set<String> iso3166Codes,
         RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext) {
      super(closer, credentialStore, utils, injector, syncApi, asyncApi, endpoint, provider, 
            identity, apiVersion, buildVersion, iso3166Codes);
      this.adminContext = adminContext;
   }
   
   @Override
   public RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> getAdminContext() {
      return adminContext;
   }
}
