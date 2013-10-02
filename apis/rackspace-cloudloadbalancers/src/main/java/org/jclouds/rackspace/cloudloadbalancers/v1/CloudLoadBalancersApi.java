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
package org.jclouds.rackspace.cloudloadbalancers.v1;

import java.io.Closeable;
import java.util.Set;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.AccessRuleApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ConnectionApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ContentCachingApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.NodeApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ReportApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SessionPersistenceApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.VirtualIPApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides access to Rackspace Cloud Load Balancers.
 * <p/>
 * 
 * @author Everett Toews
 */
public interface CloudLoadBalancersApi extends Closeable {
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides access to Load Balancer features.
    */
   @Delegate
   LoadBalancerApi getLoadBalancerApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
   
   /**
    * Provides access to Node features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   NodeApi getNodeApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Access Rule features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   AccessRuleApi getAccessRuleApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Virtual IP features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   VirtualIPApi getVirtualIPApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Connection features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ConnectionApi getConnectionApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Health Monitor features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   HealthMonitorApi getHealthMonitorApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Session Persistence features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SessionPersistenceApi getSessionPersistenceApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Content Caching features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ContentCachingApi getContentCachingApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to SSL Termination features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SSLTerminationApi getSSLTerminationApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Error Page features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ErrorPageApi getErrorPageApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides access to Report features.
    */
   @Delegate
   ReportApi getReportApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
