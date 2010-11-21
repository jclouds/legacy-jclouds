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

package org.jclouds.elastichosts;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.elastichosts.binders.BindCreateDriveRequestToPlainTextString;
import org.jclouds.elastichosts.binders.BindDriveDataToPlainTextString;
import org.jclouds.elastichosts.binders.BindReadDriveOptionsToPath;
import org.jclouds.elastichosts.domain.CreateDriveRequest;
import org.jclouds.elastichosts.domain.DriveData;
import org.jclouds.elastichosts.domain.DriveInfo;
import org.jclouds.elastichosts.domain.ImageConversionType;
import org.jclouds.elastichosts.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.elastichosts.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.elastichosts.functions.ReturnPayload;
import org.jclouds.elastichosts.functions.SplitNewlines;
import org.jclouds.elastichosts.options.ReadDriveOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.io.Payload;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to ElasticHosts via their REST API.
 * <p/>
 * 
 * @see ElasticHostsClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface ElasticHostsAsyncClient {

   /**
    * @see ElasticHostsClient#listDrives()
    */
   @GET
   @Path("/drives/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listDrives();

   /**
    * @see ElasticHostsClient#listStandardDrives()
    */
   @GET
   @Path("/drives/standard/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardDrives();

   /**
    * @see ElasticHostsClient#listDriveInfo()
    */
   @GET
   @Path("/drives/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class)
   ListenableFuture<Set<DriveInfo>> listDriveInfo();

   /**
    * @see ElasticHostsClient#getDriveInfo
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/info")
   ListenableFuture<DriveInfo> getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * @see ElasticHostsClient#createDrive
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/create")
   ListenableFuture<DriveInfo> createDrive(
         @BinderParam(BindCreateDriveRequestToPlainTextString.class) CreateDriveRequest createDrive);

   /**
    * @see ElasticHostsClient#setDriveData
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   ListenableFuture<DriveInfo> setDriveData(@PathParam("uuid") String uuid,
         @BinderParam(BindDriveDataToPlainTextString.class) DriveData createDrive);

   /**
    * @see ElasticHostsClient#destroyDrive
    */
   @POST
   @Path("/drives/{uuid}/destroy")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyDrive(@PathParam("uuid") String uuid);

   /**
    * @see ElasticHostsClient#imageDrive(String,String)
    */
   @POST
   @Path("/drives/{destination}/image/{source}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> imageDrive(@PathParam("source") String source, @PathParam("destination") String destination);

   /**
    * @see ElasticHostsClient#imageDrive(String,String,ImageConversionType)
    */
   @POST
   @Path("/drives/{destination}/image/{source}/{conversion}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> imageDrive(@PathParam("source") String source, @PathParam("destination") String destination,
         @PathParam("conversion") ImageConversionType conversionType);

   /**
    * @see ElasticHostsClient#readDrive(String)
    */
   @GET
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/read")
   @ResponseParser(ReturnPayload.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Payload> readDrive(@PathParam("uuid") String uuid);

   /**
    * @see ElasticHostsClient#readDrive(String,ReadDriveOptions)
    */
   @POST
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/read")
   @ResponseParser(ReturnPayload.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Payload> readDrive(@PathParam("uuid") String uuid,
         @BinderParam(BindReadDriveOptionsToPath.class) ReadDriveOptions options);

   /**
    * @see ElasticHostsClient#writeDrive(String, Payload)
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> writeDrive(@PathParam("uuid") String uuid, Payload content);

   /**
    * @see ElasticHostsClient#writeDrive(String, Payload, long)
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write/{offset}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> writeDrive(@PathParam("uuid") String uuid, Payload content, @PathParam("offset") long offset);
}
