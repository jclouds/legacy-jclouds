/**
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
import org.jclouds.iam.domain.Policy;
import org.jclouds.iam.functions.PoliciesToPagedIterable.RolePoliciesToPagedIterable;
import org.jclouds.iam.xml.ListPoliciesResultHandler;
import org.jclouds.iam.xml.PolicyHandler;
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
 * @see <a href="http://docs.aws.amazon.com/IAM/latest/APIReference/API_ListRolePolicies.html" />
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface RolePolicyAsyncApi {
   /**
    * @see RolePolicyApi#create
    */
   @Named("PutRolePolicy")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "PutRolePolicy")
   ListenableFuture<Void> create(@FormParam("PolicyName") String name, @FormParam("PolicyDocument") String document);

   /**
    * @see RolePolicyApi#list()
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   @Transform(RolePoliciesToPagedIterable.class)
   ListenableFuture<PagedIterable<String>> list();

   /**
    * @see RolePolicyApi#listFirstPage
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   ListenableFuture<IterableWithMarker<String>> listFirstPage();

   /**
    * @see RolePolicyApi#listAt(String)
    */
   @Named("ListRolePolicies")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "ListRolePolicies")
   @XMLResponseParser(ListPoliciesResultHandler.class)
   ListenableFuture<IterableWithMarker<String>> listAt(@FormParam("Marker") String marker);

   /**
    * @see RolePolicyApi#get()
    */
   @Named("GetRolePolicy")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyHandler.class)
   @FormParams(keys = "Action", values = "GetRolePolicy")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<Policy> get(@FormParam("PolicyName") String name);

   /**
    * @see RolePolicyApi#delete()
    */
   @Named("DeleteRolePolicy")
   @POST
   @Path("/")
   @FormParams(keys = "Action", values = "DeleteRolePolicy")
   @ExceptionParser(ReturnVoidOnNotFoundOr404.class)
   ListenableFuture<Void> delete(@FormParam("PolicyName") String name);
}
