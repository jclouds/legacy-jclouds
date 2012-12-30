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
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcApi;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupApi;
import org.jclouds.vcloud.director.v1_5.features.admin.UserApi;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorApi;

/**
 * Provides synchronous access to VCloudDirector Admin.
 * 
 * @see VCloudDirectorAsyncApi
 * @author Adrian Cole
 */
public interface VCloudDirectorAdminApi extends VCloudDirectorApi {
   /**
    * @return asynchronous access to admin query features
    */
   @Override
   @Delegate
   AdminQueryApi getQueryApi();
   
   /**
    * @return synchronous access to {@link Catalog} admin features
    */
   @Override
   @Delegate
   AdminCatalogApi getCatalogApi();
   
   /**
    * @return synchronous access to admin {@link Group} features
    */
   @Delegate
   GroupApi getGroupApi();

   /**
    * @return synchronous access to {@link AdminOrg} features
    */
   @Override
   @Delegate
   AdminOrgApi getOrgApi();
   
   /**
    * @return synchronous access to {@link User} features
    */
   @Delegate
   UserApi getUserApi();
   
   /**
    * @return synchronous access to {@link AdminVdc} features
    */
   @Override
   @Delegate
   AdminVdcApi getVdcApi();
   
   /**
    * @return synchronous access to admin {@link Network} features
    */
   @Override
   @Delegate
   AdminNetworkApi getNetworkApi();
}
