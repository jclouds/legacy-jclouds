/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.openstack.nova.v2_0.extensions;

import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provides synchronous access to Floating IPs.
 * <p/>
 * 
 * @see FloatingIPAsyncApi
 * @author Jeremy Daggett
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.FLOATING_IPS)
public interface FloatingIPApi {

   /**
    * List all Floating IP addresses
    * 
    * @return all Floating IPs
    */
   FluentIterable<? extends FloatingIP> list();

   /**
    * Get a specific Floating IP address
    * 
    * @return all Floating IPs
    */
   FloatingIP get(String id);

   /**
    * Allocate a Floating IP address
    * 
    * @return a newly created FloatingIP
    */
   FloatingIP create();

   /**
    * Allocate a Floating IP address from a pool
    *
    * @param pool
    *         Pool to allocate IP address from
    * @return a newly created FloatingIP
    */
   FloatingIP allocateFromPool(String pool);

   /**
    * Decreate a Floating IP address
    * 
    * @param id
    *           the Floating IP id
    */
   void delete(String id);

   /**
    * Add a Floating IP address to a Server
    * 
    * @param serverId
    *           the serverId
    * @param address
    *           the IP address to add
    * 
    *           NOTE: Possibly move this to ServerApi?
    */
   void addToServer(String address, String serverId);

   /**
    * Remove a Floating IP address from a Server
    * 
    * @param serverId
    *           the serverId
    * @param address
    *           the IP address to remove
    * 
    *           NOTE: Possibly move this to ServerApi?
    */
   void removeFromServer(String address, String serverId);
}
