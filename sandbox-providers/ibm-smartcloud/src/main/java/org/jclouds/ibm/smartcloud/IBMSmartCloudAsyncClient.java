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
package org.jclouds.ibm.smartcloud;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.ibm.smartcloud.domain.Address;
import org.jclouds.ibm.smartcloud.domain.Image;
import org.jclouds.ibm.smartcloud.domain.Instance;
import org.jclouds.ibm.smartcloud.domain.Key;
import org.jclouds.ibm.smartcloud.domain.Location;
import org.jclouds.ibm.smartcloud.domain.Offering;
import org.jclouds.ibm.smartcloud.domain.StorageOffering;
import org.jclouds.ibm.smartcloud.domain.Volume;
import org.jclouds.ibm.smartcloud.functions.ParseLongFromDate;
import org.jclouds.ibm.smartcloud.options.CreateInstanceOptions;
import org.jclouds.ibm.smartcloud.options.RestartInstanceOptions;
import org.jclouds.ibm.smartcloud.xml.LocationHandler;
import org.jclouds.ibm.smartcloud.xml.LocationsHandler;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.ParamParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SkipEncoding;
import org.jclouds.rest.annotations.Unwrap;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to IBMSmartCloud via their REST API.
 * <p/>
 * 
 * @see IBMSmartCloudClient
 * @see <a href="http://www-180.ibm.com/cloud/enterprise/beta/support" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@SkipEncoding( { '{', '}' })
public interface IBMSmartCloudAsyncClient {
   public static final String VERSION = "20100331";

