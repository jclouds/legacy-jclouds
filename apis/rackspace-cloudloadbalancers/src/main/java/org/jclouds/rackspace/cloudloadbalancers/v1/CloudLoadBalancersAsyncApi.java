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
package org.jclouds.rackspace.cloudloadbalancers.v1;

import java.io.Closeable;
import java.util.Set;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.jclouds.javax.annotation.Nullable;
import org.jclouds.location.Zone;
import org.jclouds.location.functions.ZoneToEndpoint;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.AccessRuleAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ConnectionAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ContentCachingAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ErrorPageAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.HealthMonitorAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.LoadBalancerAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.NodeAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.ReportAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SSLTerminationAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.SessionPersistenceAsyncApi;
import org.jclouds.rackspace.cloudloadbalancers.v1.features.VirtualIPAsyncApi;
import org.jclouds.rest.annotations.Delegate;
import org.jclouds.rest.annotations.EndpointParam;

import com.google.inject.Provides;

/**
 * Provides asynchronous access to Rackspace Cloud Load Balancers.
 * <p/>
 * 
 * @see CloudLoadBalancersApi
 * @author Adrian Cole
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudLoadBalancersApi.class)} as
 *             {@link CloudLoadBalancersAsyncApi} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudLoadBalancersAsyncApi extends Closeable {
   /**
    * @return the Zone codes configured
    */
   @Provides
   @Zone
   Set<String> getConfiguredZones();

   /**
    * Provides asynchronous access to LoadBalancer features.
    */
   @Delegate
   LoadBalancerAsyncApi getLoadBalancerApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
   
   /**
    * Provides asynchronous access to Node features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   NodeAsyncApi getNodeApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Access Rule features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   AccessRuleAsyncApi getAccessRuleApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Virtual IP features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   VirtualIPAsyncApi getVirtualIPApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Connection features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ConnectionAsyncApi getConnectionApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Health Monitor features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   HealthMonitorAsyncApi getHealthMonitorApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Session Persistence features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SessionPersistenceAsyncApi getSessionPersistenceApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Content Caching features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ContentCachingAsyncApi getContentCachingApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to SSL Termination features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   SSLTerminationAsyncApi getSSLTerminationApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Error Page features.
    */
   @Delegate
   @Path("/loadbalancers/{lbId}")
   ErrorPageAsyncApi getErrorPageApiForZoneAndLoadBalancer(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone, @PathParam("lbId") int lbId);

   /**
    * Provides asynchronous access to Report features.
    */
   @Delegate
   ReportAsyncApi getReportApiForZone(
         @EndpointParam(parser = ZoneToEndpoint.class) @Nullable String zone);
}
