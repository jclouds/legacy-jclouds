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
package org.jclouds.vcloud.director.v1_5.user;

import org.jclouds.rest.annotations.Delegate;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.TaskAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.UploadAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VmAsyncApi;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to VCloudDirector via their REST API.
 * 
 * @see VCloudDirectorApi
 * @author Adrian Cole
 */
public interface VCloudDirectorAsyncApi {
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
   QueryAsyncApi getQueryApi();

   /**
    * @return asynchronous access to {@link Org} features
    */
   @Delegate
   OrgAsyncApi getOrgApi();
   
   /**
    * @return asynchronous access to {@link Task} features
    */
   @Delegate
   TaskAsyncApi getTaskApi();
   
   /**
    * @return asynchronous access to {@link Network} features
    */
   @Delegate
   NetworkAsyncApi getNetworkApi();
   
   /**
    * @return asynchronous access to {@link Catalog} features
    */
   @Delegate
   CatalogAsyncApi getCatalogApi();
   
   /**
    * @return asynchronous access to {@link Media} features
    */
   @Delegate
   CatalogAsyncApi getMediaApi();
   
   /**
    * @return asynchronous access to {@link Vdc} features
    */
   @Delegate
   VdcAsyncApi getVdcApi();

   /**
    * @return asynchronous access to Upload features
    */
   @Delegate
   UploadAsyncApi getUploadApi();
   
   /**
    * @return asynchronous access to {@link VApp} features
    */
   @Delegate
   VAppAsyncApi getVAppApi();

   /**
    * @return asynchronous access to {@link VAppTemplate} features
    */
   @Delegate
   VAppTemplateAsyncApi getVAppTemplateApi();

   /**
    * @return asynchronous access to {@link Vm} features
    */
   @Delegate
   VmAsyncApi getVmApi();
}
