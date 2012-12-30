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
import org.jclouds.elb.domain.InstanceHealth;

/**
 * Provides access to Amazon ELB via the Query API
 * <p/>
 * 
 * @see <a href="http://docs.amazonwebservices.com/ElasticLoadBalancing/latest/APIReference" />
 * @see InstanceAsyncApi
 * @author Adrian Cole
 */
public interface InstanceApi {

   /**
    * Returns the current state of the instances of the specified LoadBalancer.
    * 
    * <h4>Note</h4>
    * 
    * The api must have created the specified input LoadBalancer in order to retrieve this
    * information; the api must provide the same account credentials as those that were used to
    * create the LoadBalancer.
    * 
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return state of all instances of the load balancer
    */
   Set<InstanceHealth> getHealthOfInstancesOfLoadBalancer(String loadBalancerName);

   /**
    * Returns the current state of the instances of the specified LoadBalancer.
    * 
    * <h4>Note</h4>
    * 
    * The api must have created the specified input LoadBalancer in order to retrieve this
    * information; the api must provide the same account credentials as those that were used to
    * create the LoadBalancer.
    * 
    * @param instanceIds
    *           A list of instance IDs whose states are being queried.
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return state of all instances of the load balancer
    */
   Set<InstanceHealth> getHealthOfInstancesOfLoadBalancer(Iterable<String> instanceIds, String loadBalancerName);

   /**
    * Adds new instances to the LoadBalancer.
    * 
    * Once the instance is registered, it starts receiving traffic and requests from the
    * LoadBalancer. Any instance that is not in any of the Availability Zones registered for the
    * LoadBalancer will be moved to the OutOfService state. It will move to the InService state when
    * the Availability Zone is added to the LoadBalancer.
    * 
    * <h4>Note</h4>
    * 
    * In order for this call to be successful, the api must have created the LoadBalancer. The
    * api must provide the same account credentials as those that were used to create the
    * LoadBalancer.
    * 
    * <h4>Note</h4>
    * 
    * Completion of this API does not guarantee that operation has completed. Rather, it means that
    * the request has been registered and the changes will happen shortly.
    * 
    * @param instanceIds
    *           A list of instance IDs that should be registered with the LoadBalancer.
    * 
    *           <h4>Note</h4>
    * 
    *           When the instance is stopped and then restarted, the IP addresses associated with
    *           your instance changes. Elastic Load Balancing cannot recognize the new IP address,
    *           which prevents it from routing traffic to your instances. We recommend that you
    *           de-register your Amazon EC2 instances from your load balancer after you stop your
    *           instance, and then register the load balancer with your instance after you've
    *           restarted. To de-register your instances from load balancer, use
    *           DeregisterInstancesFromLoadBalancer action.
    * 
    * 
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return instanceIds registered with load balancer
    */
   Set<String> registerInstancesWithLoadBalancer(Iterable<String> instanceIds, String loadBalancerName);
   
   Set<String> registerInstanceWithLoadBalancer(String instanceId, String loadBalancerName);

   /**
    * Deregisters instances from the LoadBalancer. Once the instance is deregistered, it will stop
    * receiving traffic from the LoadBalancer.
    * 
    * In order to successfully call this API, the same account credentials as those used to create
    * the LoadBalancer must be provided.
    * 
    * @param instanceIds
    *           A list of EC2 instance IDs consisting of all instances to be deregistered.
    * 
    * @param loadBalancerName
    *           The name associated with the LoadBalancer. The name must be unique within the api
    *           AWS account.
    * 
    * @return instanceIds still registered with load balancer
    */
   Set<String> deregisterInstancesFromLoadBalancer(Iterable<String> instanceIds, String loadBalancerName);

   Set<String> deregisterInstanceFromLoadBalancer(String instanceId, String loadBalancerName);

}
