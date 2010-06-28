/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.elb;

import static org.jclouds.aws.ec2.reference.EC2Parameters.ACTION;
import static org.jclouds.aws.ec2.reference.EC2Parameters.VERSION;

import java.util.Set;

import javax.annotation.Nullable;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.ec2.xml.CreateLoadBalancerResponseHandler;
import org.jclouds.aws.ec2.xml.RegisterInstancesWithLoadBalancerResponseHandler;
import org.jclouds.aws.elb.binders.BindELBInstanceIdsToIndexedFormParams;
import org.jclouds.aws.elb.domain.LoadBalancer;
import org.jclouds.aws.elb.xml.DescribeLoadBalancersResponseHandler;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.aws.functions.RegionToEndpoint;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.EndpointParam;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to EC2 Elastic Load Balancer via REST API.
 * <p/>
 * 
 * @author Lili Nader
 */
@RequestFilters(FormSigner.class)
@FormParams(keys = VERSION, values = ELBAsyncClient.VERSION)
@VirtualHost
public interface ELBAsyncClient {
   public static final String VERSION = "2009-11-25";

   /**
    * @see ELBClient#createLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   ListenableFuture<String> createLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name,
            @FormParam("Listeners.member.1.Protocol") String protocol,
            @FormParam("Listeners.member.1.LoadBalancerPort") int loadBalancerPort,
            @FormParam("Listeners.member.1.InstancePort") int instancePort,
            @FormParam("AvailabilityZones.member.1") String availabilityZone);

   /**
    * @see ELBClient#deleteLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeleteLoadBalancer")
   ListenableFuture<Void> deleteLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name);

   /**
    * @see ELBClient#registerInstancesWithLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(RegisterInstancesWithLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "RegisterInstancesWithLoadBalancer")
   ListenableFuture<? extends Set<String>> registerInstancesWithLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name,
            @BinderParam(BindELBInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see ELBClient#deregisterInstancesWithLoadBalancerInRegion
    */
   @POST
   @Path("/")
   @FormParams(keys = ACTION, values = "DeregisterInstancesFromLoadBalancer")
   ListenableFuture<Void> deregisterInstancesWithLoadBalancerInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("LoadBalancerName") String name,
            @BinderParam(BindELBInstanceIdsToIndexedFormParams.class) String... instanceIds);

   /**
    * @see ELBClient#describeLoadBalancersInRegion
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResponseHandler.class)
   @FormParams(keys = ACTION, values = "DescribeLoadBalancers")
   ListenableFuture<? extends Set<LoadBalancer>> describeLoadBalancersInRegion(
            @EndpointParam(parser = RegionToEndpoint.class) @Nullable String region,
            @FormParam("LoadBalancerName") @Nullable String name);

}
