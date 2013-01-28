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
package org.jclouds.rackspace.cloudloadbalancers.features;

import java.util.Map;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerRequest;
import org.jclouds.rackspace.cloudloadbalancers.domain.Metadata;

/**
 * Provides synchronous access to CloudLoadBalancers LoadBalancer features.
 * <p/>
 * 
 * @see LoadBalancerAsyncApi
 * @author Everett Toews
 */
public interface LoadBalancerApi {
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
    *         {@link LoadBalancerApi#getLoadBalancer}.
    * @throws HttpResponseException
    *            If the corresponding request cannot be fulfilled due to insufficient or invalid
    *            data
    * 
    */
   LoadBalancer create(LoadBalancerRequest lb);

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
    *         {@link LoadBalancerApi#getLoadBalancer}.
    * @see LoadBalancerAttributes#fromLoadBalancer
    */
   void update(int id, LoadBalancerAttributes attrs);

   /**
    * 
    * @return all load balancers configured for the account, or empty set if none available
    */
   PagedIterable<LoadBalancer> list();
   
   IterableWithMarker<LoadBalancer> list(PaginationOptions options);

   /**
    * Get a load balancer.
    * 
    * @param id
    *           id of the loadbalancer to retrieve
    * @return details of the specified load balancer, or null if not found
    */
   LoadBalancer get(int id);

   /**
    * Delete a load balancer.
    * <p/>
    * Delete the specified load balancer and its associated configuration from the account. Any and all configuration 
    * data is immediately purged and isnot recoverable.
    * 
    * @param id
    *           to remove
    */
   void delete(int id);
   
   /**
    * When a metadata item is added, it is assigned a unique identifier that can be used for mutating operations such
    * as changing the value attribute or removing it. Key and value must be 256 characters or less. 
    * All UTF-8 characters are valid.
    */
   Metadata createMetadata(int id, Map<String, String> metadata);
    
   /**
    * List a load balancer's metadata.
    */
   Metadata getMetadata(int id);
   
   /**
    * Update metadatum. Key and value must be 256 characters or less. All UTF-8 characters are valid.
    * 
    * @return true on a successful update, false if the metadatum was not found
    */
   boolean updateMetadatum(int id, int metadatumId, String value);

   /**
    * Delete metadatum.
    * 
    * @see LoadBalancerApi#deleteMetadata(int, Iterable)
    * 
    * @return true on a successful removal, false if the metadatum was not found
    */
   boolean deleteMetadatum(int id, int metadatumId);
   
   /**
    * Batch delete metadata given the specified ids.
    * 
    * The current default limit is ten ids per request. Any and all configuration data is immediately purged and is 
    * not recoverable. If one or more of the items in the list cannot be removed due to its current status, an 
    * exception is thrown along with the ids of the ones the system identified as potential failures for this request.
    * 
    * @return true on a successful removal, false if the metadata was not found
    */
   boolean deleteMetadata(int id, Iterable<Integer> metadataIds);
}
