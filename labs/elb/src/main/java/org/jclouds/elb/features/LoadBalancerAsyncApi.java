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
package org.jclouds.elb.features;

import static org.jclouds.aws.reference.FormParameters.ACTION;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptyIterableWithMarkerOnNotFoundOr404;
import org.jclouds.Fallbacks.EmptyPagedIterableOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.Fallbacks.VoidOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.elb.binders.BindAvailabilityZonesToIndexedFormParams;
import org.jclouds.elb.binders.BindListenersToFormParams;
import org.jclouds.elb.binders.BindSecurityGroupsToIndexedFormParams;
import org.jclouds.elb.binders.BindSubnetsToIndexedFormParams;
import org.jclouds.elb.domain.Listener;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.functions.LoadBalancersToPagedIterable;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.jclouds.elb.xml.CreateLoadBalancerResponseHandler;
import org.jclouds.elb.xml.DescribeLoadBalancersResultHandler;
import org.jclouds.elb.xml.LoadBalancerHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.Transform;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"
 *      >doc</a>
 * @see LoadBalancerApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface LoadBalancerAsyncApi {
   /**
    * @see LoadBalancerApi#createListeningInAvailabilityZones()
    */
   @Named("CreateLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   ListenableFuture<String> createListeningInAvailabilityZones(@FormParam("LoadBalancerName") String name,
             @BinderParam(BindListenersToFormParams.class) Listener listeners,
             @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> availabilityZones);
    
   /**
    * @see LoadBalancerApi#createListeningInAvailabilityZones()
    */
   @Named("CreateLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   ListenableFuture<String> createListeningInAvailabilityZones(@FormParam("LoadBalancerName") String name,
            @BinderParam(BindListenersToFormParams.class) Iterable<Listener> listeners,
            @BinderParam(BindAvailabilityZonesToIndexedFormParams.class) Iterable<String> availabilityZones);

   /**
    * @see LoadBalancerApi#createListeningInSubnetAssignedToSecurityGroups()
    */
   @Named("CreateLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   ListenableFuture<String> createListeningInSubnetAssignedToSecurityGroups(
            @FormParam("LoadBalancerName") String name,
            @FormParam("Subnets.member.1") String subnetId,
            @BinderParam(BindSecurityGroupsToIndexedFormParams.class) Iterable<String> securityGroupIds);
   
   /**
    * @see LoadBalancerApi#createListeningInSubnetsAssignedToSecurityGroups()
    */
   @Named("CreateLoadBalancer")
   @POST
   @Path("/")
   @XMLResponseParser(CreateLoadBalancerResponseHandler.class)
   @FormParams(keys = ACTION, values = "CreateLoadBalancer")
   ListenableFuture<String> createListeningInSubnetsAssignedToSecurityGroups(
            @FormParam("LoadBalancerName") String name,
            @BinderParam(BindSubnetsToIndexedFormParams.class) Iterable<String> subnetIds,
            @BinderParam(BindSecurityGroupsToIndexedFormParams.class) Iterable<String> securityGroupIds);

   /**
    * @see LoadBalancerApi#get()
    */
   @Named("DescribeLoadBalancers")
   @POST
   @Path("/")
   @XMLResponseParser(LoadBalancerHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<LoadBalancer> get(@FormParam("LoadBalancerNames.member.1") String name);

   /**
    * @see LoadBalancerApi#list()
    */
   @Named("DescribeLoadBalancers")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResultHandler.class)
   @Transform(LoadBalancersToPagedIterable.class)
   @Fallback(EmptyPagedIterableOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   ListenableFuture<PagedIterable<LoadBalancer>> list();

   /**
    * @see LoadBalancerApi#list(ListLoadBalancersOptions)
    */
   @Named("DescribeLoadBalancers")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResultHandler.class)
   @Fallback(EmptyIterableWithMarkerOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   ListenableFuture<IterableWithMarker<LoadBalancer>> list(ListLoadBalancersOptions options);

   /**
    * @see LoadBalancerApi#delete()
    */
   @Named("DeleteLoadBalancer")
   @POST
   @Path("/")
   @Fallback(VoidOnNotFoundOr404.class)
   @FormParams(keys = ACTION, values = "DeleteLoadBalancer")
   ListenableFuture<Void> delete(@FormParam("LoadBalancerName") String name);
}
