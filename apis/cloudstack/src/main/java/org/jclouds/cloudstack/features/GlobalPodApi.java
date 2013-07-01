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
package org.jclouds.cloudstack.features;

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.cloudstack.domain.Pod;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.cloudstack.options.CreatePodOptions;
import org.jclouds.cloudstack.options.ListPodsOptions;
import org.jclouds.cloudstack.options.UpdatePodOptions;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.OnlyElement;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

/**
 * Provides synchronous access to CloudStack Pod features available to Global
 * Admin users.
 *
 * @author Richard Downer
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Global_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface GlobalPodApi {

   /**
    * Lists pods
    *
    * @param options
    *           if present, how to constrain the list.
    * @return pods matching query, or empty set, if no pods are found
    */
   @Named("listPods")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPods", "true" })
   @SelectJson("pod")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptySetOnNotFoundOr404.class)
   Set<Pod> listPods(ListPodsOptions... options);

   /**
    * get a specific pod by id
    *
    * @param id
    *           pod to get
    * @return pod or null if not found
    */
   @Named("listPods")
   @GET
   @QueryParams(keys = { "command", "listAll" }, values = { "listPods", "true" })
   @SelectJson("pod")
   @OnlyElement
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Pod getPod(@QueryParam("id") String id);

   /**
    * Creates a new Pod.
    *
    * @param name the name of the Pod
    * @param zoneId the Zone ID in which the Pod will be created
    * @param startIp the starting IP address for the Pod
    * @param endIp the ending IP address for the Pod
    * @param gateway the gateway for the Pod
    * @param netmask the netmask for the Pod
    * @param createPodOptions optional arguments
    * @return the new Pod
    */
   @Named("createPod")
   @GET
   @QueryParams(keys = "command", values = "createPod")
   @SelectJson("pod")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Pod createPod(@QueryParam("name") String name, @QueryParam("zoneid") String zoneId, @QueryParam("startip") String startIp, @QueryParam("endip") String endIp, @QueryParam("gateway") String gateway, @QueryParam("netmask") String netmask, CreatePodOptions... createPodOptions);

   /**
    * Creates a new Pod.
    *
    * @param name the name of the Pod
    * @param zoneId the Zone ID in which the Pod will be created
    * @param startIp the starting IP address for the Pod
    * @param gateway the gateway for the Pod
    * @param netmask the netmask for the Pod
    * @param createPodOptions optional arguments
    * @return the new Pod
    */
   @Named("createPod")
   @GET
   @QueryParams(keys = "command", values = "createPod")
   @SelectJson("pod")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Pod createPod(@QueryParam("name") String name, @QueryParam("zoneid") String zoneId, @QueryParam("startip") String startIp, @QueryParam("gateway") String gateway, @QueryParam("netmask") String netmask, CreatePodOptions... createPodOptions);

   /**
    * Deletes a Pod.
    * @param id the ID of the Pod
    */
   @Named("deletePod")
   @GET
   @QueryParams(keys = "command", values = "deletePod")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(VoidOnNotFoundOr404.class)
   void deletePod(@QueryParam("id") String id);

   /**
    * Updates a Pod.
    * @param id the ID of the Pod
    * @param updatePodOptions optional arguments
    * @return the updated pod
    */
   @Named("updatePod")
   @GET
   @QueryParams(keys = "command", values = "updatePod")
   @SelectJson("pod")
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   Pod updatePod(@QueryParam("id") String id, UpdatePodOptions... updatePodOptions);

}
