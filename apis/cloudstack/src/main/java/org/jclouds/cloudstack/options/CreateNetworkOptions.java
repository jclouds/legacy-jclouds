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
package org.jclouds.cloudstack.options;

import com.google.common.collect.ImmutableSet;

/**
 * Options used to control what networks information is returned
 * 
 * @see <a
 *      href="http://download.cloud.com/releases/2.2.0/api/user/listNetworks.html"
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
    *           true if network is shared, false otherwise
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
    *           the ending IP address in the VLAN IP range
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
      public static CreateNetworkOptions accountInDomain(String account, long domain) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.accountInDomain(account, domain);
      }

      /**
       * @see CreateNetworkOptions#domainId
       */
      public static CreateNetworkOptions domainId(long domainId) {
         CreateNetworkOptions options = new CreateNetworkOptions();
         return options.domainId(domainId);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateNetworkOptions accountInDomain(String account, long domain) {
      return CreateNetworkOptions.class.cast(super.accountInDomain(account, domain));
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public CreateNetworkOptions domainId(long domainId) {
      return CreateNetworkOptions.class.cast(super.domainId(domainId));
   }
}
