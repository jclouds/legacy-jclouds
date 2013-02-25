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
import org.jclouds.iam.domain.InstanceProfile;
import org.jclouds.iam.functions.InstanceProfilesToPagedIterable;
import org.jclouds.iam.xml.InstanceProfileHandler;
import org.jclouds.iam.xml.ListInstanceProfilesResultHandler;
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
public interface InstanceProfileAsyncApi {
   /**
    * @see InstanceProfileApi#create
    */
   @Named("CreateInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateInstanceProfile")
   @XMLResponseParser(InstanceProfileHandler.class)
   ListenableFuture<InstanceProfile> create(@FormParam("InstanceProfileName") String name);

   /**
    * @see InstanceProfileApi#createWithPath
    */
   @Named("CreateInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "CreateInstanceProfile")
   @XMLResponseParser(InstanceProfileHandler.class)
   ListenableFuture<InstanceProfile> createWithPath(@FormParam("InstanceProfileName") String name,
         @FormParam("Path") String path);

   /**
    * @see InstanceProfileApi#list()
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   @Transform(InstanceProfilesToPagedIterable.class)
   ListenableFuture<PagedIterable<InstanceProfile>> list();

   /**
    * @see InstanceProfileApi#listFirstPage
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   ListenableFuture<IterableWithMarker<InstanceProfile>> listFirstPage();

   /**
    * @see InstanceProfileApi#listAt(String)
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   ListenableFuture<IterableWithMarker<InstanceProfile>> listAt(@FormParam("Marker") String marker);

   /**
    * @see InstanceProfileApi#listPathPrefix(String)
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   @Transform(InstanceProfilesToPagedIterable.class)
   ListenableFuture<PagedIterable<InstanceProfile>> listPathPrefix(@FormParam("PathPrefix") String pathPrefix);

   /**
    * @see InstanceProfileApi#listPathPrefixFirstPage(String)
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   ListenableFuture<IterableWithMarker<InstanceProfile>> listPathPrefixFirstPage(
         @FormParam("PathPrefix") String pathPrefix);

   /**
    * @see InstanceProfileApi#listPathPrefixAt(String, String)
    */
   @Named("ListInstanceProfiles")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListInstanceProfiles")
   @XMLResponseParser(ListInstanceProfilesResultHandler.class)
   ListenableFuture<IterableWithMarker<InstanceProfile>> listPathPrefixAt(@FormParam("PathPrefix") String pathPrefix,
         @FormParam("Marker") String marker);

   /**
    * @see InstanceProfileApi#get()
    */
   @Named("GetInstanceProfile")
   @POST
   @Path("/")
   @XMLResponseParser(InstanceProfileHandler.class)
   @FormParams(keys = "Action", values = "GetInstanceProfile")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<InstanceProfile> get(@FormParam("InstanceProfileName") String name);

   /**
    * @see InstanceProfileApi#delete()
    */
   @Named("DeleteInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteInstanceProfile")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@FormParam("InstanceProfileName") String name);

   /**
    * @see InstanceProfileApi#addRole()
    */
   @Named("AddRoleToInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "AddRoleToInstanceProfile")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> addRole(@FormParam("InstanceProfileName") String name, @FormParam("RoleName") String roleName);

   /**
    * @see InstanceProfileApi#removeRole()
    */
   @Named("RemoveRoleFromInstanceProfile")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "RemoveRoleFromInstanceProfile")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> removeRole(@FormParam("InstanceProfileName") String name,
         @FormParam("RoleName") String roleName);
}
