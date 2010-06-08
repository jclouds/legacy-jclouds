/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.ibmdev;

import java.util.Date;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.ibmdev.binders.BindExpirationTimeToJsonPayload;
import org.jclouds.ibmdev.binders.SaveInstanceBinder;
import org.jclouds.ibmdev.domain.Address;
import org.jclouds.ibmdev.domain.Image;
import org.jclouds.ibmdev.domain.Instance;
import org.jclouds.ibmdev.domain.Key;
import org.jclouds.ibmdev.domain.Location;
import org.jclouds.ibmdev.domain.Volume;
import org.jclouds.ibmdev.functions.ParseAddressFromJson;
import org.jclouds.ibmdev.functions.ParseAddressesFromJson;
import org.jclouds.ibmdev.functions.ParseExpirationTimeFromJson;
import org.jclouds.ibmdev.functions.ParseImageFromJson;
import org.jclouds.ibmdev.functions.ParseImagesFromJson;
import org.jclouds.ibmdev.functions.ParseInstanceFromJson;
import org.jclouds.ibmdev.functions.ParseInstancesFromJson;
import org.jclouds.ibmdev.functions.ParseKeyFromJson;
import org.jclouds.ibmdev.functions.ParseKeysFromJson;
import org.jclouds.ibmdev.functions.ParseVolumeFromJson;
import org.jclouds.ibmdev.functions.ParseVolumesFromJson;
import org.jclouds.ibmdev.options.CreateInstanceOptions;
import org.jclouds.ibmdev.options.RestartInstanceOptions;
import org.jclouds.ibmdev.xml.LocationHandler;
import org.jclouds.ibmdev.xml.LocationsHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.MapBinder;
import org.jclouds.rest.annotations.MapPayloadParam;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.binders.BindToJsonPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to IBMDeveloperCloud via their REST API.
 * <p/>
 * 
 * @see IBMDeveloperCloudClient
 * @see <a href="http://www-180.ibm.com/cloud/enterprise/beta/support" />
 * @author Adrian Cole
 */
@Endpoint(IBMDeveloperCloud.class)
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.APPLICATION_JSON)
public interface IBMDeveloperCloudAsyncClient {

   /**
    * @see IBMDeveloperCloudClient#listImages()
    */
   @GET
   @Path("/images")
   @ResponseParser(ParseImagesFromJson.class)
   ListenableFuture<Set<? extends Image>> listImages();

