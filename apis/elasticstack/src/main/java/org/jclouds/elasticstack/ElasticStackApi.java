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
package org.jclouds.elasticstack;

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
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

/**
 * Provides synchronous access to elasticstack via their REST API.
 * <p/>
 *
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.TEXT_PLAIN)
public interface ElasticStackApi extends Closeable {

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
    * create a new server
    *
    * @param createServer
    * @return newly created server
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create/stopped")
   ServerInfo createServer(
           @BinderParam(BindServerToPlainTextString.class) Server createServer);

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
    * Destroy a server
    *
    * @param uuid
    *           what to destroy
    */
   @POST
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
    * @param setDrive
    *           what values to change
    * @return new data
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToDriveInfo.class)
   @Path("/drives/{uuid}/set")
   DriveInfo setDriveData(@PathParam("uuid") String uuid,
                                            @BinderParam(BindDriveDataToPlainTextString.class) DriveData setDrive);

   /**
    * Destroy a drive
    *
    * @param uuid
    *           what to delete
    */
   @POST
   @Path("/drives/{uuid}/destroy")
   @Fallback(VoidOnNotFoundOr404.class)
   void destroyDrive(@PathParam("uuid") String uuid);

   /**
    * create and start a new server
    *
    * @param createServer
    * @return newly created server
    */
   @POST
   @Fallback(NullOnNotFoundOr404.class)
   @ResponseParser(KeyValuesDelimitedByBlankLinesToServerInfo.class)
   @Path("/servers/create")
   ServerInfo createAndStartServer(
           @BinderParam(BindServerToPlainTextString.class) Server createServer);

   /**
    * Image a drive from another drive. The actual imaging process is asynchronous, with progress
    * reported via drive info.
    *
    * @param source
    *           drive to copy from
    * @param destination
    *           drive to copy to
    */
   @POST
   @Path("/drives/{destination}/image/{source}")
   @Fallback(VoidOnNotFoundOr404.class)
   void imageDrive(@PathParam("source") String source, @PathParam("destination") String destination);

   /**
    * @see #imageDrive(String, String)
    * @param conversionType
    *           Supports 'gzip' or 'gunzip' conversions.
    */
   @POST
   @Path("/drives/{destination}/image/{source}/{conversion}")
   @Fallback(VoidOnNotFoundOr404.class)
   void imageDrive(@PathParam("source") String source, @PathParam("destination") String destination,
                                     @PathParam("conversion") ImageConversionType conversionType);

   /**
    * Read binary data from a drive
    *
    * @param uuid
    *           drive to read
    * @param offset
    *           start at the specified offset in bytes
    * @param size
    *           the specified size in bytes; must be <=4096k
    * @return binary content of the drive.
    */
   @POST
   @Consumes(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/read/{offset}/{size}")
   @ResponseParser(ReturnPayload.class)
   @Fallback(NullOnNotFoundOr404.class)
   Payload readDrive(@PathParam("uuid") String uuid, @PathParam("offset") long offset,
                                       @PathParam("size") long size);

   /**
    * Write binary data to a drive
    *
    * @param uuid
    *           drive to write
    * @param content
    *           what to write.
    *           <ul>
    *           <li>Binary data (Content-Type: application/octet-stream)</li>
    *           <li>Supports raw data or Content-Encoding: gzip</li>
    *           <li>Does not support Transfer-Encoding: chunked</li>
    *           </ul>
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write")
   @Fallback(VoidOnNotFoundOr404.class)
   void writeDrive(@PathParam("uuid") String uuid, Payload content);

   /**
    * @see ElasticStackApi#writeDrive(String, Payload)
    * @param offset
    *           the byte offset in the target drive at which to start writing, not an offset in the
    *           input stream.
    */
   @POST
   @Produces(MediaType.APPLICATION_OCTET_STREAM)
   @Path("/drives/{uuid}/write/{offset}")
   @Fallback(VoidOnNotFoundOr404.class)
   void writeDrive(@PathParam("uuid") String uuid, Payload content, @PathParam("offset") long offset);
}
