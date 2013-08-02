/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.cloudsigma;

import java.io.Closeable;
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

/**
 * Provides synchronous access to CloudSigma via their REST API.
 * <p/>
 *
 * @see <a href="http://cloudsigma.com/en/platform-details/the-api" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface CloudSigmaApi extends Closeable {

   /**
    * list of drive uuids that are in the library
    *
    * @return or empty set if no drives are found
    */
   @GET
   @Path("/drives/standard/list")
   @ResponseParser(SplitNewlines.class)
   Set<String> listStandardDrives();

   /**
    * list of cd uuids that are in the library
    *
    * @return or empty set if no cds are found
    */
   @GET
   @Path("/drives/standard/cd/list")
   @ResponseParser(SplitNewlines.class)
   Set<String> listStandardCds();

   /**
    * list of image uuids that are in the library
    *
    * @return or empty set if no images are found
    */
   @GET
   @Path("/drives/standard/img/list")
   @ResponseParser(SplitNewlines.class)
   Set<String> listStandardImages();

   /**
    * Clone an existing drive. By default, the size is the same as the source
    *
    * @param sourceUuid
    *           source to clone
    * @param newName
    *           name of the resulting drive
    * @param options
    *           options to control size
    * @return new drive
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/clone")
   @MapBinder(BindCloneDriveOptionsToPlainTextString.class)
   DriveInfo cloneDrive(@PathParam("uuid") String sourceUuid, @PayloadParam("name") String newName,
                                          CloneDriveOptions... options);

   /**
    * Get profile info
    *
    * @return info or null, if not found
    */
   @GET
   @Path("/profile/info")
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToProfileInfo.class)
   ProfileInfo getProfileInfo();

   /**
    * Get all drives info
    *
    * @return or empty set if no drives are found
    */
   @GET
   @Path("/drives/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToDriveInfoSet.class)
   Set<DriveInfo> listDriveInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/info")
   DriveInfo getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * create a new drive
    *
    * @param createDrive
    *           required parameters: name, size
    * @return newly created drive
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/create")
   DriveInfo createDrive(@BinderParam(BindDriveToPlainTextString.class) Drive createDrive);

   /**
    * set extra drive data
    *
    * @param uuid
    *           what drive to change
    * @param createDrive
    *           what values to change
    * @return new data
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   DriveInfo setDriveData(@PathParam("uuid") String uuid,
                                            @BinderParam(BindDriveDataToPlainTextString.class) DriveData createDrive);

   /**
    * create a new server
    *
    * @param createServer
    * @return newly created server
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create")
   ServerInfo createServer(@BinderParam(BindServerToPlainTextString.class) Server createServer);

   /**
    * Get all servers info
    *
    * @return or empty set if no servers are found
    */
   @GET
   @Path("/servers/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToServerInfoSet.class)
   Set<ServerInfo> listServerInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/info")
   ServerInfo getServerInfo(@PathParam("uuid") String uuid);

   /**
    * set server configuration
    *
    * @param uuid
    *           what server to change
    * @param setServer
    *           what values to change
    * @return new data
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/{uuid}/set")
   ServerInfo setServerConfiguration(@PathParam("uuid") String uuid,
                                                       @BinderParam(BindServerToPlainTextString.class) Server setServer);

   /**
    * list of server uuids in your account
    *
    * @return or empty set if no servers are found
    */
   @GET
   @Path("/servers/list")
   @ResponseParser(SplitNewlines.class)
   Set<String> listServers();

   /**
    * Destroy a server
    *
    * @param uuid
    *           what to destroy
    */
   @GET
   @Path("/servers/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   void destroyServer(@PathParam("uuid") String uuid);

   /**
    * Start a server
    *
    * @param uuid
    *           what to start
    */
   @POST
   @Path("/servers/{uuid}/start")
   void startServer(@PathParam("uuid") String uuid);

   /**
    * Stop a server
    * <p/>
    * Kills the server immediately, equivalent to a power failure. Server reverts to a stopped
    * status if it is persistent and is automatically destroyed otherwise.
    *
    * @param uuid
    *           what to stop
    */
   @POST
   @Path("/servers/{uuid}/stop")
   void stopServer(@PathParam("uuid") String uuid);

   /**
    * Shutdown a server
    * <p/>
    * Sends the server an ACPI power-down event. Server reverts to a stopped status if it is
    * persistent and is automatically destroyed otherwise.
    * <h4>note</h4> behaviour on shutdown depends on how your server OS is set up to respond to an
    * ACPI power button signal.
    *
    * @param uuid
    *           what to shutdown
    */
   @POST
   @Path("/servers/{uuid}/shutdown")
   void shutdownServer(@PathParam("uuid") String uuid);

   /**
    * Reset a server
    *
    * @param uuid
    *           what to reset
    */
   @POST
   @Path("/servers/{uuid}/reset")
   void resetServer(@PathParam("uuid") String uuid);

   /**
    * list of drive uuids in your account
    *
    * @return or empty set if no drives are found
    */
   @GET
   @Path("/drives/list")
   @ResponseParser(SplitNewlines.class)
   Set<String> listDrives();

   /**
    * Destroy a drive
    *
    * @param uuid
    *           what to delete
    */
   @GET
   @Path("/drives/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   void destroyDrive(@PathParam("uuid") String uuid);

   /**
    * create a new vlan
    *
    * @param name
    * @return newly created vlan
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/create")
   @Payload("name {name}\n")
   @Produces(MediaType.TEXT_PLAIN)
   VLANInfo createVLAN(@PayloadParam("name") String name);

   /**
    * Get all vlans info
    *
    * @return or empty set if no vlans are found
    */
   @GET
   @Path("/resources/vlan/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToVLANInfoSet.class)
   Set<VLANInfo> listVLANInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/{uuid}/info")
   VLANInfo getVLANInfo(@PathParam("uuid") String uuid);

   /**
    * set vlan configuration
    *
    * @param uuid
    *           what vlan to change
    * @param name
    *           what the new name is
    * @return new data
    */
   @POST
   @ResponseParser(KeyValuesDelimitedByBlankLinesToVLANInfo.class)
   @Path("/resources/vlan/{uuid}/set")
   @Payload("name {name}\n")
   @Produces(MediaType.TEXT_PLAIN)
   VLANInfo renameVLAN(@PathParam("uuid") String uuid, @PayloadParam("name") String name);

   /**
    * list of vlan uuids in your account
    *
    * @return or empty set if no vlans are found
    */
   @GET
   @Path("/resources/vlan/list")
   @ResponseParser(SplitNewlinesAndReturnSecondField.class)
   Set<String> listVLANs();

   /**
    * Destroy a vlan
    *
    * @param uuid
    *           what to destroy
    */
   @GET
   @Path("/resources/vlan/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   void destroyVLAN(@PathParam("uuid") String uuid);

   /**
    * create a new ip
    *
    * @return newly created ip
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToStaticIPInfo.class)
   @Path("/resources/ip/create")
   StaticIPInfo createStaticIP();

   /**
    * Get all ips info
    *
    * @return or empty set if no ips are found
    */
   @GET
   @Path("/resources/ip/info")
   @ResponseParser(ListOfKeyValuesDelimitedByBlankLinesToStaticIPInfoSet.class)
   Set<StaticIPInfo> listStaticIPInfo();

   /**
    * @param uuid
    *           what to get
    * @return null, if not found
    */
   @GET
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToStaticIPInfo.class)
   @Path("/resources/ip/{uuid}/info")
   StaticIPInfo getStaticIPInfo(@PathParam("uuid") String uuid);

   /**
    * list of ip uuids in your account
    *
    * @return or empty set if no ips are found
    */
   @GET
   @Path("/resources/ip/list")
   @ResponseParser(SplitNewlinesAndReturnSecondField.class)
   Set<String> listStaticIPs();

   /**
    * Destroy a ip
    *
    * @param uuid
    *           what to destroy
    */
   @GET
   @Path("/resources/ip/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   void destroyStaticIP(@PathParam("uuid") String uuid);

}
