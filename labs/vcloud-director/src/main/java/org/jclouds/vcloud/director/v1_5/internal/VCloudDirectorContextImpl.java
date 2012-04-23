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

import javax.inject.Inject;
import javax.inject.Singleton;

import org.jclouds.lifecycle.Closer;
import org.jclouds.providers.ProviderMetadata;
import org.jclouds.rest.RestContext;
import org.jclouds.rest.Utils;
import org.jclouds.rest.annotations.Identity;
import org.jclouds.rest.internal.RestContextImpl;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorContext;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminAsyncClient;
import org.jclouds.vcloud.director.v1_5.admin.VCloudDirectorAdminClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

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
   VCloudDirectorContextImpl(ProviderMetadata providerMetadata, @Identity String identity, Utils utils, Closer closer,
            Injector injector, RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> adminContext) {
      super(providerMetadata, identity, utils, closer, injector, TypeLiteral.get(VCloudDirectorClient.class),
               TypeLiteral.get(VCloudDirectorAsyncClient.class));
      this.adminContext = adminContext;
   }
   
   @Override
   public RestContext<VCloudDirectorAdminClient, VCloudDirectorAdminAsyncClient> getAdminContext() {
      return adminContext;
   }
}
