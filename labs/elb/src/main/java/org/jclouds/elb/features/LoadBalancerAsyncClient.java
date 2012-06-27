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

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.aws.filters.FormSigner;
import org.jclouds.collect.PaginatedSet;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.jclouds.elb.xml.DescribeLoadBalancersResultHandler;
import org.jclouds.elb.xml.LoadBalancerHandler;
import org.jclouds.rest.annotations.ExceptionParser;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;
import org.jclouds.rest.functions.ReturnNullOnNotFoundOr404;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" >doc</a>
 * @see LoadBalancerClient
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface LoadBalancerAsyncClient {
   
   /**
    * @see LoadBalancerClient#get()
    */
   @POST
   @Path("/")
   @XMLResponseParser(LoadBalancerHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   @ExceptionParser(ReturnNullOnNotFoundOr404.class)
   ListenableFuture<LoadBalancer> get(@FormParam("LoadBalancerNames.member.1") String name);
   
   /**
    * @see LoadBalancerClient#list()
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   ListenableFuture<PaginatedSet<LoadBalancer>> list();

   /**
    * @see LoadBalancerClient#list(ListLoadBalancersOptions)
    */
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancersResultHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancers")
   ListenableFuture<PaginatedSet<LoadBalancer>> list(ListLoadBalancersOptions options);

}
