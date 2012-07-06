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

import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindInstanceIdsToIndexedFormParams;
import org.jclouds.elb.domain.InstanceState;
import org.jclouds.elb.xml.DescribeLoadBalancerPoliciesResultHandler;
import org.jclouds.elb.xml.InstancesResultHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnEmptySetOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference"
 *      >doc</a>
 * @see InstanceClient
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface InstanceAsyncClient {

   /**
    * @see InstanceClient#listInstanceStatesOfLoadBalancer(String)
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeInstanceHealth")
   ListenableFuture<Set<InstanceState>> listInstanceStatesOfLoadBalancer(
            @FormParam("LoadBalancerName") String loadBalancerName);

   /**
    * @see InstanceClient#listInstanceStatesOfLoadBalancer(Iterable<String>, String)
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @ExceptionParser(ReturnEmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeInstanceHealth")
   ListenableFuture<Set<InstanceState>> listInstanceStatesOfLoadBalancer(
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) Iterable<String> instanceIds,
            @FormParam("LoadBalancerName") String loadBalancerName);


   /**
    * @see InstanceClient#registerInstancesWithLoadBalancer
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstancesResultHandler.class)
   @FormParams(keys = ACTION, values = "RegisterInstancesWithLoadBalancer")
   ListenableFuture<Set<String>> registerInstancesWithLoadBalancer(
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) Iterable<String> instanceIds,
            @FormParam("LoadBalancerName") String loadBalancerName);

   /**
    * @see InstanceClient#deregisterInstancesFromLoadBalancer
    */
   @POST
   @Path("/")
   @XMLResponseParser(InstancesResultHandler.class)
   @FormParams(keys = ACTION, values = "DeregisterInstancesFromLoadBalancer")
   ListenableFuture<Set<String>> deregisterInstancesFromLoadBalancer(
            @BinderParam(BindInstanceIdsToIndexedFormParams.class) Iterable<String> instanceIds,
            @FormParam("LoadBalancerName") String loadBalancerName);

   
}