   /**
    * @see IBMSmartCloudClient#listImages()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/image")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Image>> listImages();

   /**
    * @see IBMSmartCloudClient#getImage(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/image/{imageId}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Image> getImage(@PathParam("imageId") String id);

   /**
    * @see IBMSmartCloudClient#getManifestOfImage
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/image/{imageId}/manifest")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Consumes(MediaType.TEXT_XML)
   ListenableFuture<String> getManifestOfImage(@PathParam("imageId") String id);

   /**
    * @see IBMSmartCloudClient#deleteImage
    */
   @DELETE
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/image/{imageId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteImage(@PathParam("imageId") String id);

   /**
    * @see IBMSmartCloudClient#setImageVisibility(long, Image.Visibility)
    */
   @PUT
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/image/{imageId}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Image> setImageVisibility(@PathParam("imageId") String id,
            @FormParam("visibility") Image.Visibility visibility);

   /**
    * @see IBMSmartCloudClient#listInstancesFromRequest(long)
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/requests/{requestId}")
   @Unwrap
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Set<? extends Instance>> listInstancesFromRequest(@PathParam("requestId") String requestId);

   /**
    * @see IBMSmartCloudClient#listInstances()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Instance>> listInstances();

   /**
    * @see IBMSmartCloudClient#getInstance(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances/{instanceId}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Instance> getInstance(@PathParam("instanceId") String id);

   /**
    * 
    * @see IBMSmartCloudClient#extendReservationForInstance(long,Date)
    */
   @PUT
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances/{instanceId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   ListenableFuture<Date> extendReservationForInstance(@PathParam("instanceId") String id,
            @FormParam("expirationTime") @ParamParser(ParseLongFromDate.class) Date expirationTime);

   /**
    * @see IBMSmartCloudClient#restartInstance
    */
   @PUT
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances/{instanceId}")
   @FormParams(keys = "state", values = "restart")
   ListenableFuture<Void> restartInstance(@PathParam("instanceId") String id, RestartInstanceOptions... options);

   /**
    * @see IBMSmartCloudClient#saveInstanceToImage
    */
   @PUT
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances/{instanceId}")
   @Consumes(MediaType.APPLICATION_JSON)
   @FormParams(keys = "state", values = "save")
   ListenableFuture<Image> saveInstanceToImage(@PathParam("instanceId") String id,
            @FormParam("name") String toImageName, @FormParam("description") String toImageDescription);

   /**
    * @see IBMSmartCloudClient#createInstanceInLocation
    */
   @POST
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap(depth = 2, edgeCollection = Set.class)
   ListenableFuture<Instance> createInstanceInLocation(@FormParam("location") String location,
            @FormParam("name") String name, @FormParam("imageID") String imageID,
            @FormParam("instanceType") String instanceType, CreateInstanceOptions... options);

   /**
    * @see IBMSmartCloudClient#deleteInstance
    */
   @DELETE
   @Path(IBMSmartCloudAsyncClient.VERSION + "/instances/{instanceId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteInstance(@PathParam("instanceId") String id);

   /**
    * @see IBMSmartCloudClient#listKeys()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Key>> listKeys();

   /**
    * @see IBMSmartCloudClient#generateKeyPair(String)
    */
   @POST
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Key> generateKeyPair(@FormParam("name") String name);

   /**
    * @see IBMSmartCloudClient#addPublicKey(String, String)
    */
   @POST
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys")
   ListenableFuture<Void> addPublicKey(@FormParam("name") String name, @FormParam("publicKey") String publicKey);

   /**
    * @see IBMSmartCloudClient#updatePublicKey(String, String)
    */
   @PUT
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys/{keyName}")
   ListenableFuture<Void> updatePublicKey(@PathParam("keyName") String name, @FormParam("publicKey") String publicKey);

   /**
    * @see IBMSmartCloudClient#setDefaultStatusOfKey(String, boolean)
    */
   @PUT
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys/{keyName}")
   ListenableFuture<Void> setDefaultStatusOfKey(@PathParam("keyName") String name,
            @FormParam("default") boolean isDefault);

   /**
    * @see IBMSmartCloudClient#getKey(String)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys/{keyName}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Key> getKey(@PathParam("keyName") String name);

   /**
    * @see IBMSmartCloudClient#deleteKey
    */
   @DELETE
   @Path(IBMSmartCloudAsyncClient.VERSION + "/keys/{keyName}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteKey(@PathParam("keyName") String name);

   /**
    * @see IBMSmartCloudClient#listStorageOfferings()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/storage")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends StorageOffering>> listStorageOfferings();

   /**
    * @see IBMSmartCloudClient#listVolumes()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/storage")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Volume>> listVolumes();

   /**
    * @see IBMSmartCloudClient#createVolumeInLocation
    */
   @POST
   @Path(IBMSmartCloudAsyncClient.VERSION + "/storage")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Volume> createVolumeInLocation(@FormParam("location") String location,
            @FormParam("name") String name, @FormParam("format") String format, @FormParam("size") String size,
            @FormParam("offeringID") String offeringID);

   /**
    * @see IBMSmartCloudClient#getVolume(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/storage/{volumeId}")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Volume> getVolume(@PathParam("volumeId") String id);

   /**
    * @see IBMSmartCloudClient#deleteVolume
    */
   @DELETE
   @Path(IBMSmartCloudAsyncClient.VERSION + "/storage/{volumeId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVolume(@PathParam("volumeId") String id);

   /**
    * @see IBMSmartCloudClient#listLocations()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/locations")
   @Consumes(MediaType.TEXT_XML)
   @XMLResponseParser(LocationsHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Location>> listLocations();

   /**
    * @see IBMSmartCloudClient#getLocation
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path(IBMSmartCloudAsyncClient.VERSION + "/locations/{locationId}")
   @Consumes(MediaType.TEXT_XML)
   @XMLResponseParser(LocationHandler.class)
   ListenableFuture<Location> getLocation(@PathParam("locationId") String id);

   /**
    * @see IBMSmartCloudClient#listAddressOfferings()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/offerings/address")
   @Unwrap
   @Consumes(MediaType.APPLICATION_JSON)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Offering>> listAddressOfferings();

   /**
    * @see IBMSmartCloudClient#listAddresses()
    */
   @GET
   @Path(IBMSmartCloudAsyncClient.VERSION + "/addresses")
   @Consumes(MediaType.APPLICATION_JSON)
   @Unwrap
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends Address>> listAddresses();

   /**
    * @see IBMSmartCloudClient#allocateAddressInLocation
    */
   @POST
   @Path(IBMSmartCloudAsyncClient.VERSION + "/addresses")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<Address> allocateAddressInLocation(@FormParam("location") String locationId,
            @FormParam("offeringID") String offeringID);

   /**
    * @see IBMSmartCloudClient#releaseAddress
    */
   @DELETE
   @Path(IBMSmartCloudAsyncClient.VERSION + "/addresses/{addressId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> releaseAddress(@PathParam("addressId") String id);

}
