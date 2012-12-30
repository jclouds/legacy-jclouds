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

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncApi;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationAndCookieToRequest;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to {@link AdminQuery} objects.
 * 
 * @see AdminQueryApi
 * @author Aled Sage
 */
@RequestFilters(AddVCloudAuthorizationAndCookieToRequest.class)
public interface AdminQueryAsyncApi extends QueryAsyncApi {
   
   @GET
   @Path("/admin/groups/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> groupsQueryAll();

   @GET
   @Path("/admin/groups/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> groupsQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/admin/orgs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> orgsQueryAll();

   @GET
   @Path("/admin/orgs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> orgsQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/rights/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> rightsQueryAll();

   @GET
   @Path("/admin/rights/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> rightsQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> rolesQueryAll();

   @GET
   @Path("/admin/roles/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> rolesQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   ListenableFuture<RoleReferences> roleReferencesQueryAll();
   
   @GET
   @Path("/admin/strandedUsers/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> strandedUsersQueryAll();

   @GET
   @Path("/admin/strandedUsers/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> strandedUsersQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/users/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> usersQueryAll();

   @GET
   @Path("/admin/users/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> usersQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/vdcs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vdcsQueryAll();

   @GET
   @Path("/admin/vdcs/query")
   @Consumes
   @JAXBResponseParser
   ListenableFuture<QueryResultRecords> vdcsQuery(@QueryParam("filter") String filter);
}
