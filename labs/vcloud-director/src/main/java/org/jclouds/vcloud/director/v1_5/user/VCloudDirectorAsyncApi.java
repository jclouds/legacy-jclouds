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

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.Catalog;
import org.jclouds.vcloud.director.v1_5.domain.Entity;
import org.jclouds.vcloud.director.v1_5.domain.Media;
import org.jclouds.vcloud.director.v1_5.domain.Metadata;
import org.jclouds.vcloud.director.v1_5.domain.Session;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.VApp;
import org.jclouds.vcloud.director.v1_5.domain.VAppTemplate;
import org.jclouds.vcloud.director.v1_5.domain.Vdc;
import org.jclouds.vcloud.director.v1_5.domain.Vm;
import org.jclouds.vcloud.director.v1_5.domain.network.Network;
import org.jclouds.vcloud.director.v1_5.domain.org.Org;
import org.jclouds.vcloud.director.v1_5.features.CatalogAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.MetadataAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.NetworkAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.OrgAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.TaskAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.UploadAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VAppTemplateAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VdcAsyncApi;
import org.jclouds.vcloud.director.v1_5.features.VmAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.inject.Provides;

/**
 * Provides asynchronous access to VCloudDirector via their REST API.
 * 
 * @see VCloudDirectorApi
 * @author Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface VCloudDirectorAsyncApi {

   /**
    * @see VCloudDirectorApi#resolveEntity(String)
    */
   @GET
   @Path("/entity/{id}")
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Entity> resolveEntity(@PathParam("id") String id);

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
   
   /**
    * @return asynchronous access to {@link Metadata} features
    */
   @Delegate
   MetadataAsyncApi getMetadataApi(@EndpointParam(parser = URNToHref.class) String urn);

   @Delegate
   MetadataAsyncApi getMetadataApi(@EndpointParam URI href);
}
