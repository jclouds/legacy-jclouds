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
package org.jclouds.cloudloadbalancers.reference;



/**
 * The load balancing service is a regionalized service. It allows the caller to select a region
 * into which a load balancer is to be provisioned.
 * <p/>
 * If load balancing Cloud Servers, you can determine the appropriate region to select by viewing
 * your Cloud Servers list and creating a load balancer within the same region as the data center in
 * which your Cloud Server resides. When your resources reside in the same region as your load
 * balancer, devices are in close proximity to each other and can take advantage of ServiceNet
 * connectivity for free data transfer between services.
 * <p/>
 * If load balancing external servers, you can determine the appropriate region to select by
 * choosing the region that is geographically as close to your external servers as possible.
 * 
 * @see <a
 *      href="http://docs.rackspacecloud.com/loadbalancers/api/v1.0/clb-devguide/content/ch03s02.html"
 *      />
 * @author Adrian Cole
 */

public interface Region {
   /**
    * Chicago (ORD) https://ord.loadbalancers.api.rackspacecloud.com/v1.0/1234/
    */
   public static final String ORD = "ORD";

   /**
    * Dallas/Ft. Worth (DFW) https://dfw.loadbalancers.api.rackspacecloud.com/v1.0/1234/
    */
   public static final String DFW = "DFW";
   
   /**
    * London/Slough (LON) https://lon.loadbalancers.api.rackspacecloud.com/v1.0/1234/
    */
   public static final String LON = "LON";
   
}
