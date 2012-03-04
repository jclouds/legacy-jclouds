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
package org.jclouds.vcloud.director.v1_5;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Org;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.ovf.Network;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.TaskAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncClient;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncClient;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to VCloudDirector via their REST API.
 * 
 * @see VCloudDirectorClient
 * @author Adrian Cole
 */
public interface VCloudDirectorAsyncClient {
   /**
    * 
    * @return the current login session
    */
   @Provides
   Session getCurrentSession();

   /**
    * @return asynchronous access to query features
    */
   @Delegate
   QueryAsyncClient getQueryClient();

   /**
    * @return asynchronous access to {@link Org} features
    */
   @Delegate
   OrgAsyncClient getOrgClient();
   
   /**
    * @return asynchronous access to {@link Task} features
    */
   @Delegate
   TaskAsyncClient getTaskClient();
   
   /**
    * @return asynchronous access to {@link Network} features
    */
   @Delegate
   NetworkAsyncClient getNetworkClient();
   
   /**
    * @return asynchronous access to {@link Catalog} features
    */
   @Delegate
   CatalogAsyncClient getCatalogClient();
   
   /**
    * @return asynchronous access to {@link Media} features
    */
   @Delegate
   CatalogAsyncClient getMediaClient();
   
   /**
    * @return asynchronous access to {@link Vdc} features
    */
   @Delegate
   VdcAsyncClient getVdcClient();

   /**
    * @return asynchronous access to {@link org.jclouds.vcloud.director.v1_5.domain.VAppTemplate} features
    */
   @Delegate
   VAppTemplateAsyncClient getVAppTemplateClient();
   
}
