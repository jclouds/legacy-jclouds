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

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.AdminOrg;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Network;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.admin.UserAsyncClient;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorUserAsyncClient;

/**
 * Provides asynchronous access to VCloudDirector Admin via their REST API.
 * 
 * @see VCloudDirectorAdminClient
 * @author danikov
 */
public interface VCloudDirectorAdminAsyncClient extends VCloudDirectorUserAsyncClient {
   /**
    * @return asynchronous access to admin query features
    */
   @Override
   @Delegate
   AdminQueryAsyncClient getQueryClient();

   
   /**
    * @return asynchronous access to {@link Catalog} admin features
    */
   @Override
   @Delegate
   AdminCatalogAsyncClient getCatalogClient();
   
   /**
    * @return asynchronous access to admin {@link Group} features
    */
   @Delegate
   GroupAsyncClient getGroupClient();
   
   /**
    * @return asynchronous access to {@link AdminOrg} features
    */
   @Override
   @Delegate
   AdminOrgAsyncClient getOrgClient();
   
   /**
    * @return asynchronous access to {@link User} features
    */
   @Delegate
   UserAsyncClient getUserClient();
   
   /**
    * @return asynchronous access to {@link AdminVdc} features
    */
   @Override
   @Delegate
   AdminVdcAsyncClient getVdcClient();
   
   /**
    * @return asynchronous access to admin {@link Network} features
    */
   @Override
   @Delegate
   AdminNetworkAsyncClient getNetworkClient();
}
