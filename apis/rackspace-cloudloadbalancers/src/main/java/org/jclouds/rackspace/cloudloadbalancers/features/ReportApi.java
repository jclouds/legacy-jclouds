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

import java.util.Date;

import org.jclouds.collect.IterableWithMarker;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.v2_0.options.PaginationOptions;
import org.jclouds.rackspace.cloudloadbalancers.domain.HistoricalUsage;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancer;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerStats;
import org.jclouds.rackspace.cloudloadbalancers.domain.LoadBalancerUsage;
import org.jclouds.rackspace.cloudloadbalancers.domain.Protocol;

/**
 * 
 * <p/>
 * @see ReportAsyncApi
 * @author Everett Toews
 */
public interface ReportApi {
   /**
    * List billable load balancers for the given date range.
    */
   PagedIterable<LoadBalancer> listBillableLoadBalancers(Date startTime, Date endTime);
   
   IterableWithMarker<LoadBalancer> listBillableLoadBalancers(PaginationOptions options);

   /**
    * View of all transfer activity, average number of connections, and number of virtual IPs associated with the load
    * balancing service. Historical usage data is available for up to 90 days of service activity.
    */
   HistoricalUsage getHistoricalUsage(Date startTime, Date endTime);
   
   /**
    * Historical usage data is available for up to 90 days of service activity.
    */
   PagedIterable<LoadBalancerUsage> listLoadBalancerUsage(int loadBalancerId, Date startTime, Date endTime);
   
   IterableWithMarker<LoadBalancerUsage> listLoadBalancerUsage(PaginationOptions options);
   
   /**
    * Current usage represents all usage recorded within the preceding 24 hours.
    */
   PagedIterable<LoadBalancerUsage> listCurrentLoadBalancerUsage(int loadBalancerId);
   
   IterableWithMarker<LoadBalancerUsage> listCurrentLoadBalancerUsage(PaginationOptions options);
   
   /**
    * Current usage represents all usage recorded within the preceding 24 hours.
    */
   LoadBalancerStats getLoadBalancerStats(int loadBalancerId);
   
   /**
    * All load balancers must define the protocol of the service which is being load balanced. The protocol selection 
    * should be based on the protocol of the back-end nodes. When configuring a load balancer, the default port for 
    * the given protocol will be selected from this list unless otherwise specified.
    */
   Iterable<Protocol> listProtocols();
   
   /**
    * Get all of the possible algorthims usable by load balancers.
    */
   Iterable<String> listAlgorithms();
}