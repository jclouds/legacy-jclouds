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

import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.iam.domain.User;
import org.jclouds.iam.functions.UsersToPagedIterable;
import org.jclouds.iam.options.ListUsersOptions;
import org.jclouds.iam.xml.ListUsersResultHandler;
import org.jclouds.iam.xml.UserHandler;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

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
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<User> get(@FormParam("UserName") String name);
   
   /**
    * @see UserApi#list()
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @XMLResponseParser(ListUsersResultHandler.class)
   @Transform(UsersToPagedIterable.class)
   @FormParams(keys = "Action", values = "ListUsers")
   ListenableFuture<PagedIterable<User>> list();

   /**
    * @see UserApi#list(ListUsersOptions)
    */
   @Named("ListUsers")
   @POST
   @Path("/")
   @XMLResponseParser(ListUsersResultHandler.class)
   @FormParams(keys = "Action", values = "ListUsers")
   ListenableFuture<IterableWithMarker<User>> list(ListUsersOptions options);

}
