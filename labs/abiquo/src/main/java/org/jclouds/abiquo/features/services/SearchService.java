/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.jclouds.abiquo.features.services;

import org.jclouds.abiquo.domain.cloud.VirtualDatacenter;
import org.jclouds.abiquo.domain.cloud.Volume;
import org.jclouds.abiquo.domain.cloud.options.VolumeOptions;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.options.EnterpriseOptions;
import org.jclouds.abiquo.domain.infrastructure.Datacenter;
import org.jclouds.abiquo.domain.infrastructure.LogicServer;
import org.jclouds.abiquo.domain.infrastructure.ManagedRack;
import org.jclouds.abiquo.domain.infrastructure.StorageDevice;
import org.jclouds.abiquo.domain.infrastructure.StoragePool;
import org.jclouds.abiquo.domain.infrastructure.options.StoragePoolOptions;
import org.jclouds.abiquo.domain.network.PrivateIp;
import org.jclouds.abiquo.domain.network.PrivateNetwork;
import org.jclouds.abiquo.domain.network.PublicIp;
import org.jclouds.abiquo.domain.network.options.IpOptions;
import org.jclouds.abiquo.domain.options.search.FilterOptions;
import org.jclouds.abiquo.internal.BaseSearchService;

import com.google.inject.ImplementedBy;

/**
 * Provides high level Abiquo search, filter and pagination operations.
 * 
 * @author Ignasi Barrera
 * @author Francesc Montserrat
 */
@ImplementedBy(BaseSearchService.class)
public interface SearchService {
   /*********************** Enterprise ***********************/

   /**
    * Get the list of filtered enterprises.
    * 
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<Enterprise> searchEnterprises(final EnterpriseOptions options);

   /**
    * Get the list of filtered enterprises for a datacenter.
    * 
    * @param datacenter
    *           The given datacenter.
    * @param options
    *           The set of filtering and pagination options of the search.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/Datacenter+Resource#DatacenterResource-Retrievealistofenterprisesusingdatacenter"
    *      > http://community.abiquo.com/display/ABI20/Datacenter+Resource#
    *      DatacenterResource- Retrievealistofenterprisesusingdatacenter</a>
    */
   Iterable<Enterprise> searchEnterprisesUsingDatacenter(final Datacenter datacenter, final EnterpriseOptions options);

   /*********************** Volume ***********************/

   /**
    * Get the list of filtered volumes for a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The given virtual datacenter.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<Volume> searchVolumes(final VirtualDatacenter virtualDatacenter, final VolumeOptions options);

   /*********************** Storage Pool ***********************/

   /**
    * Get the list of filtered storage pools for a storage device.
    * 
    * @param device
    *           The given storage device.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<StoragePool> searchStoragePools(final StorageDevice device, final StoragePoolOptions options);

   /*********************** Private IPs ***********************/

   /**
    * Get the list of filtered ips for a private network.
    * 
    * @param network
    *           The given private network.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<PrivateIp> searchPrivateIps(final PrivateNetwork network, final IpOptions options);

   /*********************** Public IPs ***********************/

   /**
    * Get the list of filtered public ips to purchase by a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The given virtual datacenter.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<PublicIp> searchPublicIpsToPurchase(final VirtualDatacenter virtualDatacenter, final IpOptions options);

   /**
    * Get the list of filtered purchased public ips by a virtual datacenter.
    * 
    * @param virtualDatacenter
    *           The given virtual datacenter.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<PublicIp> searchPurchasedPublicIps(final VirtualDatacenter virtualDatacenter, final IpOptions options);

   /*********************** Logic Server ***********************/

   /**
    * Get the list of service profiles for managed rack.
    * 
    * @param managedRack
    *           The given rack.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<LogicServer> searchServiceProfiles(final ManagedRack rack, final FilterOptions options);

   /**
    * Get the list of service profile templates for managed rack.
    * 
    * @param managedRack
    *           The given rack.
    * @param options
    *           The set of filtering and pagination options of the search.
    */
   Iterable<LogicServer> searchServiceProfileTemplates(final ManagedRack rack, final FilterOptions options);
}
