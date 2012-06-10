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
package org.jclouds.openstack.nova.v2_0.extensions;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.jclouds.concurrent.Timeout;
import org.jclouds.openstack.keystone.v2_0.filters.AuthenticateRequest;
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;
import org.jclouds.rest.annotations.RequestFilters;

/**
 * Provide access to Host Aggregates in Nova (alias "OS-AGGREGATES")
 *
 * @author Adam Lowe
 * @see HostAggregateAsyncClient
 * @see <a href="http://nova.openstack.org/api_ext/ext_aggregates.html"/>
 * @see <a href="http://wiki.openstack.org/host-aggregates"/>
 */
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.AGGREGATES)
@Timeout(duration = 180, timeUnit = TimeUnit.SECONDS)
@RequestFilters(AuthenticateRequest.class)
public interface HostAggregateClient {

   /**
    * @return the set of host aggregates.
    */
   Set<HostAggregate> listAggregates();

   /**
    * Retrieves the details of an aggregate, hosts and metadata included.
    *
    * @return the details of the aggregate requested.
    */
   HostAggregate getAggregate(String id);

   /**
    * Creates an aggregate, given its name and availability zone.
    * 
    * @return the newly created Aggregate
    */
   HostAggregate createAggregate(String name, String availabilityZone);

   /**
    * Updates the name of an aggregate.
    */
   HostAggregate updateName(String id, String name);

   /**
    * Updates the availability zone an aggregate.
    */
   HostAggregate updateAvailabilityZone(String id, String availabilityZone);

   /**
    * Removes an aggregate.
    */
   Boolean deleteAggregate(String id);

   /**
    * Adds a host to an aggregate
    */
   HostAggregate addHost(String id, String host);

   /**
    * Removes a host from an aggregate
    */
   HostAggregate removeHost(String id, String host);

   /**
    * Adds metadata to an aggregate
    */
   HostAggregate setMetadata(String id, Map<String, String> metadata);
}
