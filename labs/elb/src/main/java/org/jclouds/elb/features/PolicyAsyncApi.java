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

import java.util.Set;

import javax.inject.Named;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.jclouds.Fallbacks.EmptySetOnNotFoundOr404;
import org.jclouds.Fallbacks.NullOnNotFoundOr404;
import org.jclouds.aws.filters.FormSigner;
import org.jclouds.elb.binders.BindPolicyTypeNamesToIndexedFormParams;
import org.jclouds.elb.domain.Policy;
import org.jclouds.elb.domain.PolicyType;
import org.jclouds.elb.options.ListPoliciesOptions;
import org.jclouds.elb.xml.DescribeLoadBalancerPoliciesResultHandler;
import org.jclouds.elb.xml.DescribeLoadBalancerPolicyTypesResultHandler;
import org.jclouds.elb.xml.PolicyHandler;
import org.jclouds.elb.xml.PolicyTypeHandler;
import org.jclouds.rest.annotations.BinderParam;
import org.jclouds.rest.annotations.Fallback;
import org.jclouds.rest.annotations.FormParams;
import org.jclouds.rest.annotations.RequestFilters;
import org.jclouds.rest.annotations.VirtualHost;
import org.jclouds.rest.annotations.XMLResponseParser;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" >doc</a>
 * @see PolicyApi
 * @author Adrian Cole
 */
@RequestFilters(FormSigner.class)
@VirtualHost
public interface PolicyAsyncApi {
   
   /**
    * @see PolicyApi#get()
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<Policy> get(@FormParam("PolicyNames.member.1") String name);
   
   /**
    * @see PolicyApi#list()
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   ListenableFuture<Set<Policy>> list();

   /**
    * @see PolicyApi#list(ListPoliciesOptions)
    */
   @Named("DescribeLoadBalancerPolicies")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPoliciesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicies")
   ListenableFuture<Set<Policy>> list(ListPoliciesOptions options);

   
   /**
    * @see PolicyApi#getType()
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(PolicyTypeHandler.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   @Fallback(NullOnNotFoundOr404.class)
   ListenableFuture<PolicyType> getType(@FormParam("PolicyTypeNames.member.1") String name);
   
   /**
    * @see PolicyApi#listTypes()
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPolicyTypesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   ListenableFuture<Set<PolicyType>> listTypes();

   /**
    * @see PolicyApi#listTypes(Iterable<String>)
    */
   @Named("DescribeLoadBalancerPolicyTypes")
   @POST
   @Path("/")
   @XMLResponseParser(DescribeLoadBalancerPolicyTypesResultHandler.class)
   @Fallback(EmptySetOnNotFoundOr404.class)
   @FormParams(keys = "Action", values = "DescribeLoadBalancerPolicyTypes")
   ListenableFuture<Set<PolicyType>> listTypes(@BinderParam(BindPolicyTypeNamesToIndexedFormParams.class) Iterable<String> names);
   
}
