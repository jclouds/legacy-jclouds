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
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.elastichosts.handlers.NewlineDelimitedStringHandler;
import org.jclouds.http.filters.BasicAuthentication;
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
   /*
    * TODO: define interface methods for ElasticHosts
    */

   /**
    * @see ElasticHostsClient#listDrives()
    */
   @GET
   @Path("/drives/list")
   @ResponseParser(NewlineDelimitedStringHandler.class)
   ListenableFuture<Set<String>> listDrives();

   /**
    * @see ElasticHostsClient#get(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/drives/{uuid}/info")
   ListenableFuture<String> getDriveInfo(@PathParam("uuid") String uuid);

   /**
    * @see ElasticHostsClient#delete
    */
   @DELETE
   @Path("/drives/{uuid}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteDrive(@PathParam("uuid") String uuid);
}
