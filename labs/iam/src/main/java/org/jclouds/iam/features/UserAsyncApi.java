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
package org.jclouds.iam.features;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.functions.UsersToPagedIterable;
import org.jclouds.iam.xml.ListUsersResultHandler;
import org.jclouds.iam.xml.UserHandler;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon IAM via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/IAM/latest/APIReference" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface UserAsyncApi {

   /**
    * @see UserApi#list()
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   @Transform(UsersToPagedIterable.class)
   ListenableFuture<PagedIterable<User>> list();

   /**
    * @see UserApi#listFirstPage
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   ListenableFuture<IterableWithMarker<User>> listFirstPage();

   /**
    * @see UserApi#listAt(String)
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   ListenableFuture<IterableWithMarker<User>> listAt(@FormParam("Marker") String marker);

   /**
    * @see UserApi#listPathPrefix(String)
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   @Transform(UsersToPagedIterable.class)
   ListenableFuture<PagedIterable<User>> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * @see UserApi#listPathPrefixFirstPage(String)
    */
   @Named("iam:ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   ListenableFuture<IterableWithMarker<User>> listPathPrefixFirstPage(@FormParam("PathPrefix") String pathPrefix);

   /**
    * @see UserApi#listPathPrefixAt(String, String)
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListUsers")
   @XMLResponseParser(ListUsersResultHandler.class)
   ListenableFuture<IterableWithMarker<User>> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * @see UserApi#getCurrent()
    */
   @Named("GetUser")
   @POST
   @Path("/")
   @XMLResponseParser(UserHandler.class)
   @FormParams(keys = "Action", values = "GetUser")
   ListenableFuture<User> getCurrent();

   /**
    * @see UserApi#get()
    */
   @Named("GetUser")
   @POST
   @Path("/")
   @XMLResponseParser(UserHandler.class)
   @FormParams(keys = "Action", values = "GetUser")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<User> get(@FormParam("UserName") String name);
}
