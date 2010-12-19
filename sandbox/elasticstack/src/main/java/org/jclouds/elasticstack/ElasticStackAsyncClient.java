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

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.jclouds.elasticstack.binders.BindServerToPlainTextString;
import org.jclouds.elasticstack.domain.ImageConversionType;
import org.jclouds.elasticstack.domain.Server;
import org.jclouds.elasticstack.domain.ServerInfo;
import org.jclouds.elasticstack.functions.KeyValuesDelimitedByBlankLinesToServerInfo;
import org.jclouds.elasticstack.functions.ReturnPayload;
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
public interface ElasticStackAsyncClient extends CommonElasticStackAsyncClient {

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
