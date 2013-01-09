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

import java.net.URI;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.AdminVdc;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.org.AdminOrg;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminCatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminNetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminOrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminQueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.AdminVdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.GroupAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.admin.UserAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToAdminHref;
import org.jclouds.vcloud.director.v1_5.user.VCloudDirectorAsyncApi;

/**
 * Provides asynchronous access to VCloudDirector Admin via their REST API.
 * 
 * @see VCloudDirectorAdminApi
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VCloudDirectorAdminAsyncApi extends VCloudDirectorAsyncApi {
   /**
    * @return asynchronous access to admin query features
    */
   @Override
   @Delegate
   AdminQueryAsyncApi getQueryApi();

   
   /**
    * @return asynchronous access to {@link Catalog} admin features
    */
   @Override
   @Delegate
   AdminCatalogAsyncApi getCatalogApi();
   
   /**
    * @return asynchronous access to admin {@link Group} features
    */
   @Delegate
   GroupAsyncApi getGroupApi();
   
   /**
    * @return asynchronous access to {@link AdminOrg} features
    */
   @Override
   @Delegate
   AdminOrgAsyncApi getOrgApi();
   
   /**
    * @return asynchronous access to {@link User} features
    */
   @Delegate
   UserAsyncApi getUserApi();
   
   /**
    * @return asynchronous access to {@link AdminVdc} features
    */
   @Override
   @Delegate
   AdminVdcAsyncApi getVdcApi();
   
   /**
    * @return asynchronous access to admin {@link Network} features
    */
   @Override
   @Delegate
   AdminNetworkAsyncApi getNetworkApi();
   
   /**
    * @return asynchronous access to {@link Metadata} features
    */
   @Override
   @Delegate
   MetadataAsyncApi getMetadataApi(@EndpointParam(parser = URNToAdminHref.class) String urn);

   @Override
   @Delegate
   MetadataAsyncApi getMetadataApi(@EndpointParam URI href);
}
