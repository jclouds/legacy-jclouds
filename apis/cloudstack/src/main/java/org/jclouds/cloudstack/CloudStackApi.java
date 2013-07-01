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
package org.jclouds.cloudstack;

import org.jclouds.cloudstack.features.AccountApi;
import org.jclouds.cloudstack.features.AddressApi;
import org.jclouds.cloudstack.features.AsyncJobApi;
import org.jclouds.cloudstack.features.ConfigurationApi;
import org.jclouds.cloudstack.features.EventApi;
import org.jclouds.cloudstack.features.FirewallApi;
import org.jclouds.cloudstack.features.GuestOSApi;
import org.jclouds.cloudstack.features.HypervisorApi;
import org.jclouds.cloudstack.features.ISOApi;
import org.jclouds.cloudstack.features.LimitApi;
import org.jclouds.cloudstack.features.LoadBalancerApi;
import org.jclouds.cloudstack.features.NATApi;
import org.jclouds.cloudstack.features.NetworkApi;
import org.jclouds.cloudstack.features.OfferingApi;
import org.jclouds.cloudstack.features.SSHKeyPairApi;
import org.jclouds.cloudstack.features.SecurityGroupApi;
import org.jclouds.cloudstack.features.SessionApi;
import org.jclouds.cloudstack.features.SnapshotApi;
import org.jclouds.cloudstack.features.TemplateApi;
import org.jclouds.cloudstack.features.VMGroupApi;
import org.jclouds.cloudstack.features.VirtualMachineApi;
import org.jclouds.cloudstack.features.VolumeApi;
import org.jclouds.cloudstack.features.ZoneApi;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides synchronous access to CloudStack.
 * <p/>
 *
 * @author Adrian Cole
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 */
public interface CloudStackApi {
   /**
    * Provides synchronous access to Zone features.
    */
   @Delegate
   ZoneApi getZoneApi();

   /**
    * Provides synchronous access to Template features.
    */
   @Delegate
   TemplateApi getTemplateApi();

   /**
    * Provides synchronous access to Service, Disk, and Network Offering
    * features.
    */
   @Delegate
   OfferingApi getOfferingApi();

   /**
    * Provides synchronous access to Network features.
    */
   @Delegate
   NetworkApi getNetworkApi();

   /**
    * Provides synchronous access to VirtualMachine features.
    */
   @Delegate
   VirtualMachineApi getVirtualMachineApi();

   /**
    * Provides synchronous access to SecurityGroup features.
    */
   @Delegate
   SecurityGroupApi getSecurityGroupApi();

   /**
    * Provides synchronous access to AsyncJob features.
    */
   @Delegate
   AsyncJobApi getAsyncJobApi();

   /**
    * Provides synchronous access to Address features.
    */
   @Delegate
   AddressApi getAddressApi();

   /**
    * Provides synchronous access to NAT features.
    */
   @Delegate
   NATApi getNATApi();

   /**
    * Provides synchronous access to Firewall features.
    */
   @Delegate
   FirewallApi getFirewallApi();

   /**
    * Provides synchronous access to LoadBalancer features.
    */
   @Delegate
   LoadBalancerApi getLoadBalancerApi();

   /**
    * Provides synchronous access to GuestOS features.
    */
   @Delegate
   GuestOSApi getGuestOSApi();

   /**
    * Provides synchronous access to Hypervisor features.
    */
   @Delegate
   HypervisorApi getHypervisorApi();

   /**
    * Provides synchronous access to Configuration features.
    */
   @Delegate
   ConfigurationApi getConfigurationApi();

   /**
    * Provides synchronous access to Account features.
    */
   @Delegate
   AccountApi getAccountApi();

   /**
    * Provides synchronous access to SSH Keypairs
    */
   @Delegate
   SSHKeyPairApi getSSHKeyPairApi();

   /**
    * Provides synchronous access to VM groups
    */
   @Delegate
   VMGroupApi getVMGroupApi();

   /**
    * Provides synchronous access to Events
    */
   @Delegate
   EventApi getEventApi();

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   LimitApi getLimitApi();

   /**
    * Provides synchronous access to ISOs
    */
   @Delegate
   ISOApi getISOApi();

   /**
    * Provides synchronous access to Volumes
    */
   @Delegate
   VolumeApi getVolumeApi();

   /**
    * Provides synchronous access to Snapshots
    */
   @Delegate
   SnapshotApi getSnapshotApi();

   /**
    * Provides synchronous access to Sessions
    */
   @Delegate
   SessionApi getSessionApi();
}
