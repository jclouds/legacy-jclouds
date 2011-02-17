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

package org.jclouds.elb;

import static org.jclouds.aws.reference.FormParameters.ACTION;
import static org.jclouds.aws.reference.FormParameters.VERSION;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindAvailabilityZonesToIndexedFormParams;
import org.jclouds.elb.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.elb.binders.BindLoadBalancerNamesToIndexedFormParams;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.xml.CreateLoadBalancerResponseHandler;
import org.jclouds.elb.xml.DescribeLoadBalancersResponseHandler;
import org.jclouds.elb.xml.RegisterInstancesWithLoadBalancerResponseHandler;
import org.jclouds.location.functions.RegionToEndpointOrProviderIfNull;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.annotations.Beta;
import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Elastic Load Balancer via REST API.
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference/">ELB
 *      documentation</a>
 * @author Lili Nader
 */
@Beta
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = ELBAsyncClient.VERSION)
@VirtualHost
public interface ELBAsyncClient {
   public static final String VERSION = "2010-07-01";

   // TODO: there are a lot of missing methods

   /**
    * @see ELBClient#createLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   @Beta
   // TODO:The way this handles arguments needs to be refactored. it needs to deal with collections
   // of listeners.
   ListenableFuture<String> createLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name, @FormParam("Listeners.member.1.Protocol") String protocol,
            @FormParam("Listeners.member.1.LoadBalancerPort") int loadBalancerPort,
            @FormParam("Listeners.member.1.InstancePort") int instancePort,
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) String... availabilityZones);

   /**
    * @see ELBClient#deleteLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteLoadBalancer")
   ListenableFuture<Void> deleteLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name);

   /**
    * @see ELBClient#registerInstancesWithLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(RegisterInstancesWithLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "RegisterInstancesWithLoadBalancer")
   ListenableFuture<Set<String>> registerInstancesWithLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see ELBClient#deregisterInstancesWithLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterInstancesFromLoadBalancer")
   ListenableFuture<Void> deregisterInstancesWithLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name,
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see ELBClient#describeLoadBalancersInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResponseHandler.class)
   @FormParams(keys = ACTION, values = "DescribeLoadBalancers")
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   ListenableFuture<Set<? extends LoadBalancer>> describeLoadBalancersInRegion(
            @EndpointParam(parser = RegionToEndpointOrProviderIfNull.class) @Nullable String region,
            @BinderParam(BindLoadBalancerNamesToIndexedFormParams.class) String... loadbalancerNames);

}
