/**
 *
 * Copyright (C) 2010 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */

package org.jclouds.savvis.vpdc.domain;

import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.vcloud.domain.ReferenceType;
import org.jclouds.vcloud.domain.network.FenceMode;
import org.jclouds.vcloud.domain.network.firewall.FirewallRule;
import org.jclouds.vcloud.domain.network.nat.rules.PortForwardingRule;

/**
 * 
 * A network that is available in a vDC.
 * 
 * @author Kedar Dave
 */
public interface SymphonyVPDCNetwork extends ReferenceType {
   /**
    * 
    * @return Description of the network
    */
   String getDescription();

   /**
    * @return IP addresses of the network’s DNS servers.
    */
   Set<String> getDnsServers();

   /**
    * 
    * 
    * @return The IP address of the network’s primary gateway
    */
   String getGateway();

   /**
    * *
    * 
    * @return the network’s subnet mask
    */
   String getNetmask();

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
   Set<PortForwardingRule> getNatRules();

   /**
    * @return Firewall rules for the network
    */
   Set<FirewallRule> getFirewallRules();

}