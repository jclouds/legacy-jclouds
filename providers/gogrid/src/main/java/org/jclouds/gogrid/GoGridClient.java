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
package org.jclouds.gogrid;

import org.jclouds.gogrid.services.GridImageClient;
import org.jclouds.gogrid.services.GridIpClient;
import org.jclouds.gogrid.services.GridJobClient;
import org.jclouds.gogrid.services.GridLoadBalancerClient;
import org.jclouds.gogrid.services.GridServerClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * @author Oleksiy Yarmula
 */
public interface GoGridClient {

   /**
    * Services with methods, related to managing servers
    */
   @Delegate
   GridServerClient getServerServices();

   /**
    * Services with methods, related to retrieving jobs
    */
   @Delegate
   GridJobClient getJobServices();

   /**
    * Services with methods, related to retrieving IP addresses
    */
   @Delegate
   GridIpClient getIpServices();

   /**
    * Services with methods, related to managing load balancers.
    */
   @Delegate
   GridLoadBalancerClient getLoadBalancerServices();

   /**
    * Services with methods, related to managing images.
    */
   @Delegate
   GridImageClient getImageServices();

}
