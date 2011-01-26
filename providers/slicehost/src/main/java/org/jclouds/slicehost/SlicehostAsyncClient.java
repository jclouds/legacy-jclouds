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

package org.jclouds.slicehost;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;
import org.jclouds.slicehost.binders.BindCreateSliceToXmlPayload;
import org.jclouds.slicehost.domain.Backup;
import org.jclouds.slicehost.domain.Flavor;
import org.jclouds.slicehost.domain.Image;
import org.jclouds.slicehost.domain.Slice;
import org.jclouds.slicehost.filters.SlicehostBasic;
import org.jclouds.slicehost.xml.BackupHandler;
import org.jclouds.slicehost.xml.BackupsHandler;
import org.jclouds.slicehost.xml.FlavorHandler;
import org.jclouds.slicehost.xml.FlavorsHandler;
import org.jclouds.slicehost.xml.ImageHandler;
import org.jclouds.slicehost.xml.ImagesHandler;
import org.jclouds.slicehost.xml.SliceHandler;
import org.jclouds.slicehost.xml.SlicesHandler;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to Slicehost via their REST API.
 * <p/>
 * All commands return a ListenableFuture of the result from Slicehost. Any exceptions incurred
 * during processing will be wrapped in an {@link ExecutionException} as documented in
 * {@link ListenableFuture#get()}.
 * 
 * @see SlicehostClient
 * @see <a href="http://www.slicehost.com/docs/Slicehost_API.pdf" />
 * @author Adrian Cole
 */
@SkipEncoding( { '/', '=' })
@RequestFilters(SlicehostBasic.class)
public interface SlicehostAsyncClient {

   /**
    * @see SlicehostClient#listSlices
    */
   @GET
   @Path("/slices.xml")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(SlicesHandler.class)
   ListenableFuture<? extends Set<Slice>> listSlices();

   /**
    * @see SlicehostClient#getSlice
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/slices/{id}.xml")
   @XMLResponseParser(SliceHandler.class)
   ListenableFuture<Slice> getSlice(@PathParam("id") int id);

   /**
    * @see SlicehostClient#destroySlice
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("/slices/{id}/destroy.xml")
   ListenableFuture<Void> destroySlice(@PathParam("id") int id);

   /**
    * @see SlicehostClient#rebootSlice
    */
   @PUT
   @Path("/slices/{id}/reboot.xml")
   ListenableFuture<Void> rebootSlice(@PathParam("id") int id);

   /**
    * @see SlicehostClient#hardRebootSlice
    */
   @PUT
   @Path("/slices/{id}/hard_reboot.xml")
   ListenableFuture<Void> hardRebootSlice(@PathParam("id") int id);

   /**
    * @see SlicehostClient#createSlice
    */
   @POST
   @Path("/slices.xml")
   @MapBinder(BindCreateSliceToXmlPayload.class)
   @XMLResponseParser(SliceHandler.class)
   ListenableFuture<Slice> createSlice(@MapPayloadParam("name") String name, @MapPayloadParam("image_id") int imageId,
            @MapPayloadParam("flavor_id") int flavorId);

   /**
    * @see SlicehostClient#rebuildSliceFromImage
    */
   @PUT
   @Path("/slices/{id}/rebuild.xml")
   ListenableFuture<Void> rebuildSliceFromImage(@PathParam("id") int id, @QueryParam("image_id") int imageId);

   /**
    * @see SlicehostClient#rebuildSliceFromBackup
    */
   @PUT
   @Path("/slices/{id}/rebuild.xml")
   ListenableFuture<Void> rebuildSliceFromBackup(@PathParam("id") int id, @QueryParam("backup_id") int imageId);

   /**
    * @see SlicehostClient#listFlavors
    */
   @GET
   @Path("/flavors.xml")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(FlavorsHandler.class)
   ListenableFuture<? extends Set<Flavor>> listFlavors();

   /**
    * @see SlicehostClient#getFlavor
    */
   @GET
   @Path("/flavors/{id}.xml")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @XMLResponseParser(FlavorHandler.class)
   ListenableFuture<Flavor> getFlavor(@PathParam("id") int id);

   /**
    * @see SlicehostClient#listImages
    */
   @GET
   @Path("/images.xml")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(ImagesHandler.class)
   ListenableFuture<? extends Set<Image>> listImages();

   /**
    * @see SlicehostClient#getImage
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/images/{id}.xml")
   @XMLResponseParser(ImageHandler.class)
   ListenableFuture<Image> getImage(@PathParam("id") int id);

   /**
    * @see SlicehostClient#listBackups
    */
   @GET
   @Path("/backups.xml")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(BackupsHandler.class)
   ListenableFuture<? extends Set<Backup>> listBackups();

   /**
    * @see SlicehostClient#getBackup
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/backups/{id}.xml")
   @XMLResponseParser(BackupHandler.class)
   ListenableFuture<Backup> getBackup(@PathParam("id") int id);

}
