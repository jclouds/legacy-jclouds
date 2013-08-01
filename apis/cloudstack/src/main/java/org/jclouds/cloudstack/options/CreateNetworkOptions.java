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
 * Optional fields for network creation
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api_2.2.12/user/createNetwork.html"
 *      />
 * @author Adrian Cole
 */
public class CreateNetworkOptions extends AccountInDomainOptions {

   public static final CreateNetworkOptions NONE = new CreateNetworkOptions();

   /**
    * @param isDefault
    *           true if network is default, false otherwise
    */
   public CreateNetworkOptions isDefault(boolean isDefault) {
      this.queryParameters.replaceValues("isdefault", ImmutableSet.of(isDefault + ""));
      return this;
   }

   /**
    * @param isShared
    *           true if network is shared across accounts in the Zone
    */
   public CreateNetworkOptions isShared(boolean isShared) {
      this.queryParameters.replaceValues("isshared", ImmutableSet.of(isShared + ""));
      return this;
   }

   /**
    * @param startIP
    *           the beginning IP address in the VLAN IP range
    */
   public CreateNetworkOptions startIP(String startIP) {
      this.queryParameters.replaceValues("startip", ImmutableSet.of(startIP));
      return this;
   }

   /**
    * @param endIP
    *           the ending IP address in the network IP range. If not specified, will be defaulted to startIP
    */
   public CreateNetworkOptions endIP(String endIP) {
      this.queryParameters.replaceValues("endip", ImmutableSet.of(endIP));
      return this;
   }

   /**
    * @param gateway
    *           the gateway of the VLAN IP range
    */
   public CreateNetworkOptions gateway(String gateway) {
      this.queryParameters.replaceValues("gateway", ImmutableSet.of(gateway));
      return this;
   }

   /**
    * @param netmask
    *           the netmask of the VLAN IP range
    */
   public CreateNetworkOptions netmask(String netmask) {
      this.queryParameters.replaceValues("netmask", ImmutableSet.of(netmask));
      return this;
   }

   /**
    * @param networkDomain
    *           network domain
    */
   public CreateNetworkOptions networkDomain(String networkDomain) {
      this.queryParameters.replaceValues("networkdomain", ImmutableSet.of(networkDomain));
      return this;
   }

   /**
    * @param vlan
    *           the ID or VID of the VLAN. Default is an "untagged" VLAN.
    */
   public CreateNetworkOptions vlan(String vlan) {
      this.queryParameters.replaceValues("vlan", ImmutableSet.of(vlan));
      return this;
   }

   /**
    * @param projectId
    *          the project this network will be in.
    */
   public CreateNetworkOptions projectId(String projectId) {
      this.queryParameters.replaceValues("projectid", ImmutableSet.of(projectId + ""));
      return this;
   }

   public static class Builder {
      /**
       * @see CreateNetworkOptions#isDefault
       */
      public static CreateNetworkOptions isDefault(boolean isDefault) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.isDefault(isDefault);
      }

      /**
       * @see CreateNetworkOptions#isShared
       */
      public static CreateNetworkOptions isShared(boolean isShared) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.isShared(isShared);
      }

      /**
       * @see CreateNetworkOptions#startIP(String)
       */
      public static CreateNetworkOptions startIP(String startIP) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.startIP(startIP);
      }

      /**
       * @see CreateNetworkOptions#endIP(String)
       */
      public static CreateNetworkOptions endIP(String endIP) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.endIP(endIP);
      }

      /**
       * @see CreateNetworkOptions#gateway(String)
       */
      public static CreateNetworkOptions gateway(String gateway) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.gateway(gateway);
      }

      /**
       * @see CreateNetworkOptions#netmask(String)
       */
      public static CreateNetworkOptions netmask(String netmask) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.netmask(netmask);
      }

      /**
       * @see CreateNetworkOptions#networkDomain(String)
       */
      public static CreateNetworkOptions networkDomain(String networkDomain) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.networkDomain(networkDomain);
      }

      /**
       * @see CreateNetworkOptions#vlan(String)
       */
      public static CreateNetworkOptions vlan(String vlan) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.vlan(vlan);
      }

      /**
       * @see CreateNetworkOptions#accountInDomain
       */
      public static CreateNetworkOptions accountInDomain(String account, String domain) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see CreateNetworkOptions#domainId
       */
      public static CreateNetworkOptions domainId(String domainId) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.domainId(domainId);
      }

      /**
       * @see CreateNetworkOptions#projectId(String)
       */
      public static CreateNetworkOptions projectId(String projectId) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.projectId(projectId);
      }
   }

   /**
    * Specify the account that will own the network. This can be run by a privileged user to be
    * able to set advanced network properties, such as the VLAN tag, and then to immediately pass
    * ownership of the network to an unprivileged user.
    *
    * Note that the unprivileged user will be able to delete the network later, since they are it's owner.
    *
    * @param account
    *           account name
    * @param domain
    *           domain ID
    */
   @Override
   public CreateNetworkOptions accountInDomain(String account, String domain) {
      return CreateNetworkOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * Specify the domain that will own the network. Any user in the domain can then use this
    * network.
    *
    * CloudStack requires that when using this option, you also specify isShared(true).
    *
    * Changes or deletions to this network must be done by a domain admin in the same domain, or a
    * global admin.
    * 
    * @param domainId
    *           domain ID
    */
   @Override
   public CreateNetworkOptions domainId(String domainId) {
      return CreateNetworkOptions.class.cast(super.domainId(domainId));
   }
}
