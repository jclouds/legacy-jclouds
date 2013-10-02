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
package org.jclouds.cloudstack.options;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a zone is updated
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/updateZone.html"
 *      />
 * @author Andrei Savu
 */
public class UpdateZoneOptions extends BaseHttpRequestOptions {

   public static final UpdateZoneOptions NONE = new UpdateZoneOptions();

   /**
    * @param allocationState
    *    allocation state of this Zone for allocation of new resources
    */
   public UpdateZoneOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param details
    *    the details for the Zone
    */
   public UpdateZoneOptions details(String details) {
      this.queryParameters.replaceValues("details", ImmutableSet.of(details));
      return this;
   }

   /**
    * @param dhcpProvider
    *    the dhcp Provider for the Zone
    */
   public UpdateZoneOptions dhcpProvider(String dhcpProvider) {
      this.queryParameters.replaceValues("dhcpprovider", ImmutableSet.of(dhcpProvider));
      return this;
   }

   /**
    * @param externalDnsServers
    *    the list of external DNS servers
    */
   public UpdateZoneOptions externalDns(List<String> externalDnsServers) {
      checkArgument(externalDnsServers.size() == 1 || externalDnsServers.size() == 2,
         "The list of DNS servers should have 1 or 2 elements");
      this.queryParameters.replaceValues("dns1",
         ImmutableSet.of(externalDnsServers.get(0)));
      if (externalDnsServers.size() == 2) {
         this.queryParameters.replaceValues("dns2",
            ImmutableSet.of(externalDnsServers.get(1)));
      }
      return this;
   }

   /**
    * @param internalDnsServers
    *    the list of internal DNS for the Zone
    */
   public UpdateZoneOptions internalDns(List<String> internalDnsServers) {
      checkArgument(internalDnsServers.size() == 1 || internalDnsServers.size() == 2,
         "The list of internal DNS servers should have 1 or 2 elements");
      this.queryParameters.replaceValues("internaldns1",
         ImmutableSet.of(internalDnsServers.get(0)));
      if (internalDnsServers.size() == 2) {
         this.queryParameters.replaceValues("internaldns2",
            ImmutableSet.of(internalDnsServers.get(1)));
      }
      return this;
   }

   /**
    * @param dnsSearchOrder
    *    the dns search order list
    */
   public UpdateZoneOptions dnsSearchOrder(String dnsSearchOrder) {
      this.queryParameters.replaceValues("dnssearchorder", ImmutableSet.of(dnsSearchOrder));
      return this;
   }

   /**
    * @param domainName
    *    network domain name for the networks in zone
    */
   public UpdateZoneOptions domainName(String domainName) {
      this.queryParameters.replaceValues("domain", ImmutableSet.of(domainName));
      return this;
   }

   /**
    * @param guestCIDRAddress
    *    the guest CIDR address for the Zone
    */
   public UpdateZoneOptions guestCIDRAddress(String guestCIDRAddress) {
      this.queryParameters.replaceValues("guestcidraddress", ImmutableSet.of(guestCIDRAddress));
      return this;
   }

   /**
    * @param securityGroupEnabled
    *    true if network is security group enabled, false otherwise
    */
   public UpdateZoneOptions securityGroupEnabled(boolean securityGroupEnabled) {
      this.queryParameters.replaceValues("securitygroupenabled", ImmutableSet.of(securityGroupEnabled + ""));
      return this;
   }

   /**
    * You can only make a private Zone public, not the other way around
    */
   public UpdateZoneOptions makePublic() {
      this.queryParameters.replaceValues("ispublic", ImmutableSet.of("true"));
      return this;
   }

   /**
    * @param name
    *    the name of the Zone
    */
   public UpdateZoneOptions name(String name) {
      this.queryParameters.replaceValues("name", ImmutableSet.of(name));
      return this;
   }

   /**
    * @param vlan
    *    the VLAN for the Zone
    */
   public UpdateZoneOptions vlan(String vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan));
      return this;
   }

   public static class Builder {

      /**
       * @see UpdateZoneOptions#allocationState
       */
      public static UpdateZoneOptions allocationState(AllocationState allocationState) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.allocationState(allocationState);
      }

      /**
       * @see UpdateZoneOptions#details
       */
      public static UpdateZoneOptions details(String details) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.details(details);
      }

      /**
       * @see UpdateZoneOptions#dhcpProvider
       */
      public static UpdateZoneOptions dhcpProvider(String dhcpProvider) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.dhcpProvider(dhcpProvider);
      }

      /**
       * @see UpdateZoneOptions#externalDns
       */
      public static UpdateZoneOptions externalDns(List<String> externalDnsServers) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.externalDns(externalDnsServers);
      }

      /**
       * @see UpdateZoneOptions#internalDns
       */
      public static UpdateZoneOptions internalDns(List<String> internalDnsServers) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.internalDns(internalDnsServers);
      }

      /**
       * @see UpdateZoneOptions#dnsSearchOrder
       */
      public static UpdateZoneOptions dnsSearchOrder(String dnsSearchOrder) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.dnsSearchOrder(dnsSearchOrder);
      }

      /**
       * @see UpdateZoneOptions#domainName
       */
      public static UpdateZoneOptions domainName(String domainName) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.domainName(domainName);
      }

      /**
       * @see UpdateZoneOptions#guestCIDRAddress
       */
      public static UpdateZoneOptions guestCIDRAddress(String guestCIDRAddress) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.guestCIDRAddress(guestCIDRAddress);
      }

      /**
       * @see UpdateZoneOptions#securityGroupEnabled
       */
      public static UpdateZoneOptions securityGroupEnabled(boolean securityGroupEnabled) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.securityGroupEnabled(securityGroupEnabled);
      }

      /**
       * @see UpdateZoneOptions#makePublic
       */
      public static UpdateZoneOptions makePublic() {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.makePublic();
      }

      /**
       * @see UpdateZoneOptions#name
       */
      public static UpdateZoneOptions name(String name) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.name(name);
      }

      /**
       * @see UpdateZoneOptions#vlan
       */
      public static UpdateZoneOptions vlan(String vlan) {
         UpdateZoneOptions options = new UpdateZoneOptions();
         return options.vlan(vlan);
      }
   }
}
