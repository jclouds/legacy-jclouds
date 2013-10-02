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
package org.jclouds.gogrid.features;

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

import org.jclouds.Fallbacks;
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

/**
 * Manages the server images
 * 
 * @see <a
 *      href="http://wiki.gogrid.com/wiki/index.php/API#Server_Image_Methods"/>
 * @author Oleksiy Yarmula
 */
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "{jclouds.api-version}")
public interface GridImageApi {

   /**
    * Returns all server images.
    *
    * @param options
    *           options to narrow the search down
    * @return server images found
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/list")
   Set<ServerImage> getImageList(GetImageListOptions... options);

   /**
    * Returns images, found by specified ids
    *
    * @param ids
    *           the ids that match existing images
    * @return images found
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/get")
   Set<ServerImage> getImagesById(@BinderParam(BindIdsToQueryParams.class) Long... ids);

   /**
    * Returns images, found by specified names
    *
    * @param names
    *           the names that march existing images
    * @return images found
    */
   @GET
   @ResponseParser(ParseImageListFromJsonResponse.class)
   @Path("/grid/image/get")
   Set<ServerImage> getImagesByName(@BinderParam(BindNamesToQueryParams.class) String... names);

   /**
    * Edits an existing image
    *
    * @param idOrName
    *           id or name of the existing image
    * @param newDescription
    *           description to replace the current one
    * @return edited server image
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/edit")
   ServerImage editImageDescription(@QueryParam(IMAGE_KEY) String idOrName,
                                    @QueryParam(IMAGE_DESCRIPTION_KEY) String newDescription);

   /**
    * Edits an existing image
    *
    * @param idOrName
    *           id or name of the existing image
    * @param newFriendlyName
    *           friendly name to replace the current one
    * @return edited server image
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/edit")
   ServerImage editImageFriendlyName(@QueryParam(IMAGE_KEY) String idOrName,
                                     @QueryParam(IMAGE_FRIENDLY_NAME_KEY) String newFriendlyName);

   /**
    * Retrieves the list of supported Datacenters to save images in. The objects
    * will have datacenter ID, name and description. In most cases, id or name
    * will be used for {@link #getImageList}.
    *
    * @return supported datacenters
    */
   @GET
   @ResponseParser(ParseOptionsFromJsonResponse.class)
   @Path("/common/lookup/list")
   @QueryParams(keys = LOOKUP_LIST_KEY, values = "datacenter")
   Set<Option> getDatacenters();

   /**
    * Deletes an existing image
    *
    * @param id
    *           id of the existing image
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/delete")
   @Fallback(Fallbacks.NullOnNotFoundOr404.class)
   ServerImage deleteById(@QueryParam(ID_KEY) long id);

   /**
    * This call will save a private (visible to only you) server image to your
    * library of available images. The image will be saved from an existing
    * server.
    *
    * @param idOrName
    *           id or name of the existing server
    * @param friendlyName
    *           friendly name of the image
    * @return saved server image
    */
   @GET
   @ResponseParser(ParseImageFromJsonResponse.class)
   @Path("/grid/image/save")
   ServerImage saveImageFromServer(@QueryParam(IMAGE_FRIENDLY_NAME_KEY) String friendlyName,
                                   @QueryParam(SERVER_ID_OR_NAME_KEY) String idOrName, SaveImageOptions... options);

}