   /**
    * @see IBMDeveloperCloudClient#getImage(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/images/{imageId}")
   @ResponseParser(ParseImageFromJson.class)
   ListenableFuture<Image> getImage(@PathParam("imageId") long id);

   /**
    * @see IBMDeveloperCloudClient#deleteImage
    */
   @DELETE
   @Path("/images/{imageId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteImage(@PathParam("imageId") long id);

   /**
    * @see IBMDeveloperCloudClient#setImageVisibility(long, Image.Visibility)
    */
   @PUT
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/images/{imageId}")
   @ResponseParser(ParseImageFromJson.class)
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Image> setImageVisibility(@PathParam("imageId") long id,
            @MapPayloadParam("visibility") Image.Visibility visibility);

   /**
    * @see IBMDeveloperCloudClient#listInstancesFromRequest(long)
    */
   @GET
   @Path("/requests/{requestId}")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @ResponseParser(ParseInstancesFromJson.class)
   ListenableFuture<Set<? extends Instance>> listInstancesFromRequest(
            @PathParam("requestId") long requestId);

   /**
    * @see IBMDeveloperCloudClient#listInstances()
    */
   @GET
   @Path("/instances")
   @ResponseParser(ParseInstancesFromJson.class)
   ListenableFuture<Set<? extends Instance>> listInstances();

   /**
    * @see IBMDeveloperCloudClient#getInstance(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/instances/{instanceId}")
   @ResponseParser(ParseInstanceFromJson.class)
   ListenableFuture<Instance> getInstance(@PathParam("instanceId") long id);

   /**
    * @see IBMDeveloperCloudClient#extendReservationForInstance(long,Date)
    */
   @PUT
   @Path("/instances/{instanceId}")
   @ResponseParser(ParseExpirationTimeFromJson.class)
   ListenableFuture<Date> extendReservationForInstance(@PathParam("instanceId") long id,
            @BinderParam(BindExpirationTimeToJsonPayload.class) Date expirationTime);

   /**
    * @see IBMDeveloperCloudClient#restartInstance
    */
   @PUT
   @Path("/instances/{instanceId}")
   @MapBinder(RestartInstanceOptions.class)
   ListenableFuture<Void> restartInstance(@PathParam("instanceId") long id,
            RestartInstanceOptions... options);

   /**
    * @see IBMDeveloperCloudClient#saveInstanceToImage
    */
   @PUT
   @Path("/instances/{instanceId}")
   @MapBinder(SaveInstanceBinder.class)
   @ResponseParser(ParseImageFromJson.class)
   ListenableFuture<Image> saveInstanceToImage(@PathParam("instanceId") long id,
            @MapPayloadParam("name") String toImageName,
            @MapPayloadParam("description") String toImageDescription);

   /**
    * @see IBMDeveloperCloudClient#createInstanceInLocation
    */
   @POST
   @Path("/instances")
   @MapBinder(CreateInstanceOptions.class)
   @ResponseParser(ParseInstanceFromJson.class)
   ListenableFuture<Instance> createInstanceInLocation(
            @MapPayloadParam("location") String location, @MapPayloadParam("name") String name,
            @MapPayloadParam("imageID") String imageID,
            @MapPayloadParam("instanceType") String instanceType, CreateInstanceOptions... options);

   /**
    * @see IBMDeveloperCloudClient#deleteInstance
    */
   @DELETE
   @Path("/instances/{instanceId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteInstance(@PathParam("instanceId") long id);

   /**
    * @see IBMDeveloperCloudClient#listKeys()
    */
   @GET
   @Path("/keys")
   @ResponseParser(ParseKeysFromJson.class)
   ListenableFuture<Set<? extends Key>> listKeys();

   /**
    * @see IBMDeveloperCloudClient#generateKeyPair(String)
    */
   @POST
   @Path("/keys")
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<Key> generateKeyPair(@MapPayloadParam("name") String name);

   /**
    * @see IBMDeveloperCloudClient#addPublicKey(String, String)
    */
   @POST
   @Path("/keys")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Void> addPublicKey(@MapPayloadParam("name") String name,
            @MapPayloadParam("publicKey") String publicKey);

   /**
    * @see IBMDeveloperCloudClient#updatePublicKey(String, String)
    */
   @PUT
   @Path("/keys/{keyName}")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Void> updatePublicKey(@PathParam("keyName") String name,
            @MapPayloadParam("publicKey") String publicKey);

   /**
    * @see IBMDeveloperCloudClient#setDefaultStatusOfKey(String, boolean)
    */
   @PUT
   @Path("/keys/{keyName}")
   @MapBinder(BindToJsonPayload.class)
   ListenableFuture<Void> setDefaultStatusOfKey(@PathParam("keyName") String name,
            @MapPayloadParam("default") boolean isDefault);

   /**
    * @see IBMDeveloperCloudClient#getKey(String)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/keys/{keyName}")
   @ResponseParser(ParseKeyFromJson.class)
   ListenableFuture<Key> getKey(@PathParam("keyName") String name);

   /**
    * @see IBMDeveloperCloudClient#deleteKey
    */
   @DELETE
   @Path("/keys/{keyName}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteKey(@PathParam("keyName") String name);

   /**
    * @see IBMDeveloperCloudClient#listVolumes()
    */
   @GET
   @Path("/storage")
   @ResponseParser(ParseVolumesFromJson.class)
   ListenableFuture<Set<? extends Volume>> listVolumes();

   /**
    * @see IBMDeveloperCloudClient#createVolumeInLocation
    */
   @POST
   @Path("/storage")
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParseVolumeFromJson.class)
   ListenableFuture<Volume> createVolumeInLocation(@MapPayloadParam("location") String location,
            @MapPayloadParam("name") String name, @MapPayloadParam("format") String format,
            @MapPayloadParam("size") String size);

   /**
    * @see IBMDeveloperCloudClient#getVolume(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/storage/{volumeId}")
   @ResponseParser(ParseVolumeFromJson.class)
   ListenableFuture<Volume> getVolume(@PathParam("volumeId") long id);

   /**
    * @see IBMDeveloperCloudClient#deleteVolume
    */
   @DELETE
   @Path("/storage/{volumeId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> deleteVolume(@PathParam("volumeId") long id);

   /**
    * @see IBMDeveloperCloudClient#listLocations()
    */
   @GET
   @Path("/locations")
   @Consumes(MediaType.TEXT_XML)
   @XMLResponseParser(LocationsHandler.class)
   ListenableFuture<Set<? extends Location>> listLocations();

   /**
    * @see IBMDeveloperCloudClient#getLocation(long)
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("/locations/{locationId}")
   @Consumes(MediaType.TEXT_XML)
   @XMLResponseParser(LocationHandler.class)
   ListenableFuture<Location> getLocation(@PathParam("locationId") long id);

   /**
    * @see IBMDeveloperCloudClient#listAddresses()
    */
   @GET
   @Path("/addresses")
   @ResponseParser(ParseAddressesFromJson.class)
   ListenableFuture<Set<? extends Address>> listAddresses();

   /**
    * @see IBMDeveloperCloudClient#allocateAddressInLocation(long)
    */
   @POST
   @Path("/addresses")
   @MapBinder(BindToJsonPayload.class)
   @ResponseParser(ParseAddressFromJson.class)
   ListenableFuture<Address> allocateAddressInLocation(@MapPayloadParam("location") long locationId);

   /**
    * @see IBMDeveloperCloudClient#releaseAddress(long)
    */
   @DELETE
   @Path("/addresses/{addressId}")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> releaseAddress(@PathParam("addressId") long id);
}
