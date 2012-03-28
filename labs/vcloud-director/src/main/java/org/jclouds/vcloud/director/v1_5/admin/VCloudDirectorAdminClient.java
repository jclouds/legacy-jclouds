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
package org.jclouds.vcloud.director.v1_5.admin;

import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Network;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcClient;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupClient;
import org.jclouds.vcloud.director.v1_5.features.admin.UserClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorClient;

/**
 * Provides synchronous access to VCloudDirector Admin.
 * 
 * @see VCloudDirectorAsyncClient
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface VCloudDirectorAdminClient extends VCloudDirectorClient {
   /**
    * @return asynchronous access to admin query features
    */
   @Override
   @Delegate
   AdminQueryClient getQueryClient();
   
   /**
    * @return synchronous access to {@link Catalog} admin features
    */
   @Override
   @Delegate
   AdminCatalogClient getCatalogClient();
   
   /**
    * @return synchronous access to admin {@link Group} features
    */
   @Delegate
   GroupClient getGroupClient();

   /**
    * @return synchronous access to {@link AdminOrg} features
    */
   @Override
   @Delegate
   AdminOrgClient getOrgClient();
   
   /**
    * @return synchronous access to {@link User} features
    */
   @Delegate
   UserClient getUserClient();
   
   /**
    * @return synchronous access to {@link AdminVdc} features
    */
   @Delegate
   AdminVdcClient getVdcClient();
   
   /**
    * @return synchronous access to admin {@link Network} features
    */
   @Override
   @Delegate
   AdminNetworkClient getNetworkClient();
}
