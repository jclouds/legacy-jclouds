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
    * @return The object will contain a unique identifier and status of the request. Using the
    *         identifier, the caller can check on the progress of the operation by performing a
    *         {@link LoadBalancerApi#get}.
    */
   LoadBalancer create(LoadBalancerRequest lb);

   /**
    * Update the properties of a load balancer.
    * 
    * @return The object will contain a unique identifier and status of the request. Using the
    *         identifier, the caller can check on the progress of the operation by performing a
    *         {@link LoadBalancerApi#get}.
    */
   void update(int id, LoadBalancerAttributes attrs);

   /**
    * List the load balancers.
    */
   PagedIterable<LoadBalancer> list();
   
   /**
    * List the load balancers with full control of pagination.
    */
   IterableWithMarker<LoadBalancer> list(PaginationOptions options);

   /**
    * Get a load balancer.
    */
   LoadBalancer get(int id);

   /**
    * Delete a load balancer.
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
