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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.binders.BindToXMLPayload;
import org.jclouds.vcloud.director.v1_5.VCloudDirectorMediaType;
import org.jclouds.vcloud.director.v1_5.domain.User;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;
import org.jclouds.vcloud.director.v1_5.functions.URNToAdminHref;
import org.jclouds.vcloud.director.v1_5.functions.URNToHref;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @see UserApi
 * @author danikov, Adrian Cole
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface UserAsyncApi {

   /**
    * @see UserApi#addUserToOrg(User, String)
    */
   @POST
   @Path("/users")
   @Consumes(VCloudDirectorMediaType.USER)
   @Produces(VCloudDirectorMediaType.USER)
   @JAXBResponseParser
   ListenableFuture<User> addUserToOrg(@BinderParam(BindToXMLPayload.class) User user,
            @EndpointParam(parser = URNToAdminHref.class) String orgUrn);

   /**
    * @see UserApi#addUserToOrg(User, URI)
    */
   @POST
   @Path("/users")
   @Consumes(VCloudDirectorMediaType.USER)
   @Produces(VCloudDirectorMediaType.USER)
   @JAXBResponseParser
   ListenableFuture<User> addUserToOrg(@BinderParam(BindToXMLPayload.class) User user,
            @EndpointParam URI orgAdminHref);

   /**
    * @see UserApi#get(String)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<User> get(@EndpointParam(parser = URNToHref.class) String userUrn);

   /**
    * @see UserApi#get(URI)
    */
   @GET
   @Consumes
   @JAXBResponseParser
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<User> get(@EndpointParam URI userHref);

   /**
    * @see UserApi#edit(String, User)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.USER)
   @Produces(VCloudDirectorMediaType.USER)
   @JAXBResponseParser
   ListenableFuture<User> edit(@EndpointParam(parser = URNToHref.class) String userUrn,
            @BinderParam(BindToXMLPayload.class) User user);

   /**
    * @see UserApi#edit(URI, User)
    */
   @PUT
   @Consumes(VCloudDirectorMediaType.USER)
   @Produces(VCloudDirectorMediaType.USER)
   @JAXBResponseParser
   ListenableFuture<User> edit(@EndpointParam URI userHref, @BinderParam(BindToXMLPayload.class) User user);

   /**
    * @see UserApi#remove(String)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> remove(@EndpointParam(parser = URNToHref.class) String userUrn);

   /**
    * @see UserApi#remove(URI)
    */
   @DELETE
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> remove(@EndpointParam URI userHref);

   /**
    * @see UserApi#unlock(String)
    */
   @POST
   @Path("/action/unlock")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> unlock(@EndpointParam(parser = URNToHref.class) String userUrn);

   /**
    * @see UserApi#unlock(URI)
    */
   @POST
   @Path("/action/unlock")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<Void> unlock(@EndpointParam URI userHref);
}
