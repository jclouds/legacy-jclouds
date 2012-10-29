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
package org.jclouds.cloudloadbalancers.features;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.concurrent.Timeout;

/**
 * Provides synchronous access to CloudLoadBalancers LoadBalancer features.
 * <p/>
 * 
 * @see LoadBalancerAsyncClient
 * @see <a
 *      href="http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch04s01.html"
 *      />
 * @author Adrian Cole
 */
@Timeout(duration = 60, timeUnit = TimeUnit.SECONDS)
public interface LoadBalancerClient {
   /**
    * Create a new load balancer with the configuration defined by the request.
    * 
    * <p/>
    * This operation asynchronously provisions a new load balancer based on the configuration
    * defined in the request object. Once the request is validated and progress has started on the
    * provisioning process, a response object will be returned.
    * 
    * 
    * @param lb
    *           configuration to create
    * @return The object will contain a unique identifier and status of the request. Using the
    *         identifier, the caller can check on the progress of the operation by performing a
    *         {@link LoadBalancerClient#getLoadBalancer}.
    * @throws HttpResponseException
    *            If the corresponding request cannot be fulfilled due to insufficient or invalid
    *            data
    * 
    */
   LoadBalancer createLoadBalancer(LoadBalancerRequest lb);

   /**
    * 
    * Update the properties of a load balancer.
    * 
    * <p/>
    * This operation asynchronously updates the attributes of the specified load balancer. Upon
    * successful validation of the request, the service will return a 202 (Accepted) response code.
    * A caller can poll the load balancer with its ID to wait for the changes to be applied and the
    * load balancer to return to an ACTIVE status.
    * 
    * @param id
    *           id of the loadbalancer to change
    * @param attrs
    *           what to change
    * @return The object will contain a unique identifier and status of the request. Using the
    *         identifier, the caller can check on the progress of the operation by performing a
    *         {@link LoadBalancerClient#getLoadBalancer}.
    * @see LoadBalancerAttributes#fromLoadBalancer
    */
   void updateLoadBalancerAttributes(int id, LoadBalancerAttributes attrs);

   /**
    * 
    * @return all load balancers configured for the account, or empty set if none available
    */
   Set<LoadBalancer> listLoadBalancers();

   /**
    * 
    * 
    * @param id
    *           id of the loadbalancer to retrieve
    * @return details of the specified load balancer, or null if not found
    */
   LoadBalancer getLoadBalancer(int id);

   /**
    * Remove a load balancer from the account.
    * <p/>
    * The remove load balancer function removes the specified load balancer and its associated
    * configuration from the account. Any and all configuration data is immediately purged and is
    * not recoverable.
    * 
    * @param id
    *           to remove
    */
   void removeLoadBalancer(int id);
}
