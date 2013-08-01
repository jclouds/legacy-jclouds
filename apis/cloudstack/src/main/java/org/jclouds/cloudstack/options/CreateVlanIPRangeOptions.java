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

import com.google.common.collect.ImmutableSet;

/**
 * Options to the createVlanIPRange API call.
 *
 * @author Richard Downer
 */
public class CreateVlanIPRangeOptions extends AccountInDomainOptions {

   public static class Builder {

      public static CreateVlanIPRangeOptions accountInDomain(String account, String domain) {
         return new CreateVlanIPRangeOptions().accountInDomain(account, domain);
      }

      public static CreateVlanIPRangeOptions domainId(String domainId) {
         return new CreateVlanIPRangeOptions().domainId(domainId);
      }

      public static CreateVlanIPRangeOptions forVirtualNetwork(boolean forVirtualNetwork) {
         return new CreateVlanIPRangeOptions().forVirtualNetwork(forVirtualNetwork);
      }

      public static CreateVlanIPRangeOptions zoneId(String zoneId) {
         return new CreateVlanIPRangeOptions().zoneId(zoneId);
      }

      public static CreateVlanIPRangeOptions vlan(long vlan) {
         return new CreateVlanIPRangeOptions().vlan(vlan);
      }

      public static CreateVlanIPRangeOptions vlan(String vlan) {
         return new CreateVlanIPRangeOptions().vlan(vlan);
      }

      public static CreateVlanIPRangeOptions podId(String podId) {
         return new CreateVlanIPRangeOptions().podId(podId);
      }

      public static CreateVlanIPRangeOptions gateway(String gateway) {
         return new CreateVlanIPRangeOptions().gateway(gateway);
      }

      public static CreateVlanIPRangeOptions netmask(String netmask) {
         return new CreateVlanIPRangeOptions().netmask(netmask);
      }

      public static CreateVlanIPRangeOptions networkId(String networkId) {
         return new CreateVlanIPRangeOptions().networkId(networkId);
      }

      public static CreateVlanIPRangeOptions projectId(String projectId) {
         return new CreateVlanIPRangeOptions().projectId(projectId);
      }

   }

   @Override
   public CreateVlanIPRangeOptions accountInDomain(String account, String domain) {
      return (CreateVlanIPRangeOptions) super.accountInDomain(account, domain);
   }

   @Override
   public CreateVlanIPRangeOptions domainId(String domainId) {
      return (CreateVlanIPRangeOptions) super.domainId(domainId);
   }

   public CreateVlanIPRangeOptions forVirtualNetwork(boolean forVirtualNetwork) {
      this.queryParameters.replaceValues("forvirtualnetwork", ImmutableSet.of(forVirtualNetwork+""));
      return this;
   }

   public CreateVlanIPRangeOptions zoneId(String zoneId) {
      this.queryParameters.replaceValues("zoneid", ImmutableSet.of(zoneId+""));
      return this;
   }

   public CreateVlanIPRangeOptions vlan(long vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan+""));
      return this;
   }

   public CreateVlanIPRangeOptions vlan(String vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan));
      return this;
   }

   public CreateVlanIPRangeOptions podId(String podId) {
      this.queryParameters.replaceValues("podid", ImmutableSet.of(podId+""));
      return this;
   }

   public CreateVlanIPRangeOptions gateway(String gateway) {
      this.queryParameters.replaceValues("gateway", ImmutableSet.of(gateway));
      return this;
   }

   public CreateVlanIPRangeOptions netmask(String netmask) {
      this.queryParameters.replaceValues("netmask", ImmutableSet.of(netmask));
      return this;
   }

   public CreateVlanIPRangeOptions networkId(String networkId) {
      this.queryParameters.replaceValues("networkid", ImmutableSet.of(networkId+""));
      return this;
   }

   public CreateVlanIPRangeOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId+""));
      return this;
   }
}
