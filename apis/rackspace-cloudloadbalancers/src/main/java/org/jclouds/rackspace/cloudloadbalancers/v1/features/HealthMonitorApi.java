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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;

import org.jclouds.Fallbacks.FalseOnNotFoundOr422;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.rackspace.cloudloadbalancers.v1.domain.HealthMonitor;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.SelectJson;
import org.jclouds.rest.annotations.WrapWith;

/**
 * The load balancing service includes a health monitoring operation which periodically checks your back-end nodes to 
 * ensure they are responding correctly. If a node is not responding, it is removed from rotation until the health 
 * monitor determines that the node is functional. In addition to being performed periodically, the health check also 
 * is performed against every node that is added to ensure that the node is operating properly before allowing it to 
 * service traffic. Only one health monitor is allowed to be enabled on a load balancer at a time.
 * </p>
 * As part of your strategy for monitoring connections, you should consider defining secondary nodes that provide 
 * failover for effectively routing traffic in case the primary node fails. This is an additional feature that will 
 * ensure you remain up in case your primary node fails.
 * <p/>
 * @author Everett Toews
 */
@RequestFilters(AuthenticateRequest.class)
public interface HealthMonitorApi {
   /**
    * Create or update a health monitor.
    */
   @Named("healthmonitor:create")
   @PUT
   @Consumes(MediaType.APPLICATION_JSON) 
   @Fallback(VoidOnNotFoundOr404.class)
   @Path("/healthmonitor")
   void createOrUpdate(@WrapWith("healthMonitor") HealthMonitor healthMonitor);

   /**
    * Get health monitor.
    */
   @Named("healthmonitor:get")
   @GET
   @Consumes(MediaType.APPLICATION_JSON)
   @SelectJson("healthMonitor")
   @Fallback(NullOnNotFoundOr404.class)
   @Path("/healthmonitor")
   HealthMonitor get();
   
   /**
    * Delete health monitor.
    * 
    * @return true on a successful delete, false if the health monitor was not found
    */
   @Named("healthmonitor:delete")
   @DELETE
   @Fallback(FalseOnNotFoundOr422.class)
   @Path("/healthmonitor")
   @Consumes("*/*")
   boolean delete();
}
