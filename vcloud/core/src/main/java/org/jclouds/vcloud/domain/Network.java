/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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
 * ====================================================================
 */
package org.jclouds.vcloud.domain;

import java.net.InetAddress;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * 
 * A network that is available in a vDC.
 * 
 * @author Adrian Cole
 */
public interface Network extends NamedResource {
   /**
    * 
    * @return Description of the network
    */
   String getDescription();

   /**
    * @return IP addresses of the network’s DNS servers.
    */
   Set<InetAddress> getDnsServers();

   /**
    * 
    * 
    * @return The IP address of the network’s primary gateway
    */
   InetAddress getGateway();

   /**
    * *
    * 
    * @return the network’s subnet mask
    */
   InetAddress getNetmask();

   /**
    * return the network’s fence modes.
    */
   Set<FenceMode> getFenceModes();

   /**
    * return True if the network provides DHCP services
    */
   @Nullable
   Boolean isDhcp();

   /**
    * 
    * @return Network Address Translation rules for the network
    */
   Set<NatRule> getNatRules();

   /**
    * @return Firewall rules for the network
    */
   Set<FirewallRule> getFirewallRules();

}