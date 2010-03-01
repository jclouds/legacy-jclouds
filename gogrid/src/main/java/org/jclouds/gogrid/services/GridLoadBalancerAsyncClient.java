/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.gogrid.services;

import com.google.common.util.concurrent.ListenableFuture;
import org.jclouds.gogrid.GoGrid;
import org.jclouds.gogrid.domain.LoadBalancer;
import org.jclouds.gogrid.filters.SharedKeyLiteAuthentication;
import org.jclouds.gogrid.functions.ParseLoadBalancerListFromJsonResponse;
import org.jclouds.rest.annotations.Endpoint;
import org.jclouds.rest.annotations.QueryParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.ResponseParser;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.util.Set;

import static org.jclouds.gogrid.reference.GoGridHeaders.VERSION;

/**
 * @author Oleksiy Yarmula
 */
@Endpoint(GoGrid.class)
@RequestFilters(SharedKeyLiteAuthentication.class)
@QueryParams(keys = VERSION, values = "1.4")
public interface GridLoadBalancerAsyncClient {

    /**
    * @see GridJobClient#getJobList(org.jclouds.gogrid.options.GetJobListOptions...)
    */
    @GET
    @ResponseParser(ParseLoadBalancerListFromJsonResponse.class)
    @Path("/grid/loadbalancer/list")
    ListenableFuture<Set<LoadBalancer>> getLoadBalancerList();

}
