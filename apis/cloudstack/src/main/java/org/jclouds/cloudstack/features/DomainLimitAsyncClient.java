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
package org.jclouds.cloudstack.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.jclouds.cloudstack.binders.ResourceLimitToQueryParams;
import org.jclouds.cloudstack.domain.ResourceLimit;
import org.jclouds.cloudstack.filters.AuthenticationFilter;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides asynchronous access to CloudStack Limit features available to Domain
 * Admin users.
 * 
 * @author Adrian Cole
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_Domain_Admin.html"
 *      />
 */
@RequestFilters(AuthenticationFilter.class)
@QueryParams(keys = "response", values = "json")
public interface DomainLimitAsyncClient extends LimitAsyncClient {
   /**
    * @see DomainLimitClient#updateResourceLimit
    */
   @Named("updateResourceLimit")
   @GET
   @QueryParams(keys = "command", values = "updateResourceLimit")
   @SelectJson("resourcelimit")
   @Consumes(MediaType.APPLICATION_JSON)
   ListenableFuture<ResourceLimit> updateResourceLimit(
         @BinderParam(ResourceLimitToQueryParams.class) ResourceLimit limit);

}
