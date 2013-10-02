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

import java.util.Map;
import org.jclouds.openstack.nova.v2_0.domain.HostAggregate;
import org.jclouds.openstack.v2_0.ServiceType;
import org.jclouds.openstack.v2_0.services.Extension;

import com.google.common.annotations.Beta;
import com.google.common.collect.FluentIterable;

/**
 * Provide access to Host Aggregates in Nova (alias "OS-AGGREGATES")
 *
 * @author Adam Lowe
 * @see HostAggregateAsyncApi
 * @see <a href="http://nova.openstack.org/api_ext/ext_aggregates.html"/>
 * @see <a href="http://wiki.openstack.org/host-aggregates"/>
 */
@Beta
@Extension(of = ServiceType.COMPUTE, namespace = ExtensionNamespaces.AGGREGATES)
public interface HostAggregateApi {

   /**
    * @return the set of host aggregates.
    */
   FluentIterable<? extends HostAggregate> list();

   /**
    * Retrieves the details of an aggregate, hosts and metadata included.
    *
    * @return the details of the aggregate requested.
    */
   HostAggregate get(String id);

   /**
    * Creates an aggregate, given its name and availability zone.
    * 
    * @return the newly created Aggregate
    */
   HostAggregate createInAvailabilityZone(String name, String availabilityZone);

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
   Boolean delete(String id);

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
