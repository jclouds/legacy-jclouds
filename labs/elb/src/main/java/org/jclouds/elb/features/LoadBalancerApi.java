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

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.elb.domain.Listener;
import org.jclouds.elb.domain.LoadBalancer;
import org.jclouds.elb.options.ListLoadBalancersOptions;
import org.jclouds.javax.annotation.Nullable;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" >doc</a>
 * @see LoadBalancerAsyncApi
 * @author Adrian Cole
 */
public interface LoadBalancerApi {

   String createListeningInAvailabilityZones(String name, Iterable<Listener> listeners,
            Iterable<String> availabilityZones);
   
   String createListeningInAvailabilityZones(String name, Listener listeners,
            Iterable<String> availabilityZones);

   String createListeningInSubnetAssignedToSecurityGroups(String name, String subnetId,
           Iterable<String> securityGroupIds);
   
   String createListeningInSubnetsAssignedToSecurityGroups(String name, Iterable<String> subnetIds,
            Iterable<String> securityGroupIds);


   /**
    * Retrieves information about the specified loadBalancer.
    * 
    * @param name
    *           Name of the loadBalancer to get information about.
    * @return null if not found
    */
   @Nullable
   LoadBalancer get(String name);

   /**
    * Returns detailed configuration information for the specified LoadBalancers. If there are none,
    * the action returns an empty list.
    * 
    * <br/>
    * You can paginate the results using the {@link ListLoadBalancersOptions parameter}
    * 
    * @param options
    *           the options describing the loadBalancers query
    * 
    * @return the response object
    */
   IterableWithMarker<LoadBalancer> list(ListLoadBalancersOptions options);

   /**
    * Lists the loadBalancers all load balancers
    * 
    * @return the response object
    */
   PagedIterable<LoadBalancer> list();
  
   /**
    * Deletes the specified LoadBalancer.
    * 
    * <p/>
    * If attempting to recreate the LoadBalancer, the api must reconfigure all the settings. The
    * DNS name associated with a deleted LoadBalancer will no longer be usable. Once deleted, the
    * name and associated DNS record of the LoadBalancer no longer exist and traffic sent to any of
    * its IP addresses will no longer be delivered to api instances. The api will not receive
    * the same DNS name even if a new LoadBalancer with same LoadBalancerName is created.
    * 
    * <p/>
    * To successfully call this API, the api must provide the same account credentials as were
    * used to create the LoadBalancer.
    * 
    * <h4>Note</h4>
    * 
    * By design, if the LoadBalancer does not exist or has already been deleted, DeleteLoadBalancer
    * still succeeds.
    * 
    * 
    * @param name
    *           Name of the load balancer
    */
   void delete(String name);

}
