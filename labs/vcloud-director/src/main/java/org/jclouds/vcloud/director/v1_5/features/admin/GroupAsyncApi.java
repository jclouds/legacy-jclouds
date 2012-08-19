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
package org.jclouds.vcloud.director.v1_5.features.admin;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.Group;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

import com.google.common.util.concurrent.ListenableFuture;
   
/**
 * @see GroupApi
 * @author danikov
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface GroupAsyncApi {
   
   @POST
   @Path("/groups")
   @Consumes(VCloudDirectorMediaType.GROUP)
   @Produces(VCloudDirectorMediaType.GROUP)
   @JAXBResponseParser
   ListenableFuture<Group> addGroup(@EndpointParam URI adminOrgUri, 
         @BinderParam(BindToXMLPayload.class) Group group);

   /**
    * @see GroupApi#getGroup(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Group> getGroup(@EndpointParam URI groupUri);

   /**
    * @see GroupApi#editGroup(URI, Group)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.GROUP)
   @Produces(VCloudDirectorMediaType.GROUP)
   @JAXBResponseParser
   ListenableFuture<Group> editGroup(@EndpointParam URI groupRef, 
         @BinderParam(BindToXMLPayload.class) Group group);

   /**
    * @see GroupApi#removeGroup(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> removeGroup(@EndpointParam URI groupRef);
}
