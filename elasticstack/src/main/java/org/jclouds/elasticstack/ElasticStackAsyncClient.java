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

package org.jclouds.elasticstack;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.elasticstack.binders.BindDriveDataToPlainTextString;
import org.jclouds.elasticstack.binders.BindDriveToPlainTextString;
import org.jclouds.elasticstack.binders.BindServerToPlainTextString;
import org.jclouds.elasticstack.domain.Drive;
import org.jclouds.elasticstack.domain.DriveData;
import org.jclouds.elasticstack.domain.DriveInfo;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.elasticstack.functions.KeyValuesDelimitedByBlankLinesToServerInfo;
import org.jclouds.elasticstack.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.elasticstack.functions.ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet;
import org.jclouds.elasticstack.functions.ReturnPayload;
import org.jclouds.elasticstack.functions.SplitNewlines;
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
 * Provides asynchronous access to elasticstack via their REST API.
 * <p/>
 * 
 * @see ElasticStackClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface ElasticStackAsyncClient {

   /**
    * @see ElasticStackClient#listServers()
    */
   @GET
   @Path("/servers/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listServers();

   /**
    * @see ElasticStackClient#listServerInfo()
    */
   @GET
   @Path("/servers/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class)
   ListenableFuture<Set<? extends ServerInfo>> listServerInfo();

   /**
    * @see ElasticStackClient#getServerInfo
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/info")
   ListenableFuture<? extends ServerInfo> getServerInfo(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#createServer
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create/stopped")
   ListenableFuture<? extends ServerInfo> createServer(
         @BinderParam(BindServerToPlainTextString.class) Server createServer);

   /**
    * @see ElasticStackClient#setServerConfiguration
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/set")
   ListenableFuture<? extends ServerInfo> setServerConfiguration(@PathParam("uuid") String uuid,
         @BinderParam(BindServerToPlainTextString.class) Server setServer);

   /**
    * @see ElasticStackClient#destroyServer
    */
   @POST
   @Path("/servers/{uuid}/destroy")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyServer(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#startServer
    */
   @POST
   @Path("/servers/{uuid}/start")
   ListenableFuture<Void> startServer(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#stopServer
    */
   @POST
   @Path("/servers/{uuid}/stop")
   ListenableFuture<Void> stopServer(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#shutdownServer
    */
   @POST
   @Path("/servers/{uuid}/shutdown")
   ListenableFuture<Void> shutdownServer(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#resetServer
    */
   @POST
   @Path("/servers/{uuid}/reset")
   ListenableFuture<Void> resetServer(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#listDrives()
    */
   @GET
   @Path("/drives/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listDrives();

   /**
    * @see ElasticStackClient#listDriveInfo()
    */
   @GET
   @Path("/drives/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class)
   ListenableFuture<Set<? extends DriveInfo>> listDriveInfo();

   /**
    * @see ElasticStackClient#getDriveInfo
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/info")
   ListenableFuture<? extends DriveInfo> getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#createDrive
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/create")
   ListenableFuture<? extends DriveInfo> createDrive(@BinderParam(BindDriveToPlainTextString.class) Drive createDrive);

   /**
    * @see ElasticStackClient#setDriveData
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   ListenableFuture<? extends DriveInfo> setDriveData(@PathParam("uuid") String uuid,
         @BinderParam(BindDriveDataToPlainTextString.class) DriveData setDrive);

   /**
    * @see ElasticStackClient#destroyDrive
    */
   @POST
   @Path("/drives/{uuid}/destroy")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyDrive(@PathParam("uuid") String uuid);

   /**
    * @see ElasticStackClient#createAndStartServer
    */
   @POST
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create")
   ListenableFuture<? extends ServerInfo> createAndStartServer(
         @BinderParam(BindServerToPlainTextString.class) Server createServer);

   /**
    * @see ElasticStackClient#imageDrive(String,String)
    */
   @POST
   @Path("/drives/{destination}/image/{source}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> imageDrive(@PathParam("source") String source, @PathParam("destination") String destination);

   /**
    * @see ElasticStackClient#imageDrive(String,String,ImageConversionType)
    */
   @POST
   @Path("/drives/{destination}/image/{source}/{conversion}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> imageDrive(@PathParam("source") String source, @PathParam("destination") String destination,
         @PathParam("conversion") ImageConversionType conversionType);

   /**
    * @see ElasticStackClient#readDrive
    */
   @POST
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/read/{offset}/{size}")
   @ResponseParser(ReturnPayload.class)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Payload> readDrive(@PathParam("uuid") String uuid, @PathParam("offset") long offset,
         @PathParam("size") long size);

   /**
    * @see ElasticStackClient#writeDrive(String, Payload)
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> writeDrive(@PathParam("uuid") String uuid, Payload content);

   /**
    * @see ElasticStackClient#writeDrive(String, Payload, long)
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write/{offset}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> writeDrive(@PathParam("uuid") String uuid, Payload content, @PathParam("offset") long offset);
}
