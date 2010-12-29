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

package org.jclouds.deltacloud;

import java.net.URI;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.deltacloud.collections.HardwareProfiles;
import org.jclouds.deltacloud.collections.Images;
import org.jclouds.deltacloud.collections.InstanceStates;
import org.jclouds.deltacloud.collections.Instances;
import org.jclouds.deltacloud.collections.Realms;
import org.jclouds.deltacloud.domain.DeltacloudCollection;
import org.jclouds.deltacloud.domain.HardwareProfile;
import org.jclouds.deltacloud.domain.Image;
import org.jclouds.deltacloud.domain.Instance;
import org.jclouds.deltacloud.domain.InstanceState;
import org.jclouds.deltacloud.domain.Realm;
import org.jclouds.deltacloud.domain.Transition;
import org.jclouds.deltacloud.options.CreateInstanceOptions;
import org.jclouds.deltacloud.xml.DeltacloudCollectionsHandler;
import org.jclouds.deltacloud.xml.HardwareProfileHandler;
import org.jclouds.deltacloud.xml.HardwareProfilesHandler;
import org.jclouds.deltacloud.xml.ImageHandler;
import org.jclouds.deltacloud.xml.ImagesHandler;
import org.jclouds.deltacloud.xml.InstanceHandler;
import org.jclouds.deltacloud.xml.InstanceStatesHandler;
import org.jclouds.deltacloud.xml.InstancesHandler;
import org.jclouds.deltacloud.xml.RealmHandler;
import org.jclouds.deltacloud.xml.RealmsHandler;
import org.jclouds.http.filters.BasicAuthentication;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptyMultimapOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to deltacloud via their REST API.
 * <p/>
 * 
 * @see DeltacloudClient
 * @see <a href="TODO: insert URL of provider documentation" />
 * @author Adrian Cole
 */
@RequestFilters(BasicAuthentication.class)
@Consumes(MediaType.APPLICATION_XML)
public interface DeltacloudAsyncClient {

   /**
    * @see DeltacloudClient#getCollections
    */
   @GET
   @Path("")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(DeltacloudCollectionsHandler.class)
   ListenableFuture<? extends Set<? extends DeltacloudCollection>> getCollections();

   /**
    * @see DeltacloudClient#getInstanceStates
    */
   @GET
   @Endpoint(InstanceStates.class)
   @Path("")
   @ExceptionParser(ReturnEmptyMultimapOnNotFoundOr404.class)
   @XMLResponseParser(InstanceStatesHandler.class)
   ListenableFuture<? extends Multimap<InstanceState, ? extends Transition>> getInstanceStates();

   /**
    * @see DeltacloudClient#listRealms
    */
   @GET
   @Endpoint(Realms.class)
   @Path("")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(RealmsHandler.class)
   ListenableFuture<? extends Set<? extends Realm>> listRealms();

   /**
    * @see DeltacloudClient#getRealm
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("")
   @XMLResponseParser(RealmHandler.class)
   ListenableFuture<Realm> getRealm(@EndpointParam URI realmHref);

   /**
    * @see DeltacloudClient#listImages
    */
   @GET
   @Endpoint(Images.class)
   @Path("")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(ImagesHandler.class)
   ListenableFuture<? extends Set<? extends Image>> listImages();

   /**
    * @see DeltacloudClient#getImage
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("")
   @XMLResponseParser(ImageHandler.class)
   ListenableFuture<Image> getImage(@EndpointParam URI imageHref);

   /**
    * @see DeltacloudClient#listHardwareProfiles
    */
   @GET
   @Endpoint(HardwareProfiles.class)
   @Path("")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(HardwareProfilesHandler.class)
   ListenableFuture<? extends Set<? extends HardwareProfile>> listHardwareProfiles();

   /**
    * @see DeltacloudClient#getHardwareProfile
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("")
   @XMLResponseParser(HardwareProfileHandler.class)
   ListenableFuture<HardwareProfile> getHardwareProfile(@EndpointParam URI profileHref);

   /**
    * @see DeltacloudClient#listInstances
    */
   @GET
   @Endpoint(Instances.class)
   @Path("")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @XMLResponseParser(InstancesHandler.class)
   ListenableFuture<? extends Set<? extends Instance>> listInstances();

   /**
    * @see DeltacloudClient#getInstance
    */
   @GET
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   @Path("")
   @XMLResponseParser(InstanceHandler.class)
   ListenableFuture<Instance> getInstance(@EndpointParam URI instanceHref);

   /**
    * @see DeltacloudClient#createInstance
    */
   @POST
   @Endpoint(Instances.class)
   @Path("")
   @XMLResponseParser(InstanceHandler.class)
   ListenableFuture<Instance> createInstance(@FormParam("image_id") String imageId, CreateInstanceOptions... options);

   /**
    * @see DeltacloudClient#performInstanceAction
    */
   @POST
   @Path("")
   ListenableFuture<Void> performAction(@EndpointParam URI actionRef);

   /**
    * @see DeltacloudClient#deleteResource
    */
   @DELETE
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   @Path("")
   ListenableFuture<Void> deleteResource(@EndpointParam URI resourceHref);

}
