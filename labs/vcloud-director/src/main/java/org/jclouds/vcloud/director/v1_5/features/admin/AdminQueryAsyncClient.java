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

import java.util.concurrent.TimeUnit;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.jclouds.concurrent.Timeout;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.JAXBResponseParser;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.vcloud.director.v1_5.domain.RoleReferences;
import org.jclouds.vcloud.director.v1_5.domain.query.QueryResultRecords;
import org.jclouds.vcloud.director.v1_5.features.QueryAsyncClient;
import org.jclouds.vcloud.director.v1_5.filters.AddVCloudAuthorizationToRequest;
import org.jclouds.vcloud.director.v1_5.functions.ThrowVCloudErrorOn4xx;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to {@link AdminQuery} objects.
 * 
 * @see AdminQueryClient
 * @author Aled Sage
 */
@RequestFilters(AddVCloudAuthorizationToRequest.class)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
public interface AdminQueryAsyncClient extends QueryAsyncClient {
   
   @GET
   @Path("/admin/groups/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> groupsQueryAll();

   @GET
   @Path("/admin/groups/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> groupsQuery(@QueryParam("filter") String filter);

   @GET
   @Path("/admin/orgs/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> orgsQueryAll();

   @GET
   @Path("/admin/orgs/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> orgsQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/rights/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> rightsQueryAll();

   @GET
   @Path("/admin/rights/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> rightsQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> rolesQueryAll();

   @GET
   @Path("/admin/roles/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> rolesQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/roles/query")
   @Consumes
   @QueryParams(keys = { "format" }, values = { "references" })
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<RoleReferences> roleReferencesQueryAll();
   
   @GET
   @Path("/admin/strandedUsers/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> strandedUsersQueryAll();

   @GET
   @Path("/admin/strandedUsers/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> strandedUsersQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/users/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> usersQueryAll();

   @GET
   @Path("/admin/users/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> usersQuery(@QueryParam("filter") String filter);
   
   @GET
   @Path("/admin/vdcs/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> vdcsQueryAll();

   @GET
   @Path("/admin/vdcs/query")
   @Consumes
   @JAXBResponseParser
   @ExceptionParser(ThrowVCloudErrorOn4xx.class)
   ListenableFuture<QueryResultRecords> vdcsQuery(@QueryParam("filter") String filter);
}
