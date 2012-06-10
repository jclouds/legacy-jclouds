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
package org.jclouds.openstack.nova.v2_0.config;

/**
 * Configuration properties and constants used in openstack Nova connections.
 *
 * @author Adam Lowe
 */
public class NovaProperties {

   /**
    * Eventual consistency delay for retrieving a security group after it is created (in ms)
    */
   public static final String TIMEOUT_SECURITYGROUP_PRESENT = "jclouds.openstack-nova.timeout.securitygroup-present";

   /**
    * Whenever a node is created, automatically allocate and assign a floating ip address, also
    * deallocate when the node is destroyed.
    */
   public static final String AUTO_ALLOCATE_FLOATING_IPS = "jclouds.openstack-nova.auto-allocate-floating-ips";

   /**
    * Whenever a node is created, automatically generate keypairs for groups, as needed, also
    * delete the keypair(s) when the last node in the group is destroyed.
    */
   public static final String AUTO_GENERATE_KEYPAIRS = "jclouds.openstack-nova.auto-generate-keypairs";

}
