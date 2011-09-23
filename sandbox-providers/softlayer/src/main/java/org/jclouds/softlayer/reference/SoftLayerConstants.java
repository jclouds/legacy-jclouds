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
import org.jclouds.softlayer.domain.ProductItemPrice;

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

   public static final Set<ProductItemPrice> DEFAULT_VIRTUAL_GUEST_PRICES = ImmutableSet.<ProductItemPrice>builder()
           .add(ProductItemPrice.builder().id(1639L).build()) // 100 GB (SAN)
           .add(ProductItemPrice.builder().id(21L).build()) // 1 IP Address
           .add(ProductItemPrice.builder().id(55L).build()) // Host Ping
           .add(ProductItemPrice.builder().id(58L).build()) // Automated Notification
           .add(ProductItemPrice.builder().id(1800L).build()) // 0 GB Bandwidth
           .add(ProductItemPrice.builder().id(57L).build()) // Email and Ticket
           .add(ProductItemPrice.builder().id(274L).build()) // 1000 Mbps Public & Private Networks
           .add(ProductItemPrice.builder().id(905L).build()) // Reboot / Remote Console
           .add(ProductItemPrice.builder().id(418L).build()) // Nessus Vulnerability Assessment & Reporting
           .add(ProductItemPrice.builder().id(420L).build()) // Unlimited SSL VPN Users & 1 PPTP VPN User per account
           .build();
}
