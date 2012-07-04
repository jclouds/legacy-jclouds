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
package org.jclouds.glesys.features;

import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.glesys.domain.Archive;
import org.jclouds.glesys.domain.ArchiveAllowedArguments;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Archive data via the Glesys REST API.
 * <p/>
 *
 * @author Adam Lowe
 * @see ArchiveClient
 * @see <a href="https://customer.glesys.com/api.php" />
 */
@RequestFilters(BasicAuthentication.class)
public interface ArchiveAsyncClient {

   /**
    * @see ArchiveClient#listArchives
    */
   @POST
   @Path("/archive/list/format/json")
   @SelectJson("archives")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<Archive>> listArchives();

   /**
    * @see ArchiveClient#getArchive
    */
   @POST
   @Path("/archive/details/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Archive> getArchive(@FormParam("username") String username);

   /**
    * @see ArchiveClient#createArchive
    */
   @POST
   @Path("/archive/create/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Archive> createArchive(@FormParam("username") String username, @FormParam("password") String password,
                                        @FormParam("size")int size);

   /**
    * @see ArchiveClient#deleteArchive
    */
   @POST
   @Path("/archive/delete/format/json")
   ListenableFuture<Void> deleteArchive(@FormParam("username") String username);

   /**
    * @see ArchiveClient#resizeArchive
    */
   @POST
   @Path("/archive/resize/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Archive> resizeArchive(@FormParam("username") String username, @FormParam("size") int size);
   /**
    * @see ArchiveClient#changeArchivePassword
    */
   @POST
   @Path("/archive/changepassword/format/json")
   @SelectJson("details")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Archive> changeArchivePassword(@FormParam("username") String username, @FormParam("password") String password);

   /**
    * @see org.jclouds.glesys.features.ArchiveClient#getArchiveAllowedArguments
    */
   @GET
   @Path("/archive/allowedarguments/format/json")
   @SelectJson("argumentslist")
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<ArchiveAllowedArguments> getArchiveAllowedArguments();

}
