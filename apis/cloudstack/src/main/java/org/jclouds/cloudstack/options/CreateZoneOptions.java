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

import org.jclouds.cloudstack.domain.AllocationState;
import org.jclouds.http.options.BaseHttpRequestOptions;
import org.jclouds.javax.annotation.Nullable;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control how a zone is created
 * 
 * @see <a href=
 *      "http://download.cloud.com/releases/2.2.0/api_2.2.12/global_admin/createZone.html"
 *      />
 * @author Andrei Savu
 */
public class CreateZoneOptions extends BaseHttpRequestOptions {

   public static final CreateZoneOptions NONE = new CreateZoneOptions();

   /**
    * @param allocationState
    *    allocation state of this Zone for allocation of new resources
    */
   public CreateZoneOptions allocationState(AllocationState allocationState) {
      this.queryParameters.replaceValues("allocationstate", ImmutableSet.of(allocationState.toString()));
      return this;
   }

   /**
    * @param dns2
    *    the second DNS for the Zone
    */
   public CreateZoneOptions dns2(String dns2) {
      this.queryParameters.replaceValues("dns2", ImmutableSet.of(dns2));
      return this;
   }

   /**
    * @param internalDns2
    *    the second internal DNS for the Zone
    */
   public CreateZoneOptions internalDns2(String internalDns2) {
      this.queryParameters.replaceValues("internaldns2", ImmutableSet.of(internalDns2));
      return this;
   }

   /**
    * @param domainName
    *    network domain name for the networks in zone
    */
   public CreateZoneOptions domainName(String domainName) {
      this.queryParameters.replaceValues("domain", ImmutableSet.of(domainName));
      return this;
   }

   /**
    * @param domainId
    *    the ID of the containing domain; null for public zones
    */
   public CreateZoneOptions domainId(@Nullable String domainId) {
      this.queryParameters.replaceValues("domainid", ImmutableSet.of(domainId + ""));
      return this;
   }

   /**
    * @param guestCIDRAddress
    *    the guest CIDR address for the Zone
    */
   public CreateZoneOptions guestCIDRAddress(String guestCIDRAddress) {
      this.queryParameters.replaceValues("guestcidraddress", ImmutableSet.of(guestCIDRAddress));
      return this;
   }

   /**
    * @param securityGroupEnabled
    *    true if network is security group enabled, false otherwise
    */
   public CreateZoneOptions securityGroupEnabled(boolean securityGroupEnabled) {
      this.queryParameters.replaceValues("securitygroupenabled", ImmutableSet.of(securityGroupEnabled + ""));
      return this;
   }

   /**
    * @param vlan
    *    the VLAN for the Zone
    */
   public CreateZoneOptions vlan(String vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan));
      return this;
   }

   public static class Builder {

      /**
       * @see CreateZoneOptions#allocationState
       */
      public static CreateZoneOptions allocationState(AllocationState allocationState) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.allocationState(allocationState);
      }

      /**
       * @see CreateZoneOptions#dns2
       */
      public static CreateZoneOptions dns2(String dns2) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.dns2(dns2);
      }

      /**
       * @see CreateZoneOptions#internalDns2
       */
      public static CreateZoneOptions internalDns2(String internalDns2) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.internalDns2(internalDns2);
      }

      /**
       * @see CreateZoneOptions#domainName
       */
      public static CreateZoneOptions domainName(String domainName) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.domainName(domainName);
      }

      /**
       * @see CreateZoneOptions#domainId
       */
      public static CreateZoneOptions domainId(@Nullable String domainId) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.domainId(domainId);
      }

      /**
       * @see CreateZoneOptions#guestCIDRAddress
       */
      public static CreateZoneOptions guestCIDRAddress(String guestCIDRAddress) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.guestCIDRAddress(guestCIDRAddress);
      }

      /**
       * @see CreateZoneOptions#securityGroupEnabled
       */
      public static CreateZoneOptions securityGroupEnabled(boolean securityGroupEnabled) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.securityGroupEnabled(securityGroupEnabled);
      }

      /**
       * @see CreateZoneOptions#vlan
       */
      public static CreateZoneOptions vlan(String vlan) {
         CreateZoneOptions options = new CreateZoneOptions();
         return options.vlan(vlan);
      }
   }
}
