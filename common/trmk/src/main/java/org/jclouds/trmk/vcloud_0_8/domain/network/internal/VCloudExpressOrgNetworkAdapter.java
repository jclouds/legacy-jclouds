/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.trmk.vcloud_0_8.domain.network.internal;

import org.jclouds.trmk.vcloud_0_8.domain.Task;
import org.jclouds.trmk.vcloud_0_8.domain.network.DhcpService;
import org.jclouds.trmk.vcloud_0_8.domain.network.Features;
import org.jclouds.trmk.vcloud_0_8.domain.network.FenceMode;
import org.jclouds.trmk.vcloud_0_8.domain.network.FirewallService;
import org.jclouds.trmk.vcloud_0_8.domain.network.IpRange;
import org.jclouds.trmk.vcloud_0_8.domain.network.IpScope;
import org.jclouds.trmk.vcloud_0_8.domain.network.NatService;
import org.jclouds.trmk.vcloud_0_8.domain.network.VCloudExpressNetwork;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;

/**
 * 
 * @author Adrian Cole
 */
public class VCloudExpressOrgNetworkAdapter extends OrgNetworkImpl {

   public VCloudExpressOrgNetworkAdapter(VCloudExpressNetwork in) {
      super(in.getName(), in.getType(), in.getHref(), null, in.getDescription(), ImmutableSet.<Task> of(),
               parseConfiguration(in), null, ImmutableSet.<String> of());
   }

   static Configuration parseConfiguration(VCloudExpressNetwork in) {

      String dns1 = (in.getDnsServers().size() > 0) ? Iterables.get(in.getDnsServers(), 0) : null;
      String dns2 = (in.getDnsServers().size() > 1) ? Iterables.get(in.getDnsServers(), 1) : null;

      String gateway = in.getGateway();

      String netmask = in.getNetmask();

      FenceMode mode = in.getFenceModes().size() > 0 ? Iterables.get(in.getFenceModes(), 0) : FenceMode.BRIDGED;

      DhcpService dhcp = in.isDhcp() != null && in.isDhcp() ? new DhcpService(true, null, null, null) : null;

      NatService nat = in.getNatRules().size() > 0 ? new NatService(true, null, null, in.getNatRules()) : null;

      FirewallService firewall = in.getFirewallRules().size() > 0 ? new FirewallService(true, in.getFirewallRules())
               : null;
      return new OrgNetworkImpl.ConfigurationImpl(new IpScope(true, gateway, netmask, dns1, dns2, null, ImmutableSet
               .<IpRange> of(), ImmutableSet.<String> of()), null, mode, new Features(dhcp, firewall, nat));
   }
}