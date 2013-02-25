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
import org.jclouds.iam.domain.Role;
import org.jclouds.iam.functions.RolesToPagedIterable;
import org.jclouds.iam.xml.ListRolesResultHandler;
import org.jclouds.iam.xml.RoleHandler;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;
import org.jclouds.rest.functions.ReturnVoidOnNotFoundOr404;

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
public interface RoleAsyncApi {
   /**
    * @see RoleApi#createWithPolicy
    */
   @Named("CreateRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateRole")
   @XMLResponseParser(RoleHandler.class)
   ListenableFuture<Role> createWithPolicy(@FormParam("RoleName") String name,
         @FormParam("AssumeRolePolicyDocument") String assumeRolePolicy);

   /**
    * @see RoleApi#createWithPolicyAndPath
    */
   @Named("CreateRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateRole")
   @XMLResponseParser(RoleHandler.class)
   ListenableFuture<Role> createWithPolicyAndPath(@FormParam("RoleName") String name,
         @FormParam("AssumeRolePolicyDocument") String assumeRolePolicy, @FormParam("Path") String path);

   /**
    * @see RoleApi#list()
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   @Transform(RolesToPagedIterable.class)
   ListenableFuture<PagedIterable<Role>> list();

   /**
    * @see RoleApi#listFirstPage
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   ListenableFuture<IterableWithMarker<Role>> listFirstPage();

   /**
    * @see RoleApi#listAt(String)
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   ListenableFuture<IterableWithMarker<Role>> listAt(@FormParam("Marker") String marker);

   /**
    * @see RoleApi#listPathPrefix(String)
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   @Transform(RolesToPagedIterable.class)
   ListenableFuture<PagedIterable<Role>> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * @see RoleApi#listPathPrefixFirstPage(String)
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   ListenableFuture<IterableWithMarker<Role>> listPathPrefixFirstPage(@FormParam("PathPrefix") String pathPrefix);

   /**
    * @see RoleApi#listPathPrefixAt(String, String)
    */
   @Named("ListRoles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRoles")
   @XMLResponseParser(ListRolesResultHandler.class)
   ListenableFuture<IterableWithMarker<Role>> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * @see RoleApi#get()
    */
   @Named("GetRole")
   @POST
   @Path("/")
   @XMLResponseParser(RoleHandler.class)
   @FormParams(keys = "Action", values = "GetRole")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Role> get(@FormParam("RoleName") String name);

   /**
    * @see RoleApi#delete()
    */
   @Named("DeleteRole")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteRole")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@FormParam("RoleName") String name);
}
