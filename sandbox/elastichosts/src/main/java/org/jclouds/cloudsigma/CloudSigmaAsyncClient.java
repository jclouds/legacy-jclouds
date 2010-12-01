/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.cloudsigma;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.elastichosts.CommonElasticHostsAsyncClient;
import org.jclouds.elastichosts.ElasticHostsClient;
import org.jclouds.elastichosts.binders.BindCreateDriveRequestToPlainTextString;
import org.jclouds.elastichosts.binders.BindDriveDataToPlainTextString;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveData;
import org.jclouds.elastichosts.functions.SplitNewlines;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to CloudSigma via their REST API.
 * <p/>
 * 
 * @see ElasticHostsClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface CloudSigmaAsyncClient extends CommonElasticHostsAsyncClient {

   /**
    * @see ElasticHostsClient#listStandardDrives()
    */
   @GET
   @Path("/drives/standard/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardDrives();

   /**
    * @see ElasticHostsClient#listStandardCds()
    */
   @GET
   @Path("/drives/standard/cd/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardCds();

   /**
    * @see ElasticHostsClient#listStandardImages()
    */
   @GET
   @Path("/drives/standard/img/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardImages();

   /**
    * @see ElasticHostsClient#listDriveInfo()
    */
   @Override
   @GET
   @Path("/drives/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class)
   ListenableFuture<Set<? extends org.jclouds.elastichosts.domain.DriveInfo>> listDriveInfo();

   /**
    * @see ElasticHostsClient#getDriveInfo
    */
   @Override
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/info")
   ListenableFuture<? extends DriveInfo> getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * @see ElasticHostsClient#createDrive
    */
   @Override
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/create")
   ListenableFuture<? extends DriveInfo> createDrive(
         @BinderParam(BindCreateDriveRequestToPlainTextString.class) CreateDriveRequest createDrive);

   /**
    * @see ElasticHostsClient#setDriveData
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   ListenableFuture<? extends DriveInfo> setDriveData(@PathParam("uuid") String uuid,
         @BinderParam(BindDriveDataToPlainTextString.class) DriveData createDrive);
}
