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
package org.jclouds.gogrid.services;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;
import static org.jclouds.gogrid.reference.GoGridQueryParams.ID_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_DESCRIPTION_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_FRIENDLY_NAME_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.IMAGE_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.LOOKUP_LIST_KEY;
import static org.jclouds.gogrid.reference.GoGridQueryParams.SERVER_ID_OR_NAME_KEY;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.gogrid.GoGridAsyncClient;
import org.jclouds.gogrid.binders.BindIdsToQueryParams;
import org.jclouds.gogrid.binders.BindNamesToQueryParams;
import org.jclouds.gogrid.domain.Option;
import org.jclouds.gogrid.domain.ServerImage;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseImageFromJsonResponse;
import org.jclouds.gogrid.functions.ParseImageListFromJsonResponse;
import org.jclouds.gogrid.functions.ParseOptionsFromJsonResponse;
import org.jclouds.gogrid.options.GetImageListOptions;
import org.jclouds.gogrid.options.SaveImageOptions;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = GoGridAsyncClient.VERSION)
public interface GridImageAsyncClient {

   /**
    * @see GridImageClient#getImageList
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/list")
   ListenableFuture<Set<ServerImage>> getImageList(GetImageListOptions... options);

   /**
    * @see GridImageClient#getImagesById
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/get")
   ListenableFuture<Set<ServerImage>> getImagesById(@BinderParam(BindIdsToQueryParams.class) Long... ids);

   /**
    * @see GridImageClient#getImagesByName
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/get")
   ListenableFuture<Set<ServerImage>> getImagesByName(@BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * @see GridImageClient#editImageDescription
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/edit")
   ListenableFuture<ServerImage> editImageDescription(@QueryParam(IMAGE_KEY) String idOrName,
         @QueryParam(IMAGE_DESCRIPTION_KEY) String newDescription);

   /**
    * @see GridImageClient#editImageFriendlyName
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/edit")
   ListenableFuture<ServerImage> editImageFriendlyName(@QueryParam(IMAGE_KEY) String idOrName,
         @QueryParam(IMAGE_FRIENDLY_NAME_KEY) String newFriendlyName);

   /**
    * @see GridImageClient#getDatacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "datacenter")
   ListenableFuture<Set<Option>> getDatacenters();

   /**
    * @see GridImageClient#deleteById(Long)
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/delete")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<ServerImage> deleteById(@QueryParam(ID_KEY) long id);

   /**
    * @see GridImageClient#saveImageFromServer
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/save")
   ListenableFuture<ServerImage> saveImageFromServer(@QueryParam(IMAGE_FRIENDLY_NAME_KEY) String friendlyName,
         @QueryParam(SERVER_ID_OR_NAME_KEY) String idOrName, SaveImageOptions... options);
}
