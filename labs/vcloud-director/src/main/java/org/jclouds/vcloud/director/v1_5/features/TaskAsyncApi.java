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
package org.jclouds.vcloud.director.v1_5.features;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.Task;
import org.jclouds.vcloud.director.v1_5.domain.TasksList;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see TaskApi
 * @author grkvlt@apache.org, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface TaskAsyncApi {
   
   /**
    * @see TaskApi#getTasksList(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<TasksList> getTasksList(@EndpointParam URI tasksListHref);

   /**
    * @see TaskApi#get(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> get(@EndpointParam(parser = URNToHref.class) String taskUrn);
   
   /**
    * @see TaskApi#get(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Task> get(@EndpointParam URI taskURI);
   
   /**
    * @see TaskApi#cancel(String)
    */
   @POST
   @Path("/action/cancel")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> cancel(@EndpointParam(parser = URNToHref.class) String taskUrn);
   
   /**
    * @see TaskApi#cancel(URI)
    */
   @POST
   @Path("/action/cancel")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> cancel(@EndpointParam URI taskURI);
}
