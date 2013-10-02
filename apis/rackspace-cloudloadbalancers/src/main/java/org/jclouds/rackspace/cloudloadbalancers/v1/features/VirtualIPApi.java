/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.rackspace.cloudloadbalancers.v1.features;

import javax.inject.Named;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.FalseOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIP;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.VirtualIPWithId;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.binders.BindToJsonPayload;

/**
 * A virtual IP makes a load balancer accessible by clients. The load balancing service supports either a public VIP,
 * routable on the public Internet, or a ServiceNet address, routable only within the region in which the load balancer 
 * resides.
 * <p/>
 * 
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface VirtualIPApi {
   /**
    * Create a new virtual IP.
    */
   @Named("virtualip:create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/virtualips")
   VirtualIPWithId create(@BinderParam(BindToJsonPayload.class) VirtualIP virtualIP);

   /**
    * List the virtual IPs.
    */
   @Named("virtualip:list")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @SelectJson("virtualIps")
   @Path("/virtualips")
   Iterable<VirtualIPWithId> list();
   
   /**
    * Delete a virtual IP.
    * 
    * @see VirtualIPApi#delete(Iterable)
    * 
    * @return true on a successful delete, false if the virtual IP was not found
    */
   @Named("virtualip:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/virtualips/{id}")
   @Consumes("*/*")
   boolean delete(@PathParam("id") int id);
   
   /**
    * Batch delete virtual IPs given the specified ids.
    * 
    * All load balancers must have at least one virtual IP associated with them at all times. Attempting to delete the
    * last virtual IP will result in an exception. The current default limit is ten ids per request. Any 
    * and all configuration data is immediately purged and is not recoverable. If one or more of the items in the list 
    * cannot be removed due to its current status, an exception is thrown along with the ids of the ones the 
    * system identified as potential failures for this request.
    * 
    * @return true on a successful delete, false if the virtual IP was not found
    */
   @Named("virtualip:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr404.class)
   @Path("/virtualips")
   @Consumes("*/*")
   boolean delete(@QueryParam("id") Iterable<Integer> ids);
}
