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

import org.jclouds.cloudstack.features.AccountAsyncClient;
import org.jclouds.cloudstack.features.AddressAsyncClient;
import org.jclouds.cloudstack.features.AsyncJobAsyncClient;
import org.jclouds.cloudstack.features.ConfigurationAsyncClient;
import org.jclouds.cloudstack.features.EventAsyncClient;
import org.jclouds.cloudstack.features.FirewallAsyncClient;
import org.jclouds.cloudstack.features.GuestOSAsyncClient;
import org.jclouds.cloudstack.features.HypervisorAsyncClient;
import org.jclouds.cloudstack.features.ISOAsyncClient;
import org.jclouds.cloudstack.features.LimitAsyncClient;
import org.jclouds.cloudstack.features.LoadBalancerAsyncClient;
import org.jclouds.cloudstack.features.NATAsyncClient;
import org.jclouds.cloudstack.features.NetworkAsyncClient;
import org.jclouds.cloudstack.features.OfferingAsyncClient;
import org.jclouds.cloudstack.features.SSHKeyPairAsyncClient;
import org.jclouds.cloudstack.features.SecurityGroupAsyncClient;
import org.jclouds.cloudstack.features.SessionAsyncClient;
import org.jclouds.cloudstack.features.SnapshotAsyncClient;
import org.jclouds.cloudstack.features.TemplateAsyncClient;
import org.jclouds.cloudstack.features.VMGroupAsyncClient;
import org.jclouds.cloudstack.features.VirtualMachineAsyncClient;
import org.jclouds.cloudstack.features.VolumeAsyncClient;
import org.jclouds.cloudstack.features.ZoneAsyncClient;
import org.jclouds.rest.annotations.Delegate;

/**
 * Provides asynchronous access to CloudStack via their REST API.
 * <p/>
 *
 * @author Adrian Cole
 * @see CloudStackClient
 * @see <a href="http://download.cloud.com/releases/2.2.0/api_2.2.12/TOC_User.html" />
 * @deprecated please use {@code org.jclouds.ContextBuilder#buildApi(CloudStackClient.class)} as
 *             {@link CloudStackAsyncClient} interface will be removed in jclouds 1.7.
 */
@Deprecated
public interface CloudStackAsyncClient {

   /**
    * Provides asynchronous access to Zone features.
    */
   @Delegate
   ZoneAsyncClient getZoneClient();

   /**
    * Provides asynchronous access to Template features.
    */
   @Delegate
   TemplateAsyncClient getTemplateClient();

   /**
    * Provides asynchronous access to Service, Disk, and Network Offering
    * features.
    */
   @Delegate
   OfferingAsyncClient getOfferingClient();

   /**
    * Provides asynchronous access to Network features.
    */
   @Delegate
   NetworkAsyncClient getNetworkClient();

   /**
    * Provides asynchronous access to VirtualMachine features.
    */
   @Delegate
   VirtualMachineAsyncClient getVirtualMachineClient();

   /**
    * Provides asynchronous access to SecurityGroup features.
    */
   @Delegate
   SecurityGroupAsyncClient getSecurityGroupClient();

   /**
    * Provides asynchronous access to AsyncJob features.
    */
   @Delegate
   AsyncJobAsyncClient getAsyncJobClient();

   /**
    * Provides asynchronous access to Address features.
    */
   @Delegate
   AddressAsyncClient getAddressClient();

   /**
    * Provides asynchronous access to NAT features.
    */
   @Delegate
   NATAsyncClient getNATClient();

   /**
    * Provides asynchronous access to Firewall features.
    */
   @Delegate
   FirewallAsyncClient getFirewallClient();

   /**
    * Provides asynchronous access to LoadBalancer features.
    */
   @Delegate
   LoadBalancerAsyncClient getLoadBalancerClient();

   /**
    * Provides asynchronous access to GuestOS features.
    */
   @Delegate
   GuestOSAsyncClient getGuestOSClient();

   /**
    * Provides asynchronous access to Hypervisor features.
    */
   @Delegate
   HypervisorAsyncClient getHypervisorClient();

   /**
    * Provides asynchronous access to Configuration features.
    */
   @Delegate
   ConfigurationAsyncClient getConfigurationClient();

   /**
    * Provides asynchronous access to Account features.
    */
   @Delegate
   AccountAsyncClient getAccountClient();

   /**
    * Provides asynchronous access to SSH Keypairs
    */
   @Delegate
   SSHKeyPairAsyncClient getSSHKeyPairClient();

   /**
    * Provides asynchronous access to VM groups
    */
   @Delegate
   VMGroupAsyncClient getVMGroupClient();

   /**
    * Provides synchronous access to Events
    */
   @Delegate
   EventAsyncClient getEventClient();

   /**
    * Provides synchronous access to Resource Limits
    */
   @Delegate
   LimitAsyncClient getLimitClient();

   /**
    * Provides asynchronous access to ISOs
    */
   @Delegate
   ISOAsyncClient getISOClient();

   /**
    * Provides asynchronous access to Volumes
    */
   @Delegate
   VolumeAsyncClient getVolumeClient();

   /**
    * Provides asynchronous access to Snapshots
    */
   @Delegate
   SnapshotAsyncClient getSnapshotClient();

   /**
    * Provides asynchronous access to Sessions
    */
   @Delegate
   SessionAsyncClient getSessionClient();
}
