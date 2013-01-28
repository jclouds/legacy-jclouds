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
import java.util.Set;
import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.http.HttpResponseException;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.Metadata;
import org.jclouds.rackspace.cloudloadbalancers.domain.Node;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeAttributes;
import org.jclouds.rackspace.cloudloadbalancers.domain.NodeRequest;

/**
 * Provides synchronous access to CloudLoadBalancers Node features.
 * <p/>
 * 
 * @see NodeAsyncApi
 * @author Everett Toews
 */
public interface NodeApi {
   /**
    * Create a new node with the configuration defined by the request.
    * 
    * <p/>
    * When a node is added, it is assigned a unique identifier that can be used for mutating operations 
    * such as changing the condition or removing it. Every load balancer is dual-homed on both the public 
    * Internet and ServiceNet; as a result, nodes can either be internal ServiceNet addresses or addresses 
    * on the public Internet.
    * 
    * @param nodes
    *           configurations to create
    * @return created nodes
    * @throws HttpResponseException
    *            If the corresponding request cannot be fulfilled due to insufficient or invalid
    *            data
    * 
    */
	Set<Node> add(Iterable<NodeRequest> nodes);

   /**
    * 
    * Update the properties of a node.
    * 
    * <p/>
    * This operation asynchronously updates the attributes of the specified node. Upon
    * successful validation of the request, the service will return a 202 (Accepted) response code.
    * A caller can poll the load balancer with its ID to wait for the changes to be applied and the
    * load balancer to return to an ACTIVE status.
    * @param id
    *           node to get
    * @param attrs
    *           what to change
    * 
    * @see LoadBalancerAttributes#fromLoadBalancer
    */
   void update(int id, NodeAttributes attrs);

   /**
    * @return all nodes for a given loadbalancer, or empty set if none available
    */
   PagedIterable<Node> list();

   IterableWithMarker<Node> list(PaginationOptions options);

   /**
    * @param id
    *           node to get
    * @return details of the specified node, or null if not found
    */
   Node get(int id);

   /**
    * Remove a node from the account.
    * <p/>
    * The remove load balancer function removes the specified load balancer and its associated
    * configuration from the account. Any and all configuration data is immediately purged and is
    * not recoverable.
    * 
    * @param id
    *           node to remove
    */
   void remove(int id);
   
   /**
    * Batch-remove nodes from the account.
    * <p/>
    * The current default limit is ten ids per request. Any and all configuration data is 
    * immediately purged and is not recoverable. By chance one of the items in the list 
    * cannot be removed due to its current status a 400:BadRequest is returned along with the ids 
    * of the ones the system identified as potential failures for this request
    * 
    * @param ids
    *           nodes to remove
    */
   void remove(Iterable<Integer> ids);
   
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
    * @see NodeApi#deleteMetadata(int, Iterable)
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
