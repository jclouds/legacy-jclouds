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
package org.jclouds.softlayer.reference;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * Configuration properties and constants used in SoftLayer connections.
 * 
 * @author Adrian Cole
 */
public interface SoftLayerConstants {
   /**
    * Name of the product package corresponding to cloud servers
    */
   public static final String PROPERTY_SOFTLAYER_VIRTUALGUEST_PACKAGE_NAME = "jclouds.softlayer.virtualguest.package-name";

   public static final Set<Long> DEFAULT_VIRTUAL_GUEST_PRICES = ImmutableSet.<Long>builder()
           .add(1639L) // 100 GB (SAN)
           .add(21L) // 1 IP Address
           .add(55L) // Host Ping
           .add(58L) // Automated Notification
           .add(1800L) // 0 GB Bandwidth
           .add(57L) // Email and Ticket
           .add(274L) // 1000 Mbps Public & Private Networks
           .add(905L) // Reboot / Remote Console
           .add(418L) // Nessus Vulnerability Assessment & Reporting
           .add(420L) // Unlimited SSL VPN Users & 1 PPTP VPN User per account
           .build();
}
