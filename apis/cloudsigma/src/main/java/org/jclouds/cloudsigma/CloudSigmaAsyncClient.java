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
package org.jclouds.cloudsigma;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudsigma.binders.BindCloneDriveOptionsToPlainTextString;
import org.jclouds.cloudsigma.binders.BindDriveDataToPlainTextString;
import org.jclouds.cloudsigma.binders.BindDriveToPlainTextString;
import org.jclouds.cloudsigma.binders.BindServerToPlainTextString;
import org.jclouds.cloudsigma.domain.Drive;
import org.jclouds.cloudsigma.domain.DriveData;
import org.jclouds.cloudsigma.domain.DriveInfo;
import org.jclouds.cloudsigma.domain.ProfileInfo;
import org.jclouds.cloudsigma.domain.Server;
import org.jclouds.cloudsigma.domain.ServerInfo;
import org.jclouds.cloudsigma.domain.StaticIPInfo;
import org.jclouds.cloudsigma.domain.VLANInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToDriveInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToProfileInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToServerInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToStaticIPInfo;
import org.jclouds.cloudsigma.functions.KeyValuesDelimitedByBlankLinesToVLANInfo;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet;
import org.jclouds.cloudsigma.functions.ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet;
import org.jclouds.cloudsigma.functions.SplitNewlines;
import org.jclouds.cloudsigma.functions.SplitNewlinesAndReturnSecondField;
import org.jclouds.cloudsigma.options.CloneDriveOptions;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.Payload;
import org.jclouds.rest.annotations.PayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to CloudSigma via their REST API.
 * <p/>
 * 
 * @see CloudSigmaClient
 * @see <a href="http://cloudsigma.com/en/platform-details/the-api" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface CloudSigmaAsyncClient {

   /**
    * @see CloudSigmaClient#listStandardDrives
    */
   @GET
   @Path("/drives/standard/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardDrives();

   /**
    * @see CloudSigmaClient#listStandardCds
    */
   @GET
   @Path("/drives/standard/cd/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardCds();

   /**
    * @see CloudSigmaClient#listStandardImages
    */
   @GET
   @Path("/drives/standard/img/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listStandardImages();

   /**
    * @see CloudSigmaClient#cloneDrive
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/clone")
   @MapBinder(BindCloneDriveOptionsToPlainTextString.class)
   ListenableFuture<DriveInfo> cloneDrive(@PathParam("uuid") String sourceUuid, @PayloadParam("name") String newName,
         CloneDriveOptions... options);

   /**
    * @see CloudSigmaClient#getProfileInfo
    */
   @GET
   @Path("/profile/info")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToProfileInfo.class)
   ListenableFuture<ProfileInfo> getProfileInfo();

   /**
    * @see CloudSigmaClient#listDriveInfo
    */
   @GET
   @Path("/drives/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class)
   ListenableFuture<Set<DriveInfo>> listDriveInfo();

   /**
    * @see CloudSigmaClient#getDriveInfo
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/info")
   ListenableFuture<DriveInfo> getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#createDrive
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/create")
   ListenableFuture<DriveInfo> createDrive(@BinderParam(BindDriveToPlainTextString.class) Drive createDrive);

   /**
    * @see CloudSigmaClient#setDriveData
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   ListenableFuture<DriveInfo> setDriveData(@PathParam("uuid") String uuid,
         @BinderParam(BindDriveDataToPlainTextString.class) DriveData createDrive);

   /**
    * @see CloudSigmaClient#createServer
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create")
   ListenableFuture<ServerInfo> createServer(@BinderParam(BindServerToPlainTextString.class) Server createServer);

   /**
    * @see CloudSigmaClient#listServerInfo
    */
   @GET
   @Path("/servers/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class)
   ListenableFuture<Set<ServerInfo>> listServerInfo();

   /**
    * @see CloudSigmaClient#getServerInfo
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/info")
   ListenableFuture<ServerInfo> getServerInfo(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#setServerConfiguration
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/set")
   ListenableFuture<ServerInfo> setServerConfiguration(@PathParam("uuid") String uuid,
         @BinderParam(BindServerToPlainTextString.class) Server setServer);

   /**
    * @see CloudSigmaClient#listServers
    */
   @GET
   @Path("/servers/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listServers();

   /**
    * @see CloudSigmaClient#destroyServer
    */
   @GET
   @Path("/servers/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyServer(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#startServer
    */
   @POST
   @Path("/servers/{uuid}/start")
   ListenableFuture<Void> startServer(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#stopServer
    */
   @POST
   @Path("/servers/{uuid}/stop")
   ListenableFuture<Void> stopServer(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#shutdownServer
    */
   @POST
   @Path("/servers/{uuid}/shutdown")
   ListenableFuture<Void> shutdownServer(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#resetServer
    */
   @POST
   @Path("/servers/{uuid}/reset")
   ListenableFuture<Void> resetServer(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#listDrives
    */
   @GET
   @Path("/drives/list")
   @ResponseParser(SplitNewlines.class)
   ListenableFuture<Set<String>> listDrives();

   /**
    * @see CloudSigmaClient#destroyDrive
    */
   @GET
   @Path("/drives/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyDrive(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#createVLAN
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/create")
   @Payload("name {name}\n")
   @Produces(MediaType.TEXT_PLAIN)
   ListenableFuture<VLANInfo> createVLAN(@PayloadParam("name") String name);

   /**
    * @see CloudSigmaClient#listVLANInfo
    */
   @GET
   @Path("/resources/vlan/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet.class)
   ListenableFuture<Set<VLANInfo>> listVLANInfo();

   /**
    * @see CloudSigmaClient#getVLANInfo
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/{uuid}/info")
   ListenableFuture<VLANInfo> getVLANInfo(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#setVLANConfiguration
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/{uuid}/set")
   @Payload("name {name}\n")
   @Produces(MediaType.TEXT_PLAIN)
   ListenableFuture<VLANInfo> renameVLAN(@PathParam("uuid") String uuid, @PayloadParam("name") String name);

   /**
    * @see CloudSigmaClient#listVLANs
    */
   @GET
   @Path("/resources/vlan/list")
   @ResponseParser(SplitNewlinesAndReturnSecondField.class)
   ListenableFuture<Set<String>> listVLANs();

   /**
    * @see CloudSigmaClient#destroyVLAN
    */
   @GET
   @Path("/resources/vlan/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyVLAN(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#createStaticIP
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToStaticIPInfo.class)
   @Path("/resources/ip/create")
   ListenableFuture<StaticIPInfo> createStaticIP();

   /**
    * @see CloudSigmaClient#listStaticIPInfo
    */
   @GET
   @Path("/resources/ip/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet.class)
   ListenableFuture<Set<StaticIPInfo>> listStaticIPInfo();

   /**
    * @see CloudSigmaClient#getStaticIPInfo
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToStaticIPInfo.class)
   @Path("/resources/ip/{uuid}/info")
   ListenableFuture<StaticIPInfo> getStaticIPInfo(@PathParam("uuid") String uuid);

   /**
    * @see CloudSigmaClient#listStaticIPs
    */
   @GET
   @Path("/resources/ip/list")
   @ResponseParser(SplitNewlinesAndReturnSecondField.class)
   ListenableFuture<Set<String>> listStaticIPs();

   /**
    * @see CloudSigmaClient#destroyStaticIP
    */
   @GET
   @Path("/resources/ip/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   ListenableFuture<Void> destroyStaticIP(@PathParam("uuid") String uuid);

}
